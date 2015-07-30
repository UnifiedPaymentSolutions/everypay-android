package com.everypay.sdk.steps;


import android.app.Activity;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.api.EverypayTokenResponseData;
import com.everypay.sdk.api.merchant.MerchantApiException;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.api.merchant.MerchantPaymentRequestData;
import com.everypay.sdk.api.merchant.MerchantPaymentResponseData;

public class MerchantPaymentStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PAYMENT;
    }

    public MerchantPaymentResponseData run(Activity activity, Everypay ep, MerchantParamsResponseData paramsResponse, EverypayTokenResponseData everypayResponse) throws MerchantApiException {
        MerchantPaymentResponseData resp = ep.getMerchantApi().callMakePayment(new MerchantPaymentRequestData(paramsResponse, everypayResponse));
        if (resp == null || !"success".equals(resp.status)) {
            throw new MerchantApiException("Payment failed with error code: " + resp.status);
        }
        return resp;
    }
}
