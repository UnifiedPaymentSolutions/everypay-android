package com.everypay.sdk.api;


import com.everypay.sdk.BuildConfig;

import retrofit.RestAdapter;

public class MerchantApi {

    public static MerchantApiCalls getMerchantApi(String merchantApiUrl) {
        return new RestAdapter.Builder()
                .setEndpoint(merchantApiUrl)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
                .build()
                .create(MerchantApiCalls.class);
    }

}
