package com.everypay.sdk.api;


import com.everypay.sdk.BuildConfig;

import retrofit.RestAdapter;

public class EverypayApi {

    public static EverypayApiCalls getEverypayApi(String everyPayApiUrl) {
        return new RestAdapter.Builder()
                .setEndpoint(everyPayApiUrl)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
                .build()
                .create(EverypayApiCalls.class);
    }

}
