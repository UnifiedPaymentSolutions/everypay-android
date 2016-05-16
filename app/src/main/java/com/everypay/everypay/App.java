package com.everypay.everypay;

import android.app.Application;

import com.everypay.sdk.EveryPay1;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EveryPay1.with(this).setEverypayApiBaseUrl(EveryPay1.EVERYPAY_API_URL_TESTING).setMerchantApiBaseUrl(EveryPay1.MERCHANT_API_URL_TESTING).build().setDefault();
    }
}
