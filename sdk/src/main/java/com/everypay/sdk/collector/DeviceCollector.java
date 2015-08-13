package com.everypay.sdk.collector;


import android.content.Context;

import com.everypay.sdk.collector.fieldcollectors.AndroidIdCollector;
import com.everypay.sdk.collector.fieldcollectors.AppInstallCollector;
import com.everypay.sdk.collector.fieldcollectors.FieldCollector;
import com.everypay.sdk.collector.fieldcollectors.HardwareCollector;
import com.everypay.sdk.collector.fieldcollectors.NetworkMacsCollector;
import com.everypay.sdk.collector.fieldcollectors.OsCollector;
import com.everypay.sdk.collector.fieldcollectors.WifiMacCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceCollector {

    Context context;
    List<FieldCollector> fieldCollectors;
    CollectorThread thread;
    volatile Map<String, Object> result;

    public DeviceCollector(Context context) {
        this.context = context.getApplicationContext();
        this.fieldCollectors = new ArrayList<>();
        fieldCollectors.add(new AndroidIdCollector());
        fieldCollectors.add(new HardwareCollector());
        fieldCollectors.add(new OsCollector());
        fieldCollectors.add(new AppInstallCollector());
        fieldCollectors.add(new WifiMacCollector());
        fieldCollectors.add(new NetworkMacsCollector());
        this.thread = new CollectorThread(this);
    }

    public synchronized void start() {
        if (result != null)
            thread.start();
    }

    public synchronized Map<String, Object> collectWithTimeout() {
        while (result != null) {
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
            Map<String, Object> result = new HashMap<>();
            for (FieldCollector fieldCollector : collector.fieldCollectors) {
                InfoField field = fieldCollector.getField(collector.context);
                result.put(field.name, field);
            }
            collector.result = result;
        }
    }
}
