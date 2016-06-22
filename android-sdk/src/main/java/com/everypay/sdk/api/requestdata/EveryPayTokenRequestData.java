package com.everypay.sdk.api.requestdata;

import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;


public class EveryPayTokenRequestData {

    public EncryptedTokenRequestData encryptedPaymentInstrument;


    public EveryPayTokenRequestData(MerchantParamsResponseData params, Card card, String deviceInfo) {
        this.encryptedPaymentInstrument = new EncryptedTokenRequestData(params, card, deviceInfo);
    }

    private static class EncryptedTokenRequestData {

        String apiUsername;  // Merchant ID
        String accountId;
        String userIp;

        String hmac;
        String nonce;
        long timestamp;

        String ccHolderName;
        String ccNumber;
        String ccMonth;
        String ccYear;
        String ccVerification;

        String apiVersion;

        String deviceInfo;
        String orderReference;

        public EncryptedTokenRequestData(MerchantParamsResponseData params, Card card, String deviceInfo) {
            this.apiUsername = params.apiUsername;
            this.accountId = params.accountId;
            this.userIp = params.userIp;
            this.hmac = params.hmac;
            this.nonce = params.nonce;
            this.timestamp = params.timestamp;

            this.ccHolderName = card.getName();
            this.ccNumber = card.getNumber();
            this.ccYear = card.getExpYear();
            this.ccMonth = card.getExpMonth();
            this.ccVerification = card.getCVC();

            this.deviceInfo = deviceInfo;

            this.apiVersion = params.apiVersion;
            this.orderReference = params.orderReference;
        }

        @Override
        public String toString() {
            return "EncryptedTokenRequestData{" +
                    "apiUsername='" + apiUsername +
                    ", accountId='" + accountId +
                    ", userIp='" + userIp +
                    ", hmac='" + hmac +
                    ", nonce='" + nonce +
                    ", timestamp=" + timestamp +
                    ", ccHolderName='" + ccHolderName +
                    ", ccNumber='" + ccNumber +
                    ", ccMonth='" + ccMonth +
                    ", ccYear='" + ccYear +
                    ", ccVerification='" + ccVerification +
                    ", apiVersion='" + apiVersion +
                    ", deviceInfo='" + deviceInfo +
                    ", orderReference='" + orderReference +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EveryPayTokenRequestData{" +
                "encryptedPaymentInstrument=" + encryptedPaymentInstrument.toString() +
                '}';
    }
}
