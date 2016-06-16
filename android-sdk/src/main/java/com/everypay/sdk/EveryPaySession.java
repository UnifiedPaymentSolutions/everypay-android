package com.everypay.sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;
import com.everypay.sdk.util.Log;


public class EveryPaySession extends AsyncTask<Void, Void, Void> {


    private static final String EXCEPTION_CARD_IS_NULL = "Card is null";
    private static final String EXCEPTION_LISTENER_IS_NULL = "Listener is null";
    private Handler handler;
    private Context context;
    private EveryPay ep;
    private String apiVersion;
    private String deviceInfo;
    private EveryPayListener listener;
    private static final Log log = Log.getInstance(EveryPaySession.class);

    private Card card;

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;


    public EveryPaySession(Context context, EveryPay ep, Card card, String deviceInfo, EveryPayListener listener, String apiVersion) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;
        this.apiVersion = apiVersion;

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
            EveryPayTokenResponseData everypayResponse = everyPayTokenStep.run(paramsResponse, card, deviceInfo);
            log.d(everypayResponse.toString());
            callStepSuccess(everyPayTokenStep);

            lastStep = merchantPaymentStep;
            callStepStarted(merchantPaymentStep);
            merchantPaymentStep.run(context, ep, paramsResponse, everypayResponse);
            callStepSuccess(merchantPaymentStep);

            callFullSuccess();
        } catch (Exception e) {
            log.e(String.format("Step %s failed.", lastStep), e);
            callStepFailure(lastStep, e);
            return null;
        }
        return null;
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
}
