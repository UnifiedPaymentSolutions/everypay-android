package com.everypay.sdk.api;


import com.everypay.sdk.Config;
import com.everypay.sdk.util.CustomGson;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class EveryPayApi1 {

    public static EveryPayApiCalls1 getEverypayApi(String everyPayApiUrl) {
        return new RestAdapter.Builder()
                .setEndpoint(everyPayApiUrl)
                .setConverter(new GsonConverter(CustomGson.getInstance()))
                .setLogLevel(Config.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
                .build()
                .create(EveryPayApiCalls1.class);
    }

}
