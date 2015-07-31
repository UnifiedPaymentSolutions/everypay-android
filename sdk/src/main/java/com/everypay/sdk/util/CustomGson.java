package com.everypay.sdk.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomGson {
    private static Gson gson;

    public static synchronized Gson getInstance() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            gson = builder.create();
        }
        return gson;
    }
}
