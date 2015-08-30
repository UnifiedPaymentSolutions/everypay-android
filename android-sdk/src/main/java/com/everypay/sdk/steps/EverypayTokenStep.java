package com.everypay.sdk.steps;


import android.content.Context;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.EverypayApi;
import com.everypay.sdk.api.EverypayTokenRequestData;
import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;

public class EverypayTokenStep extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EverypayTokenResponseData run(Context activity, Everypay ep, MerchantParamsResponseData paramsResponse, Card card, String deviceInfo) {
        return EverypayApi.getEverypayApi(ep.getEverypayUrl()).saveCard(new EverypayTokenRequestData(paramsResponse, card, deviceInfo));
    }
}
