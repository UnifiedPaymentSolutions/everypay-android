package com.everypay.sdk.steps;


import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.api.requestdata.EveryPayTokenRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Util;

import retrofit2.Call;


public class EveryPayTokenStep extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EveryPayTokenResponseData run(MerchantParamsResponseData paramsResponse, Card card, String deviceInfo) {
        EveryPayApi.EveryPayApiCalls apiCalls = EveryPayApi.getInstance(EveryPay.EVERYPAY_API_URL_TESTING).getApiCalls();
        EveryPayTokenRequestData requestData = new EveryPayTokenRequestData(paramsResponse, card, deviceInfo);
        final Call<EveryPayTokenResponseData> call = apiCalls.saveCard(requestData);
        ErrorHelper response = Util.execute(call);
        if(response.isError()) {
            throw new EveryPayException(response.getErrors().get(0).getCode(), response.getErrors().get(0).getMessage());
        } else {
            return (EveryPayTokenResponseData) response;
        }
    }
}
