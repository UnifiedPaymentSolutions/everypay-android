package com.everypay.sdk;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.everypay.sdk.steps.MerchantTokenParams;
import com.everypay.sdk.steps.Step;


public class EveryPayTask extends AsyncTask<Void, Void, Void> {

    private Handler handler;
    private EveryPay ep;
    private EveryPayListener listener;

    public EveryPayTask(EveryPayListener listener) {
        this.handler = new Handler();
        this.ep = EveryPay.getInstance();
        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException("Listener is null");
    }

    @Override
    protected Void doInBackground(Void... params) {
        Step steps[] = new Step[] {
                new MerchantTokenParams(),
        };

        Object requestData = steps[0].makeRequestData(null);
        for (Step step : steps) {
            try {
                callStepStarted(step);
                Object response = step.run(ep, requestData);
                callStepSuccess(step);
            } catch (Exception e) {
                Log.e(EveryPay.TAG, String.format("Step %s failed.", step), e);
                callStepFailure(step, e);
                return null;
            }
        }
        callFullSuccess();
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
