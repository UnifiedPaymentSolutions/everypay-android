package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;
import android.provider.Settings;

import com.everypay.sdk.deviceinfo.FieldError;
import com.everypay.sdk.deviceinfo.InfoField;

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
