package com.everypay.sdk.api.requestdata;

import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;

public class MerchantPaymentRequestData {
    public String hmac;
    public String ccTokenEncrypted;

    public MerchantPaymentRequestData(MerchantParamsResponseData paramsResponse, EveryPayTokenResponseData everypayResponse) {
        this.hmac = paramsResponse.hmac;
        this.ccTokenEncrypted = everypayResponse.getToken();
    }
}
