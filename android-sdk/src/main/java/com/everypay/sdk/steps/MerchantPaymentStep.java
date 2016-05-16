package com.everypay.sdk.steps;


import android.content.Context;

import com.everypay.sdk.EveryPay1;
import com.everypay.sdk.api.EveryPayTokenResponseData1;
import com.everypay.sdk.api.merchant.MerchantApi;
import com.everypay.sdk.api.merchant.MerchantApiCalls;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.api.merchant.MerchantPaymentRequestData;
import com.everypay.sdk.api.merchant.MerchantPaymentResponseData;

public class MerchantPaymentStep extends Step {

    private static final String EXCEPTION_PAYMENT_FAILED = "Payment failed with error code: ";
    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PAYMENT;
    }

    public MerchantPaymentResponseData run(Context activity, EveryPay1 ep, MerchantParamsResponseData paramsResponse, EveryPayTokenResponseData1 everypayResponse) {
        MerchantApiCalls merchantApi = MerchantApi.getMerchantApi(ep.getMerchantUrl());
        MerchantPaymentResponseData resp = merchantApi.callMakePayment(new MerchantPaymentRequestData(paramsResponse, everypayResponse));
        if (resp == null || !"success".equals(resp.status)) {
            throw new RuntimeException(EXCEPTION_PAYMENT_FAILED + (resp == null ? "null" : resp.status));
        }
        return resp;
    }
}
