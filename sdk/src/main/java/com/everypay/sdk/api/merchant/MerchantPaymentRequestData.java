package com.everypay.sdk.api.merchant;

import com.everypay.sdk.api.EverypayTokenResponseData;

public class MerchantPaymentRequestData {
    String androidId;
    String single_cc_token;

    public MerchantPaymentRequestData(MerchantParamsResponseData paramsResponse, EverypayTokenResponseData everypayResponse) {
        this.androidId = "hello";
        this.single_cc_token = "token";
    }
}
