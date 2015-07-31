package com.everypay.sdk.api;


import android.text.TextUtils;

public class EverypayTokenResponseData {
    public SingleUseTokenResponseData singleUseToken;

    public String getToken() {
        if (singleUseToken != null && !TextUtils.isEmpty(singleUseToken.singleCcToken))
            return singleUseToken.singleCcToken;
        return "";
    }

    public static class SingleUseTokenResponseData {
        String singleCcToken;
    }
}
