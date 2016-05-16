package com.everypay.sdk.api;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EveryPayApiCalls {

    @POST("/encrypted_payment_instruments")
    EveryPayTokenResponseData saveCard(@Body EveryPayTokenRequestData params);
}
