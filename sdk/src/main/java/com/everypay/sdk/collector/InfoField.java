package com.everypay.sdk.collector;


public class InfoField {

    transient public String name;
    public String error;
    public Object value;

    public InfoField(String name, FieldError error) {
        this.name = name;
        this.error = error.toString().toLowerCase();
    }

    public InfoField(String name, Object value) {
        this.name = name;
        this.value = value;
    }

}
