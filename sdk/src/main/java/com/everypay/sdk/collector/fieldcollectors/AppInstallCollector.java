package com.everypay.sdk.collector.fieldcollectors;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.everypay.sdk.collector.FieldError;
import com.everypay.sdk.collector.InfoField;

import java.util.UUID;

public class AppInstallCollector implements FieldCollector {

    private static final String PREFS_FILE = "everypay.xml";
    private static final String PREFS_KEY = "install";

    @Override
    public InfoField getField(Context context) {
        try {
            return new InfoField("app_install_token", getPersistentInstallToken(context));
        } catch (SecurityException e) {
            return new InfoField("app_install_token", FieldError.PERMISSIONS);
        }
    }

    private static String getPersistentInstallToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String token = prefs.getString(PREFS_KEY, null);
        if (TextUtils.isEmpty(token)) {
            SharedPreferences.Editor edit = prefs.edit();
            token = UUID.randomUUID().toString();
            edit.putString(PREFS_KEY, token).commit();
        }
        return token;
    }
}
