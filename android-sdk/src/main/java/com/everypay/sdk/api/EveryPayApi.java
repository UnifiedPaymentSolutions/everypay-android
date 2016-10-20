package com.everypay.sdk.api;


import android.content.Context;

import com.everypay.sdk.Config;
import com.everypay.sdk.api.requestdata.EveryPayTokenRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.util.CustomGson;
import com.everypay.sdk.util.Log;
import com.google.android.gms.security.ProviderInstaller;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public class EveryPayApi {
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
    private static volatile EveryPayApi instance;
    private final EveryPayApiCalls apiCalls;

    public EveryPayApi(final Context appContext, final String baseUrl) {
        patchSSLProvider(appContext);
        final HttpLoggingInterceptor interceptorLogging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                log.d(message);
            }
        });
        interceptorLogging.setLevel(Config.USE_DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        final ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                .supportsTlsExtensions(true)
                .build();
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
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiCalls = retrofit.create(EveryPayApiCalls.class);
    }

    public EveryPayApiCalls getApiCalls() {
        return apiCalls;
    }

    public static EveryPayApi getInstance(final Context appContext, final String baseUrl) {
        if (instance == null) {
            synchronized (EveryPayApi.class) {
                if (instance == null) {
                    createNewInstance(appContext, baseUrl);
                }
            }
        }
        return instance;
    }

    private void patchSSLProvider(final Context applicationContext) {
        try {
            ProviderInstaller.installIfNeeded(applicationContext);
            log.d("patchSSLProvider patched");
        } catch (Exception e) {
            log.d("patchSSLProvider", e);
        }
    }

    public static EveryPayApi createNewInstance(Context appContext, String baseUrl) {
        synchronized (EveryPayApi.class) {
            instance = new EveryPayApi(appContext, baseUrl);
            return instance;
        }
    }


    public interface EveryPayApiCalls {
        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("encrypted_payment_instruments")
        Call<EveryPayTokenResponseData> saveCard(@Body EveryPayTokenRequestData params);


        @GET("encrypted_payment_instruments/{paymentReference}")
        Call<EveryPayTokenResponseData> encryptedPaymentInstrumentConfirmed(@Path("paymentReference") String paymentReference, @QueryMap Map<String, String> params);

        @GET("authentication3ds/new")
        Call<String> auth(@QueryMap Map<String, String> params);
    }

}
