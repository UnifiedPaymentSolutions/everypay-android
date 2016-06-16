package com.everypay.sdk.api.responsedata;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 4127967190779445752L;

    private String message;

    public BaseResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
