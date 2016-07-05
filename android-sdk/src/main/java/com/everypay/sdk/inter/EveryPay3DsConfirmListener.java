package com.everypay.sdk.inter;

import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;

public interface EveryPay3DsConfirmListener extends ServiceListener {

    void onEveryPay3DsConfirmSucceed(EveryPayTokenResponseData responseData);

    void onEveryPay3DsConfirmFailure(ErrorHelper error);
}
