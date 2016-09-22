package com.everypay.sdk;

import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.inter.ServiceListener;
import com.everypay.sdk.steps.StepType;

public interface EveryPayListener extends ServiceListener{
    void stepStarted(StepType step);
    void stepSuccess(StepType step);
    void fullSuccess(MerchantPaymentResponseData responseData);
    void stepFailure(StepType step, EveryPayError errorMessage);
}
