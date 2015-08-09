package com.everypay.sdk.collector.fieldcollectors;


import android.content.Context;
import android.provider.Settings;

import com.everypay.sdk.collector.FieldError;
import com.everypay.sdk.collector.InfoField;

public class AndroidIdCollector implements FieldCollector {

    @Override
    public InfoField getField(Context context) {
        try {
            return new InfoField("android_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (SecurityException e) {
            return new InfoField("android_id", FieldError.PERMISSIONS);
        }
    }
}
