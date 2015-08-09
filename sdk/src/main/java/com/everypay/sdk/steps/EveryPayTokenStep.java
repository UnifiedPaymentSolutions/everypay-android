package com.everypay.sdk.steps;


import android.app.Activity;

import com.everypay.sdk.model.Card;
import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.EverypayTokenRequestData;
import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.collector.DeviceCollector;

import java.util.Map;

public class EverypayTokenStep extends Step {
    @Override
    public final StepType getType() {
        return StepType.EVERYPAY_TOKEN;
    }

    public EverypayTokenResponseData run(Activity activity, Everypay ep, MerchantParamsResponseData paramsResponse, Card card) {
        DeviceCollector collector = new DeviceCollector(activity.getApplicationContext());
        Map<String, Object> collectorResult = collector.collect();
        return ep.getEverypayApi().saveCard(new EverypayTokenRequestData(paramsResponse, card, collectorResult));
    }
}
