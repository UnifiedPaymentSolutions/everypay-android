package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.EveryPay;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EveryPay ep = EveryPay.getInstance();
        ep.configure("hello", EveryPay.EVERYPAY_API_URL_TESTING, EveryPay.MERCHANT_API_URL_TESTING);
    }
}
