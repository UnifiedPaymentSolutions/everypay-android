package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.EveryPay;
import com.squareup.leakcanary.LeakCanary;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
