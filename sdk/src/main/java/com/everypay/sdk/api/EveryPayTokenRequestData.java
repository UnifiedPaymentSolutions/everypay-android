package com.everypay.sdk.api;

import com.everypay.sdk.model.Card;
import com.everypay.sdk.api.merchant.MerchantParamsResponseData;

import java.util.Map;


public class EverypayTokenRequestData {

    public SingleUseTokenRequestData singleUseToken;

    public EverypayTokenRequestData(MerchantParamsResponseData params, Card card, Map<String, Object> collectorResult) {
        this.singleUseToken = new SingleUseTokenRequestData(params, card, collectorResult);
    }

    private static class SingleUseTokenRequestData {
        public SingleUseTokenRequestData(MerchantParamsResponseData params, Card card, Map<String, Object> collectorResult) {
            this.hmac = params.hmac;
            this.nonce = params.nonce;
            this.timestamp = params.timestamp;
            this.apiUsername = params.apiUsername;

            this.ccHolderName = card.getName();
            this.ccNumber = card.getNumber();
            this.ccYear = card.getExpYearInt();
            this.ccMonth = card.getExpMonthInt();
            //this.ccVerification = card.getCVC();

            this.deviceFingerprint = collectorResult;
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

        Map<String, Object> deviceFingerprint;
    }

}
