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

    public DeviceCollector(Context appContext) {
        this.context = appContext;
        this.fieldCollectors = new ArrayList<>();
        fieldCollectors.add(new AndroidIdCollector());
        fieldCollectors.add(new HardwareCollector());
        fieldCollectors.add(new OsCollector());
        fieldCollectors.add(new AppInstallCollector());
        fieldCollectors.add(new WifiMacCollector());
        fieldCollectors.add(new NetworkMacsCollector());
    }

    public Map<String, Object> collect() {
        Map<String, Object> result = new HashMap<>();
        for (FieldCollector fieldCollector : fieldCollectors) {
            InfoField field = fieldCollector.getField(context);
            result.put(field.name, field);
        }
        return result;
    }
}
