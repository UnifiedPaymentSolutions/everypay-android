package com.everypay.sdk.inter;

import com.everypay.sdk.api.EveryPayError;

public interface WebAuthListener extends ServiceListener {

    void onWebAuthSucceed(String paymentReference);

    void onWebAuthFailure(EveryPayError error);

    void onWebAuthCanceled(EveryPayError error);
}
