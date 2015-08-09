package com.everypay.sdk.collector.fieldcollectors;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.everypay.sdk.collector.FieldError;
import com.everypay.sdk.collector.InfoField;

public class WifiMacCollector implements FieldCollector {

    @Override
    public InfoField getField(Context context) {
        try {
            WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if (wm == null)
                throw new SecurityException("WifiManager is null.");

            WifiInfo wifi = wm.getConnectionInfo();
            if (wifi != null) {
                String mac = wifi.getMacAddress();
                if (!TextUtils.isEmpty(mac)) {
                    return new InfoField("wifi_mac", mac.replace(":", ""));
                } else {
                    return new InfoField("wifi_mac", FieldError.UNAVAILABLE);
                }
            } else {
                return new InfoField("wifi_mac", FieldError.UNAVAILABLE);
            }
        } catch (SecurityException e) {
            return new InfoField("wifi_mac", FieldError.PERMISSIONS);
        }
    }
}
