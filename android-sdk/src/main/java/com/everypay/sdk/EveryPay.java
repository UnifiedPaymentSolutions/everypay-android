package com.everypay.sdk;

import android.content.Context;
import android.os.Parcelable;

import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.Serializable;


/**
 * Builder class for EverypaySessions.
 */
public class EveryPay {


    public static final String TAG = "everypay";

    private static final String EXCEPTION_NO_DEFAULT_EVERYPAY_INSTANCE = "No default Everypay instance set.";

    public static final String EVERYPAY_API_URL_STAGING = "https://gw-staging.every-pay.com/";
    public static final String EVERYPAY_API_URL_DEMO = "https://gw-demo.every-pay.com/";
    public static final String EVERYPAY_API_URL_LIVE = "http://gw.every-pay.eu";
    public static final String MERCHANT_API_URL_STAGING = "https://igwshop-staging.every-pay.com/";
    public static final String MERCHANT_API_URL_DEMO = "https://igwshop-demo.every-pay.com/";

    private static final Log log = Log.getInstance(EveryPay.class);

    static EveryPay defaultInstance;
    public static synchronized EveryPay getDefault() {
        if (defaultInstance == null)
            throw new RuntimeException(EXCEPTION_NO_DEFAULT_EVERYPAY_INSTANCE);
        return defaultInstance;
    }

    private Context context;
    private String everypayUrl;
    private String merchantUrl;
    private String apiVersion;
    private MerchantParamsStep merchantParamsStep;
    private EveryPaySession session;
    private MerchantPaymentStep merchantPaymentStep;

    private EveryPay(Context appContext, String everypayUrl, String merchantUrl, MerchantParamsStep merchantParamsStep, MerchantPaymentStep merchantPaymentStep, String apiVersion) {
        this.context = appContext;
        this.everypayUrl = everypayUrl;
        this.merchantUrl = merchantUrl;
        this.merchantParamsStep = merchantParamsStep;
        this.merchantPaymentStep = merchantPaymentStep;
        this.apiVersion = apiVersion;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public EveryPay setDefault() {
        defaultInstance = this;
        return this;
    }

    public Context getContext() {
        return context;
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

    public void startFullPaymentFlow(Card card, String deviceInfo, EveryPayListener callback, String accountId) {
        Log.setLogLevel(Config.USE_DEBUG ? Log.LOG_LEVEL_DEBUG: Log.LOG_LEVEL_RELEASE);
        session = new EveryPaySession(context, this, card, deviceInfo, callback, apiVersion, accountId);
        session.execute();
    }

    public static class Builder {
        Context context;
        String everypayUrl;
        String merchantUrl;
        String apiVersion;
        MerchantParamsStep merchantParamsStep;
        MerchantPaymentStep merchantPaymentStep;

        private Builder(Context context) {
            this.context = context;
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

        public EveryPay build(String apiVersion) {
            if (merchantParamsStep == null)
                merchantParamsStep = new MerchantParamsStep();
            if (merchantPaymentStep == null)
                merchantPaymentStep = new MerchantPaymentStep();
            return new EveryPay(context.getApplicationContext(), everypayUrl, merchantUrl, merchantParamsStep, merchantPaymentStep, apiVersion);
        }

    }

    public void setWebViewResult(String id, String result) {
        session.webViewDone(id, result);
        session.releaseLock();
    }


}
