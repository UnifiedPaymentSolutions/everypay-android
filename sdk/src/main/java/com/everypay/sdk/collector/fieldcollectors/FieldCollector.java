package com.everypay.sdk.collector.fieldcollectors;


import android.content.Context;

import com.everypay.sdk.collector.InfoField;

public interface FieldCollector {

    InfoField getField(Context context);
}
