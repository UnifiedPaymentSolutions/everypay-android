package com.everypay.sdk.api;

import com.everypay.sdk.Card;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;


public class EverypayTokenRequestData {

    public SingleUseTokenRequestData singleUseToken;

    public EverypayTokenRequestData(MerchantParamsResponseData params, Card card) {
        this.singleUseToken = new SingleUseTokenRequestData(params, card);
    }

    private static class SingleUseTokenRequestData {
        public SingleUseTokenRequestData(MerchantParamsResponseData params, Card card) {
            this.hmac = params.hmac;
            this.nonce = params.nonce;
            this.timestamp = params.timestamp;
            this.apiUsername = params.apiUsername;

            this.ccHolderName = card.getName();
            this.ccNumber = card.getNumber();
            this.ccYear = card.getExpYear();
            this.ccMonth = card.getExpMonth();
            //this.ccVerification = card.getCVC();
        }

        public String hmac;
        public String nonce;
        public long timestamp;
        public String apiUsername;  // Merchant ID

        public String ccHolderName;
        public String ccNumber;
        public int ccMonth;
        public int ccYear;
        //public String ccVerification;
    }

}
