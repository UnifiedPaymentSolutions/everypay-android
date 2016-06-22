package com.everypay.sdk.steps;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Util;

import java.util.HashMap;

import retrofit2.Call;

public class EveryPay3DsConfirmStep extends Step {
    @Override
    public StepType getType() {
        return StepType.EVERYPAY_3DS_CONFIRM;
    }

    public EveryPayTokenResponseData run(String paymentReference, String hmac, String apiVersion) {
        EveryPayApi.EveryPayApiCalls apiCalls = EveryPayApi.getInstance(EveryPay.EVERYPAY_API_URL_TESTING).getApiCalls();
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile_3ds_hmac", hmac);
        params.put("api_version", apiVersion);
       final Call<EveryPayTokenResponseData> call = apiCalls.encryptedPaymentInstrumentConfirmed(paymentReference, params);
        ErrorHelper response = Util.execute(call);
        if(response.isError()) {
            throw new EveryPayException(response.getErrors().get(0).getCode(), response.getErrors().get(0).getMessage());
        } else {
            return (EveryPayTokenResponseData) response;
        }
    }
}
