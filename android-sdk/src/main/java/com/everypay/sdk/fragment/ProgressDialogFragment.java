/**
 * Copyright 2016 Cybernetica AS
 * All rights reserved.
 * The usage of this code is subject to appropriate license agreement.
 * Please contact info@cyber.ee for licensing.
 */
package com.everypay.sdk.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.everypay.sdk.R;


/**
 * Created by Harri Kirik (harri35@gmail.com).
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String ARG_KEEP_SCREEN_ON = "ee.cyber.smartid.app.ARG_KEEP_SCREEN_ON";

    public static ProgressDialogFragment newInstance() {
        return newInstance(false);
    }

    public static ProgressDialogFragment newInstance(boolean keepScreenOn) {
        final ProgressDialogFragment fragment = new ProgressDialogFragment();
        final Bundle args = new Bundle();
        args.putBoolean(ARG_KEEP_SCREEN_ON, keepScreenOn);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean getKeepScreenOn() {
        return getArguments().getBoolean(ARG_KEEP_SCREEN_ON);
    }

    public ProgressDialogFragment() {
        super();
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ep_Theme_AppCompat_Translucent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.ep_progress_dialog_fragment_layout, container, false);
        view.setKeepScreenOn(getKeepScreenOn());
        return view;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        final ProgressDialogFragmentListener listener = getListener();
        if (listener != null) {
            listener.onProgressDialogFragmentCanceled(getTargetRequestCode());
        }
    }

    private ProgressDialogFragmentListener getListener() {
        if (getTargetFragment() instanceof ProgressDialogFragmentListener) {
            return (ProgressDialogFragmentListener) getTargetFragment();
        } else if (getActivity() instanceof ProgressDialogFragmentListener) {
            return (ProgressDialogFragmentListener) getActivity();
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    public interface ProgressDialogFragmentListener {
        void onProgressDialogFragmentCanceled(int requestCode);
    }
}
