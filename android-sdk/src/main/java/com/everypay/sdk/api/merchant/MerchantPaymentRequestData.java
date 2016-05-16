package com.everypay.sdk.api.merchant;

import com.everypay.sdk.api.EveryPayTokenResponseData1;

public class MerchantPaymentRequestData {
    public String hmac;
    public String ccTokenEncrypted;

    public MerchantPaymentRequestData(MerchantParamsResponseData paramsResponse, EveryPayTokenResponseData1 everypayResponse) {
        this.hmac = paramsResponse.hmac;
        this.ccTokenEncrypted = everypayResponse.getToken();
    }
}
