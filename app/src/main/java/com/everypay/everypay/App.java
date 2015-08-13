package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.Everypay;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Everypay.with(this).setEverypayApiBaseUrl(Everypay.EVERYPAY_API_URL_TESTING).setMerchantApiBaseUrl(Everypay.MERCHANT_API_URL_TESTING).setDefault();
    }
}
