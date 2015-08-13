package com.everypay.sdk;

import android.content.Context;

import com.everypay.sdk.api.EverypayApi;
import com.everypay.sdk.api.EverypayApiCalls;
import com.everypay.sdk.api.merchant.MerchantApi;
import com.everypay.sdk.api.merchant.MerchantApiCalls;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;

import java.util.Map;


/**
 * Builder class for EverypaySessions.
 */
public class Everypay {

    public static final String TAG = "everypay";

    public static final String EVERYPAY_API_URL_TESTING = "https://gw-staging.every-pay.com";
    public static final String EVERYPAY_API_URL_LIVE = "http://gw.every-pay.com";
    public static final String MERCHANT_API_URL_TESTING = "https://igwshop-staging.every-pay.com";


    static Everypay defaultInstance;
    public static synchronized Everypay getDefault() {
        if (defaultInstance == null)
            throw new RuntimeException("No default Everypay instance set.");
        return defaultInstance;
    }

    private Context context;
    private EverypayApiCalls everypayApi;
    private MerchantParamsStep merchantParamsStep;
    private MerchantPaymentStep merchantPaymentStep;

    private Everypay(Context appContext, String everypayUrl, MerchantParamsStep merchantParamsStep, MerchantPaymentStep merchantPaymentStep) {
        this.context = appContext;
        this.everypayApi = EverypayApi.getEverypayApi(everypayUrl);
        this.merchantParamsStep = merchantParamsStep;
        this.merchantPaymentStep = merchantPaymentStep;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public EverypayApiCalls getEverypayApi() {
        return everypayApi;
    }

    public MerchantParamsStep getMerchantParamsStep() {
        return merchantParamsStep;
    }

    public MerchantPaymentStep getMerchantPaymentStep() {
        return  merchantPaymentStep;
    }

    public void startFullPaymentFlow(Card card, Map<String, Object> deviceInfo, EverypayListener callback) {
        new EverypaySession(context, this, card, deviceInfo, callback).execute();
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

        public Everypay build() {
            MerchantApiCalls merchantApi = MerchantApi.getMerchantApi(merchantUrl);
            if (merchantParamsStep == null)
                merchantParamsStep = new MerchantParamsStep(merchantApi);
            if (merchantPaymentStep == null)
                merchantPaymentStep = new MerchantPaymentStep(merchantApi);
            return new Everypay(context.getApplicationContext(), everypayUrl, merchantParamsStep, merchantPaymentStep);
        }

        public void setDefault() {
            defaultInstance = build();
        }

    }
}
