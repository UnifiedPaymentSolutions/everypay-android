package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;
import android.os.Build;

import com.everypay.sdk.deviceinfo.FieldError;
import com.everypay.sdk.deviceinfo.InfoField;

public class HardwareCollector implements FieldCollector {

    private static final String FIELD_NAME_HARDWARE = "hardware";
    @Override
    public InfoField getField(Context context) {
        try {
            return new InfoField(FIELD_NAME_HARDWARE, new HardwareData());
        } catch (SecurityException e) {
            return new InfoField(FIELD_NAME_HARDWARE, FieldError.PERMISSIONS);
        }
    }

    public static class HardwareData {
        String serial;
        String board;
        String brand;
        String cpuAbi;
        String cpuAbi2;
        String[] supportedAbis;
        String device;
        String hardware;
        String manufacturer;
        String model;
        String product;
        String radioVersion;

        public HardwareData() {
            this.board = Build.BOARD;
            this.brand = Build.BRAND;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.supportedAbis = Build.SUPPORTED_ABIS;
            }else {
                this.cpuAbi = Build.CPU_ABI;
                this.cpuAbi2 = Build.CPU_ABI2;
            }
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
