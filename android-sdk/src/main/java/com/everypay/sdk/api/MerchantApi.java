package com.everypay.sdk.api;


import android.content.Context;

import com.everypay.sdk.Config;
import com.everypay.sdk.api.requestdata.MerchantParamsRequestData;
import com.everypay.sdk.api.requestdata.MerchantPaymentRequestData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.util.CustomGson;
import com.everypay.sdk.util.Log;
import com.google.android.gms.security.ProviderInstaller;


import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class MerchantApi {
    /**
     * Connect timeout value. In milliseconds.
     */
    private static final long TIMEOUT_CONNECT = 10 * 1000L;
    /**
     * Read timeout value. In milliseconds.
     */
    private static final long TIMEOUT_READ = 15 * 1000L;
    /**
     * Write timeout value. In milliseconds.
     */
    private static final long TIMEOUT_WRITE = 15 * 1000L;
    private static final Log log = Log.getInstance(EveryPayApi.class);
    private static volatile MerchantApi instance;
    private final MerchantApi.MerchantApiCalls apiCalls;

    public MerchantApi(final Context appContext, final String baseUrl) {
        patchSSLProvider(appContext);
        final HttpLoggingInterceptor interceptorLogging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                log.d(message);
            }
        });
        interceptorLogging.setLevel(Config.USE_DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        final ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build();

        final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT_WRITE, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptorLogging)
                .connectionSpecs(Collections.singletonList(connectionSpec));
        final OkHttpClient client = okHttpBuilder.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(CustomGson.getInstance()))
                .build();
        apiCalls = retrofit.create(MerchantApi.MerchantApiCalls.class);
    }

    public MerchantApi.MerchantApiCalls getApiCalls() {
        return apiCalls;
    }

    public static MerchantApi getInstance(final Context appContext, final String baseUrl) {
        if(instance == null) {
            synchronized (EveryPayApi.class) {
                if(instance == null) {
                    createNewInstance(appContext, baseUrl);
                }
            }
        }
        return instance;
    }

    public static MerchantApi createNewInstance(Context appContext, String baseUrl) {
        synchronized (MerchantApi.class) {
            instance = new MerchantApi(appContext, baseUrl);
            return instance;
        }
    }

    private void patchSSLProvider(final Context applicationContext) {
        try {
            ProviderInstaller.installIfNeeded(applicationContext);
            log.d("patchSSLProvider patched");
        } catch (Exception e) {
            log.d("patchSSLProvider", e);
        }
    }

    public interface MerchantApiCalls {
        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("/merchant_mobile_payments/generate_token_api_parameters")
        Call<MerchantParamsResponseData> callGetParams(@Body MerchantParamsRequestData params);

        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("/merchant_mobile_payments/pay")
        Call<MerchantPaymentResponseData> callMakePayment(@Body MerchantPaymentRequestData params);
    }

}
