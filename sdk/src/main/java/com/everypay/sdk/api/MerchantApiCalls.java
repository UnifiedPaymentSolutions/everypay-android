package com.everypay.sdk.api;

import com.everypay.sdk.api.request.MerchantParamsRequestData;
import com.everypay.sdk.api.request.MerchantMakePaymentRequestData;
import com.everypay.sdk.api.response.MerchantParamsResponseData;
import com.everypay.sdk.api.response.MerchantMakePaymentResponseData;

import retrofit.http.Body;
import retrofit.http.POST;

public interface MerchantApiCalls {

    @POST("/merchant_mobile_payments/generate_token_api_parameter")
    MerchantParamsResponseData getMobileParams(@Body MerchantParamsRequestData params);

    @POST("/mobile/makepayment")
    MerchantMakePaymentResponseData makePayment(@Body MerchantMakePaymentRequestData params);
}
