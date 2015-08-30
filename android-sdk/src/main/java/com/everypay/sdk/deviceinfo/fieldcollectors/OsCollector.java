package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;
import android.os.Build;

import com.everypay.sdk.deviceinfo.FieldError;
import com.everypay.sdk.deviceinfo.InfoField;

public class OsCollector implements FieldCollector {

    @Override
    public InfoField getField(Context context) {
        try {
            return new InfoField("os", new OsData());
        } catch (SecurityException e) {
            return new InfoField("os", FieldError.PERMISSIONS);
        }
    }

    public static class OsData {
        String os;
        int versionSdk;
        String versionName;
        String display;
        String bootloader;
        String fingerprint;
        String id;
        String tags;

        public OsData() {
            this.os = "Android";
            this.versionSdk = Build.VERSION.SDK_INT;
            this.versionName = Build.VERSION.RELEASE;

            this.display = Build.DISPLAY;
            this.bootloader = Build.BOOTLOADER;
            this.fingerprint = Build.FINGERPRINT;
            this.id = Build.ID;
            this.tags = Build.TAGS;
        }
    }
}
