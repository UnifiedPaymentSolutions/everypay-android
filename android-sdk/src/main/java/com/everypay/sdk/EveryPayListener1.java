package com.everypay.sdk;

import com.everypay.sdk.steps.StepType;

public interface EveryPayListener1 {
    void stepStarted(StepType step);
    void stepSuccess(StepType step);
    void fullSuccess();
    void stepFailure(StepType step, Exception e);
}
