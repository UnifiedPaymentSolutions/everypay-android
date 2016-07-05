package com.everypay.sdk;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.inter.MerchantParamsListener;
import com.everypay.sdk.inter.ServiceListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPay3DsConfirmStep;
import com.everypay.sdk.steps.EveryPayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Log;
import com.everypay.sdk.util.Util;

import java.util.WeakHashMap;


public class EveryPaySession {


    private static final String EXCEPTION_CARD_IS_NULL = "Card is null";
    private static final String EXCEPTION_LISTENER_IS_NULL = "Listener is null";
    public static final String WEBVIEW_RESULT_FAILURE = "com.everypay.sdk.WEBVIEW_RESULT_FAILURE";
    private static final String MESSAGE_3DS_AUTHENTICATION_FAILED = "3Ds authentication failed";
    private static final String MESSAGE_3DS_AUTHENTICATION_CANCELED = "3Ds authentication canceled";
    private static final long THREAD_LOCK_TIMEOUT = 10 * 60 * 1000L;
    private static final int EXCEPTION_3DS_AUTHENTICATION_FAILED = 997;
    private static final int EXCEPTION_3DS_AUTHENTICATION_CANCELED = 998;
    private static final String PAYMENT_STATE_WAITING_FOR_3DS = "waiting_for_3ds_response";
    private final Object threadLock = new Object();
    private Handler handler;
    private Context context;
    private String id;
    private EveryPay ep;
    private String apiVersion;
    private String deviceInfo;
    private String accountId;
    private EveryPayListener listener;
    private volatile String paymentReference;
    private static final Log log = Log.getInstance(EveryPaySession.class);

    private Card card;
    private final WeakHashMap<String, ServiceListener> listeners = new WeakHashMap<>();

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;
    private EveryPay3DsConfirmStep everyPay3DsConfirmStep;


    public EveryPaySession(Context context, EveryPay ep, Card card, String deviceInfo, EveryPayListener listener, String apiVersion, String accountId) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;
        this.apiVersion = apiVersion;
        this.id = Util.getRandomString();
        this.accountId = accountId;

        if (card == null)
            throw new IllegalArgumentException(EXCEPTION_CARD_IS_NULL);
        this.card = card;

        this.deviceInfo = deviceInfo;

        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException(EXCEPTION_LISTENER_IS_NULL);

        this.merchantParamsStep = ep.getMerchantParamsStep();
        this.everyPayTokenStep = new EveryPayTokenStep();
        this.merchantPaymentStep = ep.getMerchantPaymentStep();
        this.everyPay3DsConfirmStep = new EveryPay3DsConfirmStep();
    }


    public void startPaymentFlow() {
        Step lastStep = merchantParamsStep;
        callStepStarted(merchantParamsStep);
        getMerchantParams();
    }

    private void getMerchantParams() {
        log.d("getMerchantParams called");
        merchantParamsStep.run(ep, apiVersion, accountId, new MerchantParamsListener() {
            @Override
            public void onMerchantParamsSucceed(MerchantParamsResponseData responseData) {
                log.d("EverypaySession merchantParams succeed");
                callStepSuccess(merchantParamsStep);
            }

            @Override
            public void onMerchantParamsFailure(ErrorHelper error) {
                log.d("EverypaySession merchantParams failed");
            }
        });

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
    private <T extends ServiceListener> T getListener(final String tag, final boolean forgetListener, @NonNull final Class<T> type) {
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
    private String buildUrlForWebView(EveryPay ep, String paymentReference, String secureCodeOne, String hmac) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(ep.getEveryPayHost())
                .path("/authentication3ds/new")
                .appendQueryParameter("payment_reference", paymentReference)
                .appendQueryParameter("secure_code_one", secureCodeOne)
                .appendQueryParameter("mobile_3ds_hmac", hmac)
                .build();
        return uri.toString();
    }

    private void startwebViewStep(Context context, String url, String id, EveryPay ep) {
        PaymentBrowserActivity.start(ep, context, url, id);
        takeLock();
    }

    private void callStepStarted(final Step step) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepStarted(step.getType());
                }
            });
        }
    }

    private void callStepSuccess(final Step step) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepSuccess(step.getType());
                }
            });
        }
    }

    private void callFullSuccess() {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.fullSuccess();
                }
            });
        }
    }

    private void callStepFailure(final Step step, final Exception e) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepFailure(step.getType(), e);
                }
            });
        }
    }

    public void webViewDone(String id, String result) {
        if (TextUtils.equals(this.id, id)) {
            if (!TextUtils.equals(result, WEBVIEW_RESULT_FAILURE)) {
                paymentReference = result;
            }
        }
    }

    /**
     * Wait until the client notifies us about success or failure
     */
    protected void takeLock() {
        // wait until the client notifies us about success of failure
        log.d("takeLock start");
        synchronized (threadLock) {
            try {
                threadLock.wait(THREAD_LOCK_TIMEOUT);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        log.d("takeLock end");
    }

    /**
     * release lock
     */
    protected void releaseLock() {
        synchronized (threadLock) {
            threadLock.notify();
        }
    }


}
