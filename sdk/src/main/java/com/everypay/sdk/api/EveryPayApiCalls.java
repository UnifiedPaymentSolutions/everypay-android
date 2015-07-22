package com.everypay.sdk.api;

import com.everypay.sdk.api.request.EveryPaySaveCardRequestData;
import com.everypay.sdk.api.response.EveryPaySaveCardResponseData;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface EveryPayApiCalls {

    @POST("/mobile/savecard")
    void saveCard(@Body EveryPaySaveCardRequestData params, Callback<EveryPaySaveCardResponseData> callback);
}
