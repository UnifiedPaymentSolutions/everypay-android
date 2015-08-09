package com.everypay.sdk.collector.fieldcollectors;


import android.content.Context;
import android.os.Build;

import com.everypay.sdk.collector.FieldError;
import com.everypay.sdk.collector.InfoField;

public class HardwareCollector implements FieldCollector {

    @Override
    public InfoField getField(Context context) {
        try {
            return new InfoField("hardware", new HardwareData());
        } catch (SecurityException e) {
            return new InfoField("hardware", FieldError.PERMISSIONS);
        }
    }

    public static class HardwareData {
        String serial;
        String board;
        String brand;
        String cpuAbi;
        String cpuAbi2;
        String device;
        String hardware;
        String manufacturer;
        String model;
        String product;
        String radioVersion;

        public HardwareData() {
            this.board = Build.BOARD;
            this.brand = Build.BRAND;
            this.cpuAbi = Build.CPU_ABI;
            this.cpuAbi2 = Build.CPU_ABI2;
            this.device = Build.DEVICE;
            this.serial = Build.SERIAL;
            this.hardware = Build.HARDWARE;
            this.manufacturer = Build.MANUFACTURER;
            this.model = Build.MODEL;
            this.product = Build.PRODUCT;
            this.radioVersion = Build.getRadioVersion();
        }
    }
}
