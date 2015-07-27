package com.everypay.sdk.steps;


import com.everypay.sdk.EveryPay;

public abstract class Step<I, O> {
    public abstract StepType getType();
    public abstract I makeRequestData(Object o);
    public abstract O run(EveryPay ep, I requestData);
}
