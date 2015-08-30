package com.everypay.sdk.api;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EverypayApiCalls {

    @POST("/encrypted_payment_instruments")
    EverypayTokenResponseData saveCard(@Body EverypayTokenRequestData params);
}
