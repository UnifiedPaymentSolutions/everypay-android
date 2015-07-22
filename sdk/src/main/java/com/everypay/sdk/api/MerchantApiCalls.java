package com.everypay.sdk.api;

import com.everypay.sdk.api.request.MerchantGetCredentialsRequestData;
import com.everypay.sdk.api.request.MerchantMakePaymentRequestData;
import com.everypay.sdk.api.response.MerchantGetCredentialsResponseData;
import com.everypay.sdk.api.response.MerchantMakePaymentResponseData;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface MerchantApiCalls {

    @POST("/mobile/credentials")
    void getMobileCredentials(@Body MerchantGetCredentialsRequestData params, Callback<MerchantGetCredentialsResponseData> callback);

    @POST("/mobile/makepayment")
    void makePayment(@Body MerchantMakePaymentRequestData params, Callback<MerchantMakePaymentResponseData> callback);
}
