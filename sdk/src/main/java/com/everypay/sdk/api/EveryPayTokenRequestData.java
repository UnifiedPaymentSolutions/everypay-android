package com.everypay.sdk.api;

import com.everypay.sdk.model.Card;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;

import java.util.Map;


public class EverypayTokenRequestData {

    public EncryptedTokenRequestData encryptedPaymentInstrument;

    public EverypayTokenRequestData(MerchantParamsResponseData params, Card card, Map<String, Object> collectorResult) {
        this.encryptedPaymentInstrument = new EncryptedTokenRequestData(params, card, collectorResult);
    }

    private static class EncryptedTokenRequestData {
        public EncryptedTokenRequestData(MerchantParamsResponseData params, Card card, Map<String, Object> collectorResult) {
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

            //this.deviceFingerprint = collectorResult;
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

        //Map<String, Object> deviceFingerprint;
    }

}
