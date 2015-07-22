package com.everypay.sdk.api.request;

public class MerchantMakePaymentRequestData {
    String androidId;
    String single_cc_token;

    public MerchantMakePaymentRequestData() {
        this.androidId = "hello";
        this.single_cc_token = "token";
    }
}
