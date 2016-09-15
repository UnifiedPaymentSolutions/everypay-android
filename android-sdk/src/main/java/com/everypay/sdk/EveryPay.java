package com.everypay.sdk;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.everypay.sdk.inter.ServiceListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.Serializable;
import java.util.WeakHashMap;


/**
 * Builder class for EverypaySessions.
 */
public class EveryPay {


    public static final String TAG = "everypay";

    private static final String EXCEPTION_NO_DEFAULT_EVERYPAY_INSTANCE = "No default Everypay instance set.";

    public static final String EVERYPAY_API_URL_STAGING = "https://gw-staging.every-pay.com/";
    public static final String EVERYPAY_API_STAGING_HOST = "gw-staging.every-pay.com";
    public static final String EVERYPAY_API_URL_DEMO = "https://gw-demo.every-pay.com/v2/";
    public static final String EVERYPAY_API_DEMO_HOST = "gw-demo.every-pay.com";
    public static final String EVERYPAY_API_URL_LIVE = "https://gw.every-pay.eu";
    public static final String MERCHANT_API_URL_STAGING = "https://igwshop-staging.every-pay.com/";
    public static final String MERCHANT_API_URL_DEMO = "https://igwshop-demo.every-pay.com/";

    private static final Log log = Log.getInstance(EveryPay.class);

    static EveryPay defaultInstance;
    public static synchronized EveryPay getDefault() {
        if (defaultInstance == null) {
            return null;
        }
        return defaultInstance;
    }

    private Context context;
    private String everypayUrl;
    private String everyPayHost;
    private String merchantUrl;
    private String apiVersion;
    private MerchantParamsStep merchantParamsStep;
    private EveryPaySession session;
    private MerchantPaymentStep merchantPaymentStep;
    private final WeakHashMap<String, ServiceListener> listeners = new WeakHashMap<>();

    private EveryPay(Context appContext, String everypayUrl, String merchantUrl, MerchantParamsStep merchantParamsStep, MerchantPaymentStep merchantPaymentStep, String apiVersion, String everyPayHost) {
        this.context = appContext;
        this.everypayUrl = everypayUrl;
        this.merchantUrl = merchantUrl;
        this.merchantParamsStep = merchantParamsStep;
        this.merchantPaymentStep = merchantPaymentStep;
        this.apiVersion = apiVersion;
        this.everyPayHost = everyPayHost;
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

    public String getEveryPayHost() {
        return everyPayHost;
    }

    public MerchantParamsStep getMerchantParamsStep() {
        return merchantParamsStep;
    }

    public MerchantPaymentStep getMerchantPaymentStep() {
        return  merchantPaymentStep;
    }

    public void startFullPaymentFlow(String tag, Card card, String deviceInfo, EveryPayListener callback, String accountId) {
        setListener(tag, callback);
        Log.setLogLevel(Config.USE_DEBUG ? Log.LOG_LEVEL_DEBUG: Log.LOG_LEVEL_RELEASE);
        session = new EveryPaySession(context, defaultInstance, card, deviceInfo, callback, apiVersion, accountId);
        session.startPaymentFlow();
    }

    /**
     * Overwrite or clear a listener for a specific tag.
     * NB: For an initial listener set it when calling a specific method.
     *
     * @param tag      Listener tag
     * @param listener Listener to set
     */
    public void setListener(final String tag, @Nullable final ServiceListener listener) {
        log.d("setListener: " + tag + ", listener: " + listener);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        synchronized (listeners) {
            listeners.put(tag, listener);
        }
    }

    /**
     * Getter for specific listener.
     *
     * @param tag            unique tag that listener was set with
     * @param forgetListener if we should listen for callback or not
     * @param type           listener type
     * @return listener of provided type
     */
    public <T extends ServiceListener> T getListener(final String tag, final boolean forgetListener, @NonNull final Class<T> type) {
        log.d("getListener: " + tag + ", forgetListener: " + forgetListener);
        if (TextUtils.isEmpty(tag) || type == null) {
            return null;
        }
        synchronized (listeners) {
            if (listeners.get(tag) != null && type.isInstance(listeners.get(tag))) {
                //noinspection unchecked
                return (T) (forgetListener ? listeners.remove(tag) : listeners.get(tag));
            }
        }
        return null;
    }

    /**
     * Method to remove listener.
     *
     * @param tag unique tag that listeners was set with
     */
    public void removeListener(final String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        synchronized (listeners) {
            listeners.remove(tag);
        }
    }
    public static class Builder {
        Context context;
        String everypayUrl;
        String merchantUrl;
        String everyPayHost;
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
        public Builder setEveryPayHost(String everyPayHost) {
            this.everyPayHost = everyPayHost;
            return this;
        }

        public EveryPay build(String apiVersion) {
            if (merchantParamsStep == null)
                merchantParamsStep = new MerchantParamsStep();
            if (merchantPaymentStep == null)
                merchantPaymentStep = new MerchantPaymentStep();
            return new EveryPay(context.getApplicationContext(), everypayUrl, merchantUrl, merchantParamsStep, merchantPaymentStep, apiVersion, everyPayHost);
        }

    }




}
