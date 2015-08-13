package com.everypay.sdk.steps;

import android.content.Context;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.merchant.MerchantApiCalls;
import com.everypay.sdk.api.merchant.MerchantParamsRequestData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;

import java.util.Map;

public class MerchantParamsStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    private MerchantApiCalls merchantApi;

    public MerchantParamsStep(MerchantApiCalls merchantApi) {
        this.merchantApi = merchantApi;
    }

    public MerchantParamsResponseData run(Context activity, Everypay ep, Map<String, Object> deviceInfo) {
        return merchantApi.callGetParams(new MerchantParamsRequestData(deviceInfo));
    }
}
