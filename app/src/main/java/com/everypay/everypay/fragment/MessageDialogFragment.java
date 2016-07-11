
package com.everypay.everypay.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.everypay.everypay.R;


/**
 * Basic "Yes/What?/No" dialog
 * Created by Harri Kirik (harri35@gmail.com).
 */
public class MessageDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "ee.cyber.smartid.app.ARG_TITLE";
    private static final String ARG_MESSAGE = "ee.cyber.smartid.app.ARG_MESSAGE";
    private static final String ARG_BUTTON_POS = "ee.cyber.smartid.app.ARG_BUTTON_POS";
    private static final String ARG_BUTTON_NEG = "ee.cyber.smartid.app.ARG_BUTTON_NEG";
    private static final String ARG_BUTTON_NEU = "ee.cyber.smartid.app.ARG_BUTTON_NEU";
    private static final String ARG_EXTRAS = "ee.cyber.smartid.app.ARG_EXTRAS";

    public static final int BUTTON_POSITIVE = 0;
    public static final int BUTTON_NEGATIVE = 1;
    public static final int BUTTON_NEUTRAL = 2;


    public static MessageDialogFragment newInstance(final String title, final CharSequence message, final String posbutton) {
        return newInstance(title, message, posbutton, null, null, null);
    }

    public static MessageDialogFragment newInstance(final String title, final CharSequence message, final String posbutton, final String negButton, final String neutralButton) {
        return newInstance(title, message, posbutton, negButton, neutralButton, null);
    }

    public static MessageDialogFragment newInstance(final String title, final CharSequence message, final String posbutton, final String negButton, final String neutralButton, final Bundle extras) {
        final Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putCharSequence(ARG_MESSAGE, message);
        args.putString(ARG_BUTTON_POS, posbutton);
        args.putString(ARG_BUTTON_NEG, negButton);
        args.putString(ARG_BUTTON_NEU, neutralButton);
        args.putBundle(ARG_EXTRAS, extras);
        final MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MessageDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getArguments().getString(ARG_TITLE));
        builder.setMessage(getArguments().getCharSequence(ARG_MESSAGE));

        if (getArguments().getString(ARG_BUTTON_POS) != null) {
            builder.setPositiveButton(getArguments().getString(ARG_BUTTON_POS), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (getListener() != null) {
                        getListener().onMessageDialogFragmentButtonPressed(getTargetRequestCode(), BUTTON_POSITIVE, getExtras());
                    }
                }
            });
        }

        if (getArguments().getString(ARG_BUTTON_NEG) != null) {
            builder.setNegativeButton(getArguments().getString(ARG_BUTTON_NEG), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (getListener() != null) {
                        getListener().onMessageDialogFragmentButtonPressed(getTargetRequestCode(), BUTTON_NEGATIVE, getExtras());
                    }
                }
            });

        }

        if (getArguments().getString(ARG_BUTTON_NEU) != null) {
            builder.setNeutralButton(getArguments().getString(ARG_BUTTON_NEU), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (getListener() != null) {
                        getListener().onMessageDialogFragmentButtonPressed(getTargetRequestCode(), BUTTON_NEUTRAL, getExtras());
                    }
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final TextView textMessage = ((TextView) ((AlertDialog) dialog).findViewById(android.R.id.message));
                if (textMessage != null) {
                    textMessage.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        });

        return dialog;
    }

    private Bundle getExtras() {
        return getArguments().getBundle(ARG_EXTRAS);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getListener() != null) {
            getListener().onMessageDialogFragmentCanceled(getTargetRequestCode(), getExtras());
        }
    }

    private MessageDialogFragmentListener getListener() {
        if (getTargetFragment() instanceof MessageDialogFragmentListener) {
            return (MessageDialogFragmentListener) getTargetFragment();
        } else if (getActivity() instanceof MessageDialogFragmentListener) {
            return (MessageDialogFragmentListener) getActivity();
        }
        return null;
    }

    public interface MessageDialogFragmentListener {
        void onMessageDialogFragmentButtonPressed(int requestCode, int buttonId, Bundle extras);

        void onMessageDialogFragmentCanceled(int requestCode, Bundle extras);
    }
}