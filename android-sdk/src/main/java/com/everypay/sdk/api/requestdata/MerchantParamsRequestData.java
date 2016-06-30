package com.everypay.sdk.api.requestdata;


public class MerchantParamsRequestData {

    String accountId;
    String apiVersion;

    public MerchantParamsRequestData(String apiVersion, String accountId) {
        this.apiVersion = apiVersion;
        this.accountId = accountId;
    }


    @Override
    public String toString() {
        return "MerchantParamsRequestData{" +
                "accountId='" + accountId +
                ", apiVersion='" + apiVersion +
                '}';
    }
}
