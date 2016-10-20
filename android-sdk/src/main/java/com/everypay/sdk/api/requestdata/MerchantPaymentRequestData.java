package com.everypay.sdk.api.requestdata;

import android.support.annotation.Nullable;

import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.google.gson.annotations.SerializedName;

public class MerchantPaymentRequestData {
    @SerializedName("hmac")
    public String hmac;
    @SerializedName("cc_token_encrypted")
    public String ccTokenEncrypted;

    public MerchantPaymentRequestData(String hmac, EveryPayTokenResponseData everypayResponse) {
        this.hmac = hmac;
        this.ccTokenEncrypted = everypayResponse.getToken();
    }
}
