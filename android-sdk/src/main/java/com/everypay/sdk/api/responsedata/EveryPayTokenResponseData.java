package com.everypay.sdk.api.responsedata;


import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EveryPayTokenResponseData extends ErrorHelper {
    private static final long serialVersionUID = -5232203392021227683L;
    @SerializedName("encrypted_payment_instrument")
    public EncryptedTokenResponseData encryptedPaymentInstrument;

    public EveryPayTokenResponseData(ArrayList<EveryPayError> errors) {
        super(errors);
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
        @SerializedName("cc_token_encrypted")
        String ccTokenEncrypted;
        @SerializedName("payment_state")
        String paymentState;
        @SerializedName("payment_reference")
        String paymentReference;
        @SerializedName("secure_code_one")
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
