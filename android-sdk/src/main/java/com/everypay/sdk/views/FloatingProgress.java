package com.everypay.sdk.views;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.everypay.sdk.R;


public class FloatingProgress extends Dialog {

    public FloatingProgress(Context context) {
        super(context, R.style.ep_FloatingProgressTheme);
    }

    /**
     * Not cancelable.
     */
    public static FloatingProgress show(Context context) {
        return show(context, false, null);
    }

    /**
     * Cancelable.
     */
    public static FloatingProgress show(Context context, OnCancelListener cancelListener) {
        return show(context, true, cancelListener);
    }

    public static FloatingProgress show(Context context, boolean cancelable, OnCancelListener cancelListener) {
        FloatingProgress dialog = new FloatingProgress(context);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        // Add ProgressBar to the dialog
        ProgressBar bar = new ProgressBar(context);
        bar.setIndeterminate(true);
        dialog.addContentView(bar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
        return dialog;
    }
}
