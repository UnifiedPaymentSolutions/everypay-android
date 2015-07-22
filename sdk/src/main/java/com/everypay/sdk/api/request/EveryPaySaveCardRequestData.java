package com.everypay.sdk.api.request;

import com.everypay.sdk.api.response.MerchantGetCredentialsResponseData;


public class EveryPaySaveCardRequestData {
    public String hmac;
    public String nonce;
    public String timestamp;
    public String apiUsername;  // Merchant ID

    String ccNumber;
    int ccYear;
    int ccMonth;
    String ccHolderName;

    public EveryPaySaveCardRequestData(MerchantGetCredentialsResponseData credentials) {
        this.hmac = credentials.hmac;
        this.nonce = credentials.nonce;
        this.timestamp = credentials.timestamp;
        this.apiUsername = credentials.apiUsername;
    }
}
