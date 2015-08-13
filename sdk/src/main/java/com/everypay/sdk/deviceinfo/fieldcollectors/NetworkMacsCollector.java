package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.everypay.sdk.deviceinfo.FieldError;
import com.everypay.sdk.deviceinfo.InfoField;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

public class NetworkMacsCollector implements FieldCollector {

    @Override
    public InfoField getField(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null)
                throw new SecurityException("ConnectivityManager is null.");

            HashMap<String, String> networkNameToMac = new HashMap<>();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface inter = interfaces.nextElement();
                    if (inter.isUp() && !TextUtils.isEmpty(inter.getName())) {
                        byte[] binaryMac = inter.getHardwareAddress();
                        if (binaryMac != null && binaryMac.length > 0) {
                            StringBuilder strMac = new StringBuilder();
                            for (byte b : binaryMac) {
                                strMac.append(String.format("%02x", ((int)b) & 0xFF));
                            }
                            networkNameToMac.put(inter.getName(), strMac.toString());
                        }
                    }
                }
            }

            if (!networkNameToMac.isEmpty()) {
                return new InfoField("net_macs", networkNameToMac);
            } else {
                return new InfoField("net_macs", FieldError.UNAVAILABLE);
            }
        } catch (SecurityException e) {
            return new InfoField("net_macs", FieldError.PERMISSIONS);
        } catch (SocketException e) {
            return new InfoField("net_macs", FieldError.UNAVAILABLE);
        }
    }
}
