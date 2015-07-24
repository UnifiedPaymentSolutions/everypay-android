package com.everypay.sdk;

public class EveryPay {

    public static final String API_URL_TESTING = "http://example.com";
    public static final String API_URL_LIVE = "http://example.com";

    private static EveryPay instance;
    public static synchronized EveryPay getInstance() {
        if (instance == null)
            instance = new EveryPay();
        return instance;
    }

    public void configure(String merchantId, String everyPayApiUrl, String merchantApiUrl) {

    }

    public void startFullPaymentFlow(EveryPayListener callback) {

    }

}
