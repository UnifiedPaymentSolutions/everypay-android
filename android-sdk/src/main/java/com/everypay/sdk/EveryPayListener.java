package com.everypay.sdk;

import com.everypay.sdk.inter.ServiceListener;
import com.everypay.sdk.steps.StepType;

public interface EveryPayListener extends ServiceListener{
    void stepStarted(StepType step);
    void stepSuccess(StepType step);
    void fullSuccess();
    void stepFailure(StepType step, String errorMessage);
}
