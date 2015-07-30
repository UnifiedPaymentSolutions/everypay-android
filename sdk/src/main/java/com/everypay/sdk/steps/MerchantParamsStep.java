package com.everypay.sdk.steps;

import android.app.Activity;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.merchant.MerchantParamsRequestData;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;

public class MerchantParamsStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    public MerchantParamsResponseData run(Activity activity, Everypay ep) {
        return ep.getMerchantApi().callGetParams(new MerchantParamsRequestData());
    }
}
