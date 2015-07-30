package com.everypay.sdk.api;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EverypayApiCalls {

    @POST("/single_use_tokens")
    EverypayTokenResponseData saveCard(@Body EverypayTokenRequestData params);
}
