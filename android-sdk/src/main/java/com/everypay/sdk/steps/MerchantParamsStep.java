package com.everypay.sdk.steps;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.requestdata.MerchantParamsRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Util;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class MerchantParamsStep extends Step {

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    public MerchantParamsResponseData run(EveryPay ep, String apiVersion, String accountId) {
        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(ep.getContext(), ep.getMerchantUrl()).getApiCalls();
        MerchantParamsRequestData requestData = new MerchantParamsRequestData(apiVersion, accountId);
        Call<MerchantParamsResponseData> call = apiCalls.callGetParams(requestData);
        ErrorHelper response = Util.execute(call);
        if (response.isError()) {
            throw new EveryPayException(response.getErrors().get(0).getCode(), response.getErrors().get(0).getMessage());
        } else {
            return (MerchantParamsResponseData) response;
        }
    }
}
