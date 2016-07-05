package com.everypay.sdk.inter;

import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;

public interface MerchantParamsListener extends ServiceListener {

    void onMerchantParamsSucceed(MerchantParamsResponseData responseData);

    void onMerchantParamsFailure(ErrorHelper error);
}
