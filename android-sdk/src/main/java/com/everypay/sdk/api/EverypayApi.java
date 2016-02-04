package com.everypay.sdk.api;


import com.everypay.sdk.BuildConfig;
import com.everypay.sdk.util.CustomGson;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class EveryPayApi {

    public static EveryPayApiCalls getEverypayApi(String everyPayApiUrl) {
        return new RestAdapter.Builder()
                .setEndpoint(everyPayApiUrl)
                .setConverter(new GsonConverter(CustomGson.getInstance()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
                .build()
                .create(EveryPayApiCalls.class);
    }

}
