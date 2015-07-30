package com.everypay.sdk.api;

import com.everypay.sdk.Card;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;


public class EverypayTokenRequestData {
    public String hmac;
    public String nonce;
    public long timestamp;
    public String apiUsername;  // Merchant ID


    String ccHolderName;
    String ccNumber;
    int ccMonth;
    int ccYear;
    String ccVerification;

    public EverypayTokenRequestData(MerchantParamsResponseData params, Card card) {
        this.hmac = params.hmac;
        this.nonce = params.nonce;
        this.timestamp = params.timestamp;
        this.apiUsername = params.apiUsername;

        this.ccHolderName = card.getName();
        this.ccNumber = card.getNumber();
        this.ccYear = card.getExpYear();
        this.ccMonth = card.getExpMonth();
        this.ccVerification = card.getCVC();
    }
}
