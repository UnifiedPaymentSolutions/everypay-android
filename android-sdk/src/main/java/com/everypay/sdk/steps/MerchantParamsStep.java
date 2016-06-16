package com.everypay.sdk.steps;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.requestdata.MerchantParamsRequestData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class MerchantParamsStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    public MerchantParamsResponseData run(EveryPay ep, String deviceInfo, String apiVersion) {
        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(ep.getMerchantUrl()).getApiCalls();
        MerchantParamsRequestData requestData = new MerchantParamsRequestData(deviceInfo, apiVersion);
        Response<MerchantParamsResponseData> requestResult;
        Call<MerchantParamsResponseData> call = apiCalls.callGetParams(requestData);
        try {
            requestResult = call.execute();
            return requestResult.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
