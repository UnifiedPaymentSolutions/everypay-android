
package com.everypay.sdk.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Some methods to make DialogFragments safer to use
 */
public class DialogUtil {

    private static ArrayList<PendingDialogAction> pendingDialogActions = new ArrayList<>();

    public static void executePendingActionsIfAny(final FragmentActivity activity) {
        if (activity == null || activity.isFinishing()) {
            return; // No need
        }
        Log.getInstance(DialogUtil.class).d("executePendingActionsIfAny called for " + activity.getClass().getSimpleName());
        synchronized (pendingDialogActions) {
            final Iterator<PendingDialogAction> iter = pendingDialogActions.iterator();
            while (iter.hasNext()) {
                final PendingDialogAction action = iter.next();
                if (!action.isForActivity(activity)) {
                    // If not the same activity anymore then forget about it!
                    iter.remove();
                    Log.getInstance(DialogUtil.class).d("executePendingActionsIfAny: Ignoring " + action.tag + ", not for this activity!");
                    continue;
                }
                Log.getInstance(DialogUtil.class).d("executePendingActionsIfAny: Retrying " + action.tag);
                // Retry
                showDialogFragment(activity, action.fragment, action.tag, action.targetFragment, 1, false);

                // Forget
                iter.remove();
            }
        }
    }

    private static void addRetryForResume(final FragmentActivity activity, final DialogFragment dialog, final String tag, final Fragment targetFragment) {
        if (activity == null || dialog == null || TextUtils.isEmpty(tag)) {
            return;
        }

        synchronized (pendingDialogActions) {
            final PendingDialogAction newAction = new PendingDialogAction(activity, dialog, tag, targetFragment);
            for (PendingDialogAction pendingDialogAction : pendingDialogActions) {
                if (pendingDialogAction.isSameAs(newAction)) {
                    pendingDialogAction.fragment = newAction.fragment;
                    Log.getInstance(DialogUtil.class).d("addRetryForResume: already there, updating " + tag);
                    return; // Already here, lets just update the content
                }
            }
            Log.getInstance(DialogUtil.class).d("addRetryForResume: " + tag);
            pendingDialogActions.add(newAction);
        }
    }

    private static boolean removeRetryForResume(final FragmentActivity activity, final String tag) {
        if (activity == null || TextUtils.isEmpty(tag)) {
            return false;
        }

        synchronized (pendingDialogActions) {
            final Iterator<PendingDialogAction> iter = pendingDialogActions.iterator();
            while (iter.hasNext()) {
                final PendingDialogAction action = iter.next();
                if (action.isForActivity(activity) && TextUtils.equals(action.tag, tag)) {
                    Log.getInstance(DialogUtil.class).d("removeRetryForResume: canceling " + tag);
                    iter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public static void removeAllPendingActionsIfAny() {
        synchronized (pendingDialogActions) {
            pendingDialogActions.clear();
        }
    }

    public static void showDialogFragment(final DialogFragment dialog, final String tag, final Fragment targetFragment) {
        showDialogFragment(targetFragment.getActivity(), dialog, tag, targetFragment, 3, true);
    }

    public static void showDialogFragment(final FragmentActivity activity, final DialogFragment dialog, final String tag, final Fragment targetFragment) {
        showDialogFragment(activity, dialog, tag, targetFragment, 3, true);
    }

    public static void showDialogFragment(final FragmentActivity activity, final DialogFragment dialog, final String tag, final Fragment targetFragment, final int retryCount, final boolean retryOnResume) {
        if (activity == null || dialog == null || TextUtils.isEmpty(tag)) {
            Log.getInstance(DialogUtil.class).e("showDialogFragment: activity == null || dialog == null || TextUtils.isEmpty(tag)");
            // Fail
            return;
        }

        if (activity.isFinishing()) {
            // No need
            Log.getInstance(DialogUtil.class).w("showDialogFragment: activity.isFinishing()");
            return;
        }

        try {
            final FragmentTransaction ft = targetFragment != null ? targetFragment.getFragmentManager().beginTransaction() : activity.getSupportFragmentManager().beginTransaction();
            removePreviousFragmentIfAny(activity, ft, tag);
            // Create and show the dialog.
            if (targetFragment != null) {
                dialog.setTargetFragment(targetFragment, 0);
            }
            dialog.show(ft, tag);
        } catch (IllegalStateException e) {
            Log.getInstance(DialogUtil.class).e("showDialogFragment:", e);
            if (retryCount > 0) {
                Log.getInstance(DialogUtil.class).d("showDialogFragment: will retry");
                final Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        showDialogFragment(activity, dialog, tag, targetFragment, retryCount - 1, retryOnResume);
                    }
                }, 800l);
            } else if (retryCount == 0 && retryOnResume) {
                addRetryForResume(activity, dialog, tag, targetFragment);
            }
        }

    }

    public static void dismissDialogFragment(final Fragment fragment, final String tag) {
        dismissDialogFragment(fragment.getActivity(), tag);
    }

    public static void dismissDialogFragment(final FragmentActivity activity, final String tag) {
        dismissDialogFragment(activity, tag, 3);
    }

    public static void dismissDialogFragment(final FragmentActivity activity, final String tag, final int retryCount) {
        try {
            final DialogFragment fragment = (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                fragment.dismissAllowingStateLoss();
                Log.getInstance(DialogUtil.class).d("dismissDialogFragment: dismissed");
            } else if (removeRetryForResume(activity, tag)) {
                // The dialog was waiting to be shown, it is not anymore, we are done
                Log.getInstance(DialogUtil.class).d("dismissDialogFragment: canceled the dialog showing");
            } else {
                if (retryCount > 0) {
                    Log.getInstance(DialogUtil.class).d("dismissDialogFragment: will retry");
                    final Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            dismissDialogFragment(activity, tag, retryCount - 1);
                        }
                    }, 800l);
                }
            }
        } catch (IllegalStateException e) {
            Log.getInstance(DialogUtil.class).e("dismissDialogFragment:", e);
            if (retryCount > 0) {
                final Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialogFragment(activity, tag, retryCount - 1);
                    }
                }, 800l);
            }
        }
    }

    protected static void removePreviousFragmentIfAny(final FragmentActivity activity, final FragmentTransaction ft, final String tag) {
        if (activity == null || ft == null || TextUtils.isEmpty(tag)) {
            return;
        }
        final Fragment previousFragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (previousFragment != null) {
            // Remove the previous one?
            ft.remove(previousFragment);
        }
    }

    public static boolean isShowing(final FragmentActivity activity, final String tag) {
        return activity != null && activity.getSupportFragmentManager().findFragmentByTag(tag) != null;
    }

    private static class PendingDialogAction {
        public String tag;
        public DialogFragment fragment;
        public Fragment targetFragment;
        public Class activityClazz;

        public PendingDialogAction(Activity activity, DialogFragment fragment, String tag, Fragment targetFragment) {
            this.activityClazz = activity.getClass();
            this.tag = tag;
            this.fragment = fragment;
            this.targetFragment = targetFragment;
        }

        public boolean isSameAs(PendingDialogAction action) {
            return action != null && TextUtils.equals(this.getId(), action.getId());
        }

        public String getId() {
            return tag  + "_" + targetFragment + "_" + activityClazz;
        }

        public boolean isForActivity(final Activity activity) {
            return !(activity == null || this.activityClazz == null) && activityClazz.equals(activity.getClass());
        }
    }
}

