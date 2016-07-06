package com.everypay.sdk.inter;

import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;

public interface MerchantParamsListener extends ServiceListener {

    void onMerchantParamsSucceed(MerchantParamsResponseData responseData);

    void onMerchantParamsFailure(EveryPayError error);
}
