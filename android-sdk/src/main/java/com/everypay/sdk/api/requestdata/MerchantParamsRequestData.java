package com.everypay.sdk.api.requestdata;


public class MerchantParamsRequestData {

    private static String ACCOUNT_ID = "EUR3D1";
    String accountId;
    String deviceInfo;
    String apiVersion;

    public MerchantParamsRequestData(String deviceInfo, String apiVersion) {
        this.deviceInfo = deviceInfo;
        this.apiVersion = apiVersion;
        this.accountId = ACCOUNT_ID;
    }


    @Override
    public String toString() {
        return "MerchantParamsRequestData{" +
                "accountId='" + accountId + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                '}';
    }
}
