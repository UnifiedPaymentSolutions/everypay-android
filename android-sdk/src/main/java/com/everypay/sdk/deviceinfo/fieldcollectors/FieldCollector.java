package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;

import com.everypay.sdk.deviceinfo.InfoField;

public interface FieldCollector {

    InfoField getField(Context context);
}
