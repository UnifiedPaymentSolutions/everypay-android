package com.everypay.sdk.steps;



import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.api.EveryPayTokenRequestData;
import com.everypay.sdk.api.EveryPayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;

public class EveryPayTokenStep extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EveryPayTokenResponseData run(EveryPay ep, MerchantParamsResponseData paramsResponse, Card card, String deviceInfo) {
        return EveryPayApi.getEverypayApi(ep.getEverypayUrl()).saveCard(new EveryPayTokenRequestData(paramsResponse, card, deviceInfo));
    }
}
