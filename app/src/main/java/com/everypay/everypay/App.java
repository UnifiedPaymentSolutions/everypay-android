package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.EveryPay;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EveryPay.with(this).setEverypayApiBaseUrl(EveryPay.EVERYPAY_API_URL_TESTING).setMerchantApiBaseUrl(EveryPay.MERCHANT_API_URL_TESTING).build().setDefault();
    }
}
