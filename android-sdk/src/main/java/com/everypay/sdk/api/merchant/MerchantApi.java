package com.everypay.sdk.api.merchant;


import com.everypay.sdk.BuildConfig;
import com.everypay.sdk.util.CustomGson;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MerchantApi {

    public static MerchantApiCalls getMerchantApi(String merchantApiUrl) {
        return new RestAdapter.Builder()
                .setEndpoint(merchantApiUrl)
                .setConverter(new GsonConverter(CustomGson.getInstance()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
                .build()
                .create(MerchantApiCalls.class);
    }

}
