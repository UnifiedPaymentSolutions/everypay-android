package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.everypay.sdk.deviceinfo.FieldError;
import com.everypay.sdk.deviceinfo.InfoField;

import java.util.UUID;

public class AppInstallCollector implements FieldCollector {

    private static final String APP_INTALL_TOKEN = "app_install_token";
    private static final String PREFS_FILE = "everypay.xml";
    private static final String PREFS_KEY = "install";

    @Override
    public InfoField getField(Context context) {
        try {
            return new InfoField(APP_INTALL_TOKEN, getPersistentInstallToken(context));
        } catch (SecurityException e) {
            return new InfoField(APP_INTALL_TOKEN, FieldError.PERMISSIONS);
        }
    }

    private static String getPersistentInstallToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String token = prefs.getString(PREFS_KEY, null);
        if (TextUtils.isEmpty(token)) {
            SharedPreferences.Editor edit = prefs.edit();
            token = UUID.randomUUID().toString();
            edit.putString(PREFS_KEY, token).apply();
        }
        return token;
    }
}
