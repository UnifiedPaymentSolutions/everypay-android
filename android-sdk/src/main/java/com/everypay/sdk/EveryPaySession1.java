package com.everypay.sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.everypay.sdk.api.EveryPayTokenResponseData1;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPayTokenStep1;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;


public class EveryPaySession1 extends AsyncTask<Void, Void, Void> {


    private static final String EXCEPTION_CARD_IS_NULL = "Card is null";
    private static final String EXCEPTION_LISTENER_IS_NULL = "Listener is null";
    private Handler handler;
    private Context context;
    private EveryPay1 ep;
    private String deviceInfo;
    private EveryPayListener1 listener;

    private Card card;

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep1 everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;


    public EveryPaySession1(Context context, EveryPay1 ep, Card card, String deviceInfo, EveryPayListener1 listener) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;

        if (card == null)
            throw new IllegalArgumentException(EXCEPTION_CARD_IS_NULL);
        this.card = card;

        this.deviceInfo = deviceInfo;

        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException(EXCEPTION_LISTENER_IS_NULL);

        this.merchantParamsStep = ep.getMerchantParamsStep();
        this.everyPayTokenStep = new EveryPayTokenStep1();
        this.merchantPaymentStep = ep.getMerchantPaymentStep();
    }


    @Override
    protected Void doInBackground(Void... params) {
        Step lastStep = null;
        try {
            lastStep = merchantParamsStep;
            callStepStarted(merchantParamsStep);
            MerchantParamsResponseData paramsResponse = merchantParamsStep.run(ep, deviceInfo);
            callStepSuccess(merchantParamsStep);

            lastStep = everyPayTokenStep;
            callStepStarted(everyPayTokenStep);
            EveryPayTokenResponseData1 everypayResponse = everyPayTokenStep.run(ep, paramsResponse, card, deviceInfo);
            callStepSuccess(everyPayTokenStep);

            lastStep = merchantPaymentStep;
            callStepStarted(merchantPaymentStep);
            merchantPaymentStep.run(context, ep, paramsResponse, everypayResponse);
            callStepSuccess(merchantPaymentStep);

            callFullSuccess();
        } catch (Exception e) {
            Log.e(EveryPay1.TAG, String.format("Step %s failed.", lastStep), e);
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
