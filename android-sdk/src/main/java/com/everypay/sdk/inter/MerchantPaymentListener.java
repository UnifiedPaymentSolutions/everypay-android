package com.everypay.sdk.inter;

import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;

public interface MerchantPaymentListener extends ServiceListener {

    void onMerchantPaymentSucceed(MerchantPaymentResponseData responseData);

    void onMerchantPaymentFailure(EveryPayError error);
}
