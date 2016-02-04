package com.everypay.sdk;

import android.content.Context;

import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;


/**
 * Builder class for EverypaySessions.
 */
public class EveryPay {

    public static final String TAG = "everypay";

    public static final String EVERYPAY_API_URL_TESTING = "https://gw-demo.every-pay.com";
    public static final String EVERYPAY_API_URL_LIVE = "http://gw.every-pay.com";
    public static final String MERCHANT_API_URL_TESTING = "https://igwshop-demo.every-pay.com";


    static EveryPay defaultInstance;
    public static synchronized EveryPay getDefault() {
        if (defaultInstance == null)
            throw new RuntimeException("No default Everypay instance set.");
        return defaultInstance;
    }

    private Context context;
    private String everypayUrl;
    private String merchantUrl;
    private MerchantParamsStep merchantParamsStep;
    private MerchantPaymentStep merchantPaymentStep;

    private EveryPay(Context appContext, String everypayUrl, String merchantUrl, MerchantParamsStep merchantParamsStep, MerchantPaymentStep merchantPaymentStep) {
        this.context = appContext;
        this.everypayUrl = everypayUrl;
        this.merchantUrl = merchantUrl;
        this.merchantParamsStep = merchantParamsStep;
        this.merchantPaymentStep = merchantPaymentStep;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public EveryPay setDefault() {
        defaultInstance = this;
        return this;
    }

    public String getEverypayUrl() {
        return everypayUrl;
    }

    public String getMerchantUrl() {
        return merchantUrl;
    }

    public MerchantParamsStep getMerchantParamsStep() {
        return merchantParamsStep;
    }

    public MerchantPaymentStep getMerchantPaymentStep() {
        return  merchantPaymentStep;
    }

    public void startFullPaymentFlow(Card card, String deviceInfo, EveryPayListener callback) {
        new EveryPaySession(context, this, card, deviceInfo, callback).execute();
    }

    public static class Builder {
        Context context;
        String everypayUrl;
        String merchantUrl;
        MerchantParamsStep merchantParamsStep;
        MerchantPaymentStep merchantPaymentStep;

        private Builder(Context context) {
            this.context = context;
            this.everypayUrl = EVERYPAY_API_URL_TESTING;
            this.merchantUrl = MERCHANT_API_URL_TESTING;
            this.merchantParamsStep = null;
            this.merchantPaymentStep = null;
        }

        public Builder setEverypayApiBaseUrl(String url) {
            this.everypayUrl = url;
            return this;
        }

        public Builder setMerchantApiBaseUrl(String url) {
            this.merchantUrl = url;
            return this;
        }

        public Builder setMerchantParamsStep(MerchantParamsStep merchantParamsStep) {
            this.merchantParamsStep = merchantParamsStep;
            return this;
        }

        public Builder setMerchantPaymentStep(MerchantPaymentStep merchantPaymentStep) {
            this.merchantPaymentStep = merchantPaymentStep;
            return this;
        }

        public EveryPay build() {
            if (merchantParamsStep == null)
                merchantParamsStep = new MerchantParamsStep();
            if (merchantPaymentStep == null)
                merchantPaymentStep = new MerchantPaymentStep();
            return new EveryPay(context.getApplicationContext(), everypayUrl, merchantUrl, merchantParamsStep, merchantPaymentStep);
        }

    }
}
