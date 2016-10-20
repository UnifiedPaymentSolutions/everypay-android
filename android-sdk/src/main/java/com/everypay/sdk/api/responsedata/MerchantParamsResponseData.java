package com.everypay.sdk.api.responsedata;


import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MerchantParamsResponseData extends ErrorHelper {

    private static final long serialVersionUID = 3727496590524886343L;
    @SerializedName("api_username")
    public String apiUsername;  // Merchant ID
    @SerializedName("account_id")
    public String accountId;  // Merchant account & currency
    @SerializedName("user_ip")
    public String userIp;
    @SerializedName("nonce")
    public String nonce;
    @SerializedName("timestamp")
    public long timestamp;
    @SerializedName("hmac")
    public String hmac;
    @SerializedName("api_version")
    public String apiVersion;
    @SerializedName("order_reference")
    public String orderReference;

    public MerchantParamsResponseData( ArrayList<EveryPayError> errors) {
        super(errors);
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserIp() {
        return userIp;
    }

    public String getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHmac() {
        return hmac;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getOrderReference() {
        return orderReference;
    }

    @Override
    public String toString() {
        return "MerchantParamsResponseData{" +
                "apiUsername='" + apiUsername + '\'' +
                ", accountId='" + accountId + '\'' +
                ", userIp='" + userIp + '\'' +
                ", nonce='" + nonce + '\'' +
                ", timestamp=" + timestamp +
                ", hmac='" + hmac + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", orderReference='" + orderReference + '\'' +
                '}';
    }
}
