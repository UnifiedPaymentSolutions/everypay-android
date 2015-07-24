package com.everypay.sdk;

import com.everypay.sdk.steps.StepType;

public interface EveryPayListener {
    void stepSuccess(StepType step);
    void fullSuccess();
    void stepFailure(StepType step);
}
