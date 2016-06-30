package com.everypay.sdk.steps;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Util;

import java.util.HashMap;

import retrofit2.Call;

public class TestStep extends Step {
    @Override
    public StepType getType() {
        return null;
    }

    public ErrorHelper run(String paymentReference, String secureCodeOne, String hmac) {
        EveryPayApi.EveryPayApiCalls apiCalls = EveryPayApi.getInstance(EveryPay.EVERYPAY_API_URL_TESTING).getApiCalls();
        HashMap<String, String> params = new HashMap<>();
        params.put("payment_reference", paymentReference);
        params.put("secure_code_one", secureCodeOne);
        params.put("mobile_3ds_hmac", hmac);
        final Call<String> call = apiCalls.auth(params);
        ErrorHelper response = Util.execute(call);
        if(response.isError()) {
            throw new EveryPayException(response.getErrors().get(0).getCode(), response.getErrors().get(0).getMessage());
        } else {
            return response;
        }
    }
}
