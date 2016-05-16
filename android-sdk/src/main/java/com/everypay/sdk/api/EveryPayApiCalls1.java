package com.everypay.sdk.api;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EveryPayApiCalls1 {

    @POST("/encrypted_payment_instruments")
    EveryPayTokenResponseData1 saveCard(@Body EveryPayTokenRequestData1 params);
}
