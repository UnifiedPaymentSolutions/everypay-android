package com.everypay.sdk.steps;


import android.app.Activity;

import com.everypay.sdk.Card;
import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.EverypayTokenRequestData;
import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;

public class EverypayTokenStep extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EverypayTokenResponseData run(Activity activity, Everypay ep, MerchantParamsResponseData paramsResponse, Card card) {
        return ep.getEverypayApi().saveCard(new EverypayTokenRequestData(paramsResponse, card));
    }
}
