package com.everypay.sdk.steps;


import android.support.annotation.Nullable;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.requestdata.EveryPayTokenRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.inter.EveryPayCallback;
import com.everypay.sdk.inter.EveryPayTokenListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.util.Log;

import retrofit2.Call;
import retrofit2.Response;


public class EveryPayTokenStep extends Step {

    private static final Log log = Log.getInstance(MerchantParamsStep.class);

    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public void run(final String tag, final EveryPay ep, MerchantParamsResponseData paramsResponse, Card card, String deviceInfo, final EveryPayTokenListener listener) {
        log.d("EveryPayTokenStep run called");
        if (ep != null) {
            ep.setListener(tag, listener);
        }
        EveryPayApi.EveryPayApiCalls apiCalls = EveryPayApi.getInstance(ep.getEverypayUrl()).getApiCalls();
        final EveryPayTokenRequestData requestData = new EveryPayTokenRequestData(paramsResponse, card, deviceInfo);
        final Call<EveryPayTokenResponseData> call = apiCalls.saveCard(requestData);
        call.enqueue(new EveryPayCallback<EveryPayTokenResponseData>() {
            @Override
            public void onSuccess(Call<EveryPayTokenResponseData> call, Response<EveryPayTokenResponseData> response) {

                log.d("saveCard success with response : " + response.body().toString());
                final EveryPayTokenListener listener = ep.getListener(tag, true, EveryPayTokenListener.class);
                if (listener != null) {
                    listener.onEveryPayTokenSucceed(response.body());
                }
            }

            @Override
            public void onFailure(Call<EveryPayTokenResponseData> call, @Nullable EveryPayError error, @Nullable Throwable t) {
                log.d("saveCard failure");
                final EveryPayTokenListener listener = ep.getListener(tag, true, EveryPayTokenListener.class);
                if (listener != null) {
                    listener.onEveryPayTokenFailure(EveryPayError.from(ep.getContext(), error, t));
                }

            }
        });
    }
}
