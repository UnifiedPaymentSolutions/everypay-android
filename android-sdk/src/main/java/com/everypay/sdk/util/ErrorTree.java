
package com.everypay.sdk.util;

import android.util.Log;

import timber.log.Timber;

/**
 *  Release logging Tree. Only logs ERROR and ASSERT priority.
 */
public class ErrorTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO || priority == Log.WARN) {
            return;
        }
        Log.println(priority,tag, message);
    }
}
