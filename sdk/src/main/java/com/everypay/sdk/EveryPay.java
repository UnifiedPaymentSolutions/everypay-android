package com.everypay.sdk;

import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.MerchantApiCalls;

public class EveryPay {

    public static final String TAG = "everypay";

    public static final String EVERYPAY_API_URL_TESTING = "https://gw-staging.every-pay.com";
    public static final String API_URL_LIVE = "http://example.com";

    public static final String MERCHANT_API_URL_TESTING = "https://igwshop-staging.every-pay.com";


    private static EveryPay instance;
    public static synchronized EveryPay getInstance() {
        if (instance == null)
            instance = new EveryPay();
        return instance;
    }

    private boolean configured = false;
    private String merchantId;
    private MerchantApiCalls merchantApi;

    public void configure(String merchantId, String everyPayApiUrl, String merchantApiUrl) {
        if (configured)
            throw new RuntimeException("Cannot configure EveryPay SDK twice");
        this.configured = true;
        this.merchantId = merchantId;
        this.merchantApi = MerchantApi.getMerchantApi(merchantApiUrl);
    }

    public void startFullPaymentFlow(EveryPayListener callback) {
        if (!configured)
            throw new RuntimeException("EveryPay SDK not configured.");
        new EveryPayTask(callback).execute();
    }

    public String getMerchantId() {
        return merchantId;
    }

    public MerchantApiCalls getMerchantApi() {
        return merchantApi;
    }

}
