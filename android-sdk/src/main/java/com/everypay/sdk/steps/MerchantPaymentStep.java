package com.everypay.sdk.steps;


import android.content.Context;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.api.requestdata.MerchantPaymentRequestData;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class MerchantPaymentStep extends Step {

    private static final String EXCEPTION_PAYMENT_FAILED = "Payment failed with error code: ";
    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PAYMENT;
    }

    public MerchantPaymentResponseData run(Context activity, EveryPay ep, MerchantParamsResponseData paramsResponse, EveryPayTokenResponseData everypayResponse) {
        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(EveryPay.MERCHANT_API_URL_TESTING).getApiCalls();
        Response<MerchantPaymentResponseData> requestResult;
        MerchantPaymentRequestData requestData = new MerchantPaymentRequestData(paramsResponse, everypayResponse);
        final Call<MerchantPaymentResponseData> call = apiCalls.callMakePayment(requestData);
        try {
            requestResult = call.execute();
            return requestResult.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
