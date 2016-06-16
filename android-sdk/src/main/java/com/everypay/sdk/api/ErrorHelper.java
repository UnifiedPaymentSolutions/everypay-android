package com.everypay.sdk.api;


import java.io.Serializable;
import java.util.ArrayList;

public class ErrorHelper implements Serializable {
    private static final long serialVersionUID = -7025174880520305308L;

    public ArrayList<EveryPayError> errors;

    public ErrorHelper(ArrayList<EveryPayError> errors) {
        this.errors = errors;
    }

    public ArrayList<EveryPayError> getErrors() {
        return errors;
    }

    public boolean isError() {
        return getErrors() != null && getErrors().size() != 0;
    }
}
