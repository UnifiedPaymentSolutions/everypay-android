package com.everypay.sdk.steps;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.request.MerchantParamsRequestData;
import com.everypay.sdk.api.response.MerchantParamsResponseData;

public class MerchantTokenParams extends Step<MerchantParamsRequestData, MerchantParamsResponseData> {

    @Override
    public StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    @Override
    public MerchantParamsRequestData makeRequestData(Object o) {
        return new MerchantParamsRequestData();
    }

    public MerchantParamsResponseData run(EveryPay ep, MerchantParamsRequestData requestData) {
        return ep.getMerchantApi().getMobileParams(requestData);
    }

}
