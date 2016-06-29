package com.everypay.sdk;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPay3DsConfirmStep;
import com.everypay.sdk.steps.EveryPayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Log;
import com.everypay.sdk.util.Util;


public class EveryPaySession extends AsyncTask<Void, Void, Void> {


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
    private EveryPayListener listener;
    private volatile String paymentReference;
    private static final Log log = Log.getInstance(EveryPaySession.class);

    private Card card;

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;
    private EveryPay3DsConfirmStep everyPay3DsConfirmStep;


    public EveryPaySession(Context context, EveryPay ep, Card card, String deviceInfo, EveryPayListener listener, String apiVersion) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;
        this.apiVersion = apiVersion;
        this.id = Util.getRandomString();

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


    @Override
    protected Void doInBackground(Void... params) {
        Step lastStep = null;
        try {
            lastStep = merchantParamsStep;
            callStepStarted(merchantParamsStep);
            MerchantParamsResponseData paramsResponse = merchantParamsStep.run(ep, deviceInfo, apiVersion);
            callStepSuccess(merchantParamsStep);

            lastStep = everyPayTokenStep;
            callStepStarted(everyPayTokenStep);
            EveryPayTokenResponseData everyPayResponse = everyPayTokenStep.run(paramsResponse, card, deviceInfo);
            callStepSuccess(everyPayTokenStep);
            EveryPayTokenResponseData everyPay3DsConfirmResponse = null;
            if (TextUtils.equals(everyPayResponse.getPaymentState(), PAYMENT_STATE_WAITING_FOR_3DS)) {
                String url = buildUrlForWebView(everyPayResponse.getPaymentReference(), everyPayResponse.getSecureCodeOne(), paramsResponse.getHmac());
                startwebViewStep(context, url, id);
                if (!TextUtils.isEmpty(paymentReference)) {
                    everyPay3DsConfirmResponse = everyPay3DsConfirmStep.run(paymentReference, paramsResponse.getHmac(), paramsResponse.getApiVersion());
                } else {
                    throw new EveryPayException(EXCEPTION_3DS_AUTHENTICATION_FAILED, MESSAGE_3DS_AUTHENTICATION_FAILED);
                }
            }
            lastStep = merchantPaymentStep;
            callStepStarted(merchantPaymentStep);
            if(everyPayResponse.getToken() == null && everyPay3DsConfirmResponse.getToken() == null){
                throw  new EveryPayException(EXCEPTION_3DS_AUTHENTICATION_CANCELED, MESSAGE_3DS_AUTHENTICATION_CANCELED);
            }
            merchantPaymentStep.run(paramsResponse, everyPay3DsConfirmResponse != null ? everyPay3DsConfirmResponse : everyPayResponse);
            callStepSuccess(merchantPaymentStep);
            callFullSuccess();

        } catch (Exception e) {
            log.e(String.format("Step %s failed.", lastStep), e);
            callStepFailure(lastStep, e);
            return null;
        }
        return null;
    }

    private String buildUrlForWebView(String paymentReference, String secureCodeOne, String hmac) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("gw-staging.every-pay.com")
                .path("authentication3ds/new")
                .appendQueryParameter("payment_reference", paymentReference)
                .appendQueryParameter("secure_code_one", secureCodeOne)
                .appendQueryParameter("mobile_3ds_hmac", hmac)
                .build();
        return uri.toString();
    }

    private void startwebViewStep(Context context, String url, String id) {
        PaymentBrowserActivity.start(context, url, id);
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
