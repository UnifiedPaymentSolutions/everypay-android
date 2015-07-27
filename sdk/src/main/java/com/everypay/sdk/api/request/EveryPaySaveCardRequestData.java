package com.everypay.sdk.api.request;

import com.everypay.sdk.api.response.MerchantParamsResponseData;


public class EveryPaySaveCardRequestData {
    public String hmac;
    public String nonce;
    public long timestamp;
    public String apiUsername;  // Merchant ID

    String ccNumber;
    int ccYear;
    int ccMonth;
    String ccHolderName;

    public EveryPaySaveCardRequestData(MerchantParamsResponseData credentials) {
        this.hmac = credentials.hmac;
        this.nonce = credentials.nonce;
        this.timestamp = credentials.timestamp;
        this.apiUsername = credentials.apiUsername;
    }
}
