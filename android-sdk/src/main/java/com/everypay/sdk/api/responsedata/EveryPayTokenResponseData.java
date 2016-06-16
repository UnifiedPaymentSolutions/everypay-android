package com.everypay.sdk.api.responsedata;


import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;

import java.util.ArrayList;

public class EveryPayTokenResponseData extends ErrorHelper {
    private static final long serialVersionUID = -5232203392021227683L;
    public EncryptedTokenResponseData encryptedPaymentInstrument;

    public EveryPayTokenResponseData(EncryptedTokenResponseData encryptedPaymentInstrument, ArrayList<EveryPayError> errors) {
        super(errors);
        this.encryptedPaymentInstrument = encryptedPaymentInstrument;

    }

    public String getToken() {
        if (encryptedPaymentInstrument != null) {
            return encryptedPaymentInstrument.ccTokenEncrypted;
        }
        return null;

    }

    public String getPaymentState() {
        if(encryptedPaymentInstrument != null) {
            return encryptedPaymentInstrument.paymentState;
        }
        return null;

    }
    public String getPaymentReference() {
        if(encryptedPaymentInstrument != null) {
            return encryptedPaymentInstrument.paymentReference;
        }
        return null;

    }
    public String getSecureCodeOne() {
        if(encryptedPaymentInstrument != null) {
            return encryptedPaymentInstrument.secureCodeOne;
        }
        return null;

    }

    @Override
    public String toString() {
        return "EveryPayTokenResponseData{" +
                "encryptedPaymentInstrument=" + encryptedPaymentInstrument.toString() +
                '}';
    }

    public static class EncryptedTokenResponseData {
        String ccTokenEncrypted;
        String paymentState;
        String paymentReference;
        String secureCodeOne;


        @Override
        public String toString() {
            return "EncryptedTokenResponseData{" +
                    "ccTokenEncrypted='" + ccTokenEncrypted + '\'' +
                    ", paymentState='" + paymentState + '\'' +
                    ", paymentReference='" + paymentReference + '\'' +
                    ", secureCodeOne='" + secureCodeOne + '\'' +
                    '}';
        }
    }
}
