package com.everypay.sdk;

import android.app.Activity;

import com.everypay.sdk.api.EverypayApi;
import com.everypay.sdk.api.EverypayApiCalls;
import com.everypay.sdk.api.merchant.MerchantApi;
import com.everypay.sdk.api.merchant.MerchantApiCalls;

public class Everypay {

    public static final String TAG = "everypay";

    public static final String EVERYPAY_API_URL_TESTING = "https://gw-staging.every-pay.com";
    public static final String EVERYPAY_API_URL_LIVE = "http://gw.every-pay.com";

    public static final String MERCHANT_API_URL_TESTING = "https://igwshop-staging.every-pay.com";


    private static Everypay instance;
    public static synchronized Everypay getInstance() {
        if (instance == null)
            instance = new Everypay();
        return instance;
    }

    private boolean configured = false;
    private String merchantId;
    private MerchantApiCalls merchantApi;
    private EverypayApiCalls everypayApi;

    public void configure(String merchantId, String everyPayApiUrl, String merchantApiUrl) {
        if (configured)
            throw new RuntimeException("Cannot configure EveryPay SDK twice");
        this.configured = true;
        this.merchantId = merchantId;
        this.merchantApi = MerchantApi.getMerchantApi(merchantApiUrl);
        this.everypayApi = EverypayApi.getEverypayApi(everyPayApiUrl);
    }

    public void startFullPaymentFlow(Activity activity, Card card, EverypayListener callback) {
        if (!configured)
            throw new RuntimeException("EveryPay SDK not configured.");
        new EverypaySession(activity, card, callback).execute();
    }

    public String getMerchantId() {
        return merchantId;
    }

    public MerchantApiCalls getMerchantApi() {
        return merchantApi;
    }

    public EverypayApiCalls getEverypayApi() {
        return everypayApi;
    }

}
