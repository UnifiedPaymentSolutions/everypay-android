package com.everypay.sdk.deviceinfo;


import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.deviceinfo.fieldcollectors.AndroidIdCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.AppInstallCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.GpsCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.HardwareCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.NetworkMacsCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.OsCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.WifiMacCollector;
import com.everypay.sdk.util.CustomGson;

public class DeviceCollector {

    public static long DEFAULT_TIMEOUT_MILLIS = 5000;

    Context context;
    Handler handler;
    GpsCollector gpsCollector;
    long startMillis;

    public DeviceCollector(Context context) {
        this.context = context.getApplicationContext();
        this.handler = new Handler();
    }

    public synchronized void start() {
        startMillis = SystemClock.elapsedRealtime();
        gpsCollector = new GpsCollector(context);
        gpsCollector.start();
    }

    public synchronized void collectWithTimeout(final DeviceInfoListener listener, long timeout) {
        long remainingTimeout = Math.max(0, startMillis - SystemClock.elapsedRealtime() + timeout);
        Log.d(EveryPay.TAG, "Remaining timeout " + remainingTimeout + "ms");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DeviceInfo result = new DeviceInfo();
                result.androidId = new AndroidIdCollector().getField(context);
                result.hardware = new HardwareCollector().getField(context);
                result.os = new OsCollector().getField(context);
                result.appInstallToken = new AppInstallCollector().getField(context);
                result.wifiMac = new WifiMacCollector().getField(context);
                result.netMacs = new NetworkMacsCollector().getField(context);

                result.gps = gpsCollector.stopAndGetInfoField();

                if (listener != null) {
                    listener.deviceInfoCollected(CustomGson.getInstance().toJson(result));
                }
            }
        }, remainingTimeout);
    }

    public synchronized void collectWithDefaultTimeout(DeviceInfoListener listener) {
        collectWithTimeout(listener, DEFAULT_TIMEOUT_MILLIS);
    }

    public synchronized void cancel() {
        if (gpsCollector != null)
            gpsCollector.stopAndGetInfoField();
    }

    public interface DeviceInfoListener {
        void deviceInfoCollected(String deviceInfo);
    }
}
