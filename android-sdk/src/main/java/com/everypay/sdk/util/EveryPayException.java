package com.everypay.sdk.util;

public class EveryPayException extends RuntimeException {

    private static final long serialVersionUID = -3213806648620958790L;

    private final long errorCode;

    public EveryPayException(final long errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public long getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "EveryPayException{" +
                "errorCode=" + errorCode +
                ", errorMessage=" + getMessage() +
                '}';
    }
}
