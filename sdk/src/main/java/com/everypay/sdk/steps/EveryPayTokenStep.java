package com.everypay.sdk.steps;


import android.content.Context;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.EverypayTokenRequestData;
import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;

import java.util.Map;

public class EverypayTokenStep extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EverypayTokenResponseData run(Context activity, Everypay ep, MerchantParamsResponseData paramsResponse, Card card, String deviceInfo) {
        return ep.getEverypayApi().saveCard(new EverypayTokenRequestData(paramsResponse, card, deviceInfo));
    }
}
