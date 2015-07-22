package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.EveryPay;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EveryPay ep = EveryPay.getInstance();
        ep.configure("hello", "http://example.org", "http://example.net");
    }
}
