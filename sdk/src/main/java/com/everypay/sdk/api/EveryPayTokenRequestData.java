package com.everypay.sdk.api;

import com.everypay.sdk.api.merchant.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;


public class EverypayTokenRequestData {

    public EncryptedTokenRequestData encryptedPaymentInstrument;

    public EverypayTokenRequestData(MerchantParamsResponseData params, Card card, String deviceInfo) {
        this.encryptedPaymentInstrument = new EncryptedTokenRequestData(params, card, deviceInfo);
    }

    private static class EncryptedTokenRequestData {
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
        }

        public String apiUsername;  // Merchant ID
        public String accountId;
        public String userIp;

        public String hmac;
        public String nonce;
        public long timestamp;

        public String ccHolderName;
        public String ccNumber;
        public String ccMonth;
        public String ccYear;
        public String ccVerification;

        String deviceInfo;
    }

}
