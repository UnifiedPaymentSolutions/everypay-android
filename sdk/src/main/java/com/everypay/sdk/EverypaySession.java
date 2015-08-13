package com.everypay.sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EverypayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;

import java.util.Map;


public class EverypaySession extends AsyncTask<Void, Void, Void> {

    private Handler handler;
    private Context context;
    private Everypay ep;
    private Map<String, Object> deviceInfo;
    private EverypayListener listener;

    private Card card;

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EverypayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;


    public EverypaySession(Context context, Everypay ep, Card card, Map<String, Object> deviceInfo, EverypayListener listener) {
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
        this.everyPayTokenStep = new EverypayTokenStep();
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
            EverypayTokenResponseData everypayResponse = everyPayTokenStep.run(context, ep, paramsResponse, card, deviceInfo);
            callStepSuccess(everyPayTokenStep);

            lastStep = merchantPaymentStep;
            callStepStarted(merchantPaymentStep);
            merchantPaymentStep.run(context, ep, paramsResponse, everypayResponse);
            callStepSuccess(merchantPaymentStep);

            callFullSuccess();
        } catch (Exception e) {
            Log.e(Everypay.TAG, String.format("Step %s failed.", lastStep), e);
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
