package com.everypay.sdk.steps;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.EveryPaySession;
import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.requestdata.MerchantParamsRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.inter.MerchantParamsListener;
import com.everypay.sdk.util.EveryPayException;
import com.everypay.sdk.util.Log;
import com.everypay.sdk.util.Util;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantParamsStep extends Step {

    private static final Log log = Log.getInstance(MerchantParamsStep.class);
    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    public void run(EveryPay ep, String apiVersion, String accountId, final MerchantParamsListener listener) {
        log.d("MerchantParamsStep run called");

        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(ep.getContext(), ep.getMerchantUrl()).getApiCalls();
        MerchantParamsRequestData requestData = new MerchantParamsRequestData(apiVersion, accountId);
        Call<MerchantParamsResponseData> call = apiCalls.callGetParams(requestData);
        call.enqueue(new Callback<MerchantParamsResponseData>() {
            @Override
            public void onResponse(Call<MerchantParamsResponseData> call, Response<MerchantParamsResponseData> response) {
                log.d("MerchantParamsResponse : " + response.toString());
                listener.onMerchantParamsSucceed(response.body());
            }

            @Override
            public void onFailure(Call<MerchantParamsResponseData> call, Throwable t) {
                log.d("MerchantParams failed : " + t.getMessage());
                listener.onMerchantParamsFailure(new ErrorHelper(null));
            }
        });
    }
}
