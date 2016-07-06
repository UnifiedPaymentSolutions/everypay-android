package com.everypay.sdk.inter;

import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;

public interface EveryPayTokenListener extends ServiceListener {

    void onEveryPayTokenSucceed(EveryPayTokenResponseData responseData);

    void onEveryPayTokenFailure(EveryPayError error);
}
