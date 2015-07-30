package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.Everypay;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Everypay ep = Everypay.getInstance();
        ep.configure("hello", Everypay.EVERYPAY_API_URL_TESTING, Everypay.MERCHANT_API_URL_TESTING);
    }
}
