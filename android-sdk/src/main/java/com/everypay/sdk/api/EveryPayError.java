package com.everypay.sdk.api;


import java.io.Serializable;

public class EveryPayError implements Serializable {
    private static final long serialVersionUID = -6753639718147888568L;
    public static final int EXCEPTION = 1000;
    public static final int GENERAL_ERROR = 999;
    public int code;
    public String message;


    public EveryPayError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
