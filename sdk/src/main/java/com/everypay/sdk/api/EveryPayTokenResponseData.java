package com.everypay.sdk.api;


import android.text.TextUtils;

public class EverypayTokenResponseData {
    public EncryptedTokenResponseData encryptedPaymentInstrument;

    public String getToken() {
        if (encryptedPaymentInstrument != null && !TextUtils.isEmpty(encryptedPaymentInstrument.ccTokenEncrypted))
            return encryptedPaymentInstrument.ccTokenEncrypted;
        return "";
    }

    public static class EncryptedTokenResponseData {
        String ccTokenEncrypted;
    }
}
