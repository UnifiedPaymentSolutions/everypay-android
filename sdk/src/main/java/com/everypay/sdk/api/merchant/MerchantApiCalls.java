package com.everypay.sdk.api.merchant;

import retrofit.http.Body;
import retrofit.http.POST;

public interface MerchantApiCalls {

    @POST("/merchant_mobile_payments/generate_token_api_parameters")
    MerchantParamsResponseData callGetParams(@Body MerchantParamsRequestData params);

    @POST("/merchant_mobile_payments/pay")
    MerchantPaymentResponseData callMakePayment(@Body MerchantPaymentRequestData params);
}
