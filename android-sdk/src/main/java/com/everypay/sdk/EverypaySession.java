package com.everypay.sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.everypay.sdk.api.EveryPayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;


public class EveryPaySession extends AsyncTask<Void, Void, Void> {

    private Handler handler;
    private Context context;
    private EveryPay ep;
    private String deviceInfo;
    private EveryPayListener listener;

    private Card card;

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;


    public EveryPaySession(Context context, EveryPay ep, Card card, String deviceInfo, EveryPayListener listener) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;

        if (card == null)
            throw new IllegalArgumentException("Card is null");
        this.card = card;

        this.deviceInfo = deviceInfo;

        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException("Listener is null");

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
            MerchantParamsResponseData paramsResponse = merchantParamsStep.run(context, ep, deviceInfo);
            callStepSuccess(merchantParamsStep);

            lastStep = everyPayTokenStep;
            callStepStarted(everyPayTokenStep);
            EveryPayTokenResponseData everypayResponse = everyPayTokenStep.run(context, ep, paramsResponse, card, deviceInfo);
            callStepSuccess(everyPayTokenStep);

            lastStep = merchantPaymentStep;
            callStepStarted(merchantPaymentStep);
            merchantPaymentStep.run(context, ep, paramsResponse, everypayResponse);
            callStepSuccess(merchantPaymentStep);

            callFullSuccess();
        } catch (Exception e) {
            Log.e(EveryPay.TAG, String.format("Step %s failed.", lastStep), e);
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
