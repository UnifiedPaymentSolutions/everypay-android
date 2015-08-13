package com.everypay.sdk.deviceinfo;


import android.content.Context;

import com.everypay.sdk.deviceinfo.fieldcollectors.AndroidIdCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.AppInstallCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.FieldCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.HardwareCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.NetworkMacsCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.OsCollector;
import com.everypay.sdk.deviceinfo.fieldcollectors.WifiMacCollector;
import com.everypay.sdk.util.CustomGson;

import java.util.ArrayList;
import java.util.List;

public class DeviceCollector {

    Context context;
    List<FieldCollector> fieldCollectors;
    CollectorThread thread;
    volatile String result;

    public DeviceCollector(Context context) {
        this.context = context.getApplicationContext();
        this.fieldCollectors = new ArrayList<>();
        fieldCollectors.add(new AndroidIdCollector());
        fieldCollectors.add(new HardwareCollector());
        fieldCollectors.add(new OsCollector());
        fieldCollectors.add(new AppInstallCollector());
        fieldCollectors.add(new WifiMacCollector());
        fieldCollectors.add(new NetworkMacsCollector());
        this.result = null;
        this.thread = new CollectorThread(this);
    }

    public synchronized void start() {
        if (result == null)
            thread.start();
    }

    public synchronized String collectWithTimeout() {
        while (result == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        return result;
    }

    private static class CollectorThread extends Thread {
        DeviceCollector collector;

        CollectorThread(DeviceCollector collector) {
            this.collector = collector;
        }

        @Override
        public void run() {
            Context context = collector.context;
            DeviceInfo result = new DeviceInfo();
            result.androidId = new AndroidIdCollector().getField(context);
            result.hardware = new HardwareCollector().getField(context);
            result.os = new OsCollector().getField(context);
            result.appInstallToken = new AppInstallCollector().getField(context);
            result.wifiMac = new WifiMacCollector().getField(context);
            result.netMacs = new NetworkMacsCollector().getField(context);
            collector.result = CustomGson.getInstance().toJson(result);
        }
    }
}
