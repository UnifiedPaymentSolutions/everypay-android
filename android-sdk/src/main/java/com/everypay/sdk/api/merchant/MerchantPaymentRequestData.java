package com.everypay.sdk.api.merchant;

import com.everypay.sdk.api.EveryPayTokenResponseData;

public class MerchantPaymentRequestData {
    public String hmac;
    public String ccTokenEncrypted;

    public MerchantPaymentRequestData(MerchantParamsResponseData paramsResponse, EveryPayTokenResponseData everypayResponse) {
        this.hmac = paramsResponse.hmac;
        this.ccTokenEncrypted = everypayResponse.getToken();
    }
}
