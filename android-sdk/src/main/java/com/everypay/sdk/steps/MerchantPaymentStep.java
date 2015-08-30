package com.everypay.sdk.steps;


import android.content.Context;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantApi;
import com.everypay.sdk.api.merchant.MerchantApiCalls;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.api.merchant.MerchantPaymentRequestData;
import com.everypay.sdk.api.merchant.MerchantPaymentResponseData;

public class MerchantPaymentStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PAYMENT;
    }

    public MerchantPaymentResponseData run(Context activity, Everypay ep, MerchantParamsResponseData paramsResponse, EverypayTokenResponseData everypayResponse) {
        MerchantApiCalls merchantApi = MerchantApi.getMerchantApi(ep.getMerchantUrl());
        MerchantPaymentResponseData resp = merchantApi.callMakePayment(new MerchantPaymentRequestData(paramsResponse, everypayResponse));
        if (resp == null || !"success".equals(resp.status)) {
            throw new RuntimeException("Payment failed with error code: " + (resp == null ? "null" : resp.status));
        }
        return resp;
    }
}
