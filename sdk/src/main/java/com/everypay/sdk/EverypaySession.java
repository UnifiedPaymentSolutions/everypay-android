package com.everypay.sdk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.api.merchant.MerchantPaymentResponseData;
import com.everypay.sdk.steps.EverypayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;


public class EverypaySession extends AsyncTask<Void, Void, Void> {

    private Handler handler;
    private Activity activity;
    private Everypay ep;
    private EverypayListener listener;

    private Card card;

    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EverypayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;


    public EverypaySession(Activity activity, Card card, EverypayListener listener) {
        this.handler = new Handler();
        this.activity = activity;
        this.ep = Everypay.getInstance();
        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException("Listener is null");

        if (card == null)
            throw new IllegalArgumentException("Card is null");
        this.card = card;

        this.merchantParamsStep = new MerchantParamsStep();
        this.everyPayTokenStep = new EverypayTokenStep();
        this.merchantPaymentStep = new MerchantPaymentStep();
    }

    /**
     * Set before calling run() to customise.
     */
    public void setMerchantParamsStep(MerchantParamsStep merchantParamsStep) {
        this.merchantParamsStep = merchantParamsStep;
    }

    /**
     * Set before calling run() to customise.
     */
    public void setMerchantPaymentStep(MerchantPaymentStep merchantPaymentStep) {
        this.merchantPaymentStep = merchantPaymentStep;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Step lastStep = null;
        try {
            lastStep = merchantParamsStep;
            callStepStarted(merchantParamsStep);
            MerchantParamsResponseData paramsResponse = merchantParamsStep.run(activity, ep);
            callStepSuccess(merchantParamsStep);

            lastStep = everyPayTokenStep;
            callStepStarted(everyPayTokenStep);
            EverypayTokenResponseData everypayResponse = everyPayTokenStep.run(activity, ep, paramsResponse, card);
            callStepSuccess(everyPayTokenStep);

            lastStep = merchantPaymentStep;
            callStepStarted(merchantPaymentStep);
            MerchantPaymentResponseData paymentResponse = merchantPaymentStep.run(activity, ep, paramsResponse, everypayResponse);
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
