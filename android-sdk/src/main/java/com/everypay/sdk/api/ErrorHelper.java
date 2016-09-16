package com.everypay.sdk.api;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ErrorHelper implements Serializable {
    private static final long serialVersionUID = -7025174880520305308L;

    @SerializedName("errors")
    public ArrayList<EveryPayError> errors;

    public ErrorHelper(ArrayList<EveryPayError> errors) {
        this.errors = errors;
    }

    public ArrayList<EveryPayError> getErrors() {
        return errors;
    }

    public boolean isError() {
        return errors != null && getErrors().size() != 0;
    }
}
