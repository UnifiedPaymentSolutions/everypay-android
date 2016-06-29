package com.everypay.sdk.steps;


import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.requestdata.MerchantPaymentRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Util;

import retrofit2.Call;


public class MerchantPaymentStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PAYMENT;
    }

    public MerchantPaymentResponseData run(EveryPay ep, MerchantParamsResponseData paramsResponse, EveryPayTokenResponseData everypayResponse) {
        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(ep.getContext(), EveryPay.MERCHANT_API_URL_TESTING).getApiCalls();
        MerchantPaymentRequestData requestData = new MerchantPaymentRequestData(paramsResponse, everypayResponse);
        final Call<MerchantPaymentResponseData> call = apiCalls.callMakePayment(requestData);
        ErrorHelper response = Util.execute(call);
        if (response.isError()) {
            throw new EveryPayException(response.getErrors().get(0).getCode(), response.getErrors().get(0).getMessage());
        } else {
            return (MerchantPaymentResponseData) response;
        }
    }
}
