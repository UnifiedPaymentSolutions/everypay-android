package com.everypay.sdk;

public class EveryPay {

    private static EveryPay instance;
    public static synchronized EveryPay getInstance() {
        if (instance == null)
            instance = new EveryPay();
        return instance;
    }

    String merchantId;
    String everyPayApiUrl;
    String merchantApiUrl;


    public void configure(String merchantId, String everyPayApiUrl, String merchantApiUrl) {

    }



}
