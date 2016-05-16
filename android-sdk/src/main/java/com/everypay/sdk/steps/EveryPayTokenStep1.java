package com.everypay.sdk.steps;



import com.everypay.sdk.EveryPay1;
import com.everypay.sdk.api.EveryPayApi1;
import com.everypay.sdk.api.EveryPayTokenRequestData1;
import com.everypay.sdk.api.EveryPayTokenResponseData1;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;

public class EveryPayTokenStep1 extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EveryPayTokenResponseData1 run(EveryPay1 ep, MerchantParamsResponseData paramsResponse, Card card, String deviceInfo) {
        return EveryPayApi1.getEverypayApi(ep.getEverypayUrl()).saveCard(new EveryPayTokenRequestData1(paramsResponse, card, deviceInfo));
    }
}
