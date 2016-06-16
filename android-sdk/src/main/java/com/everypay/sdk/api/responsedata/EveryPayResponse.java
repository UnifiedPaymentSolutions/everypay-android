package com.everypay.sdk.api.responsedata;

import com.everypay.sdk.api.EveryPayError;

import java.io.Serializable;
import java.util.ArrayList;

public class EveryPayResponse implements Serializable {

    private static final long serialVersionUID = 4127967190779445752L;

    private String message;

    public EveryPayResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
