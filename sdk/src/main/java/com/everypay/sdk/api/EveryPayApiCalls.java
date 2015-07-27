package com.everypay.sdk.api;

import com.everypay.sdk.api.request.EveryPaySaveCardRequestData;
import com.everypay.sdk.api.response.EveryPaySaveCardResponseData;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EveryPayApiCalls {

    @POST("/mobile/savecard")
    EveryPaySaveCardResponseData saveCard(@Body EveryPaySaveCardRequestData params);
}
