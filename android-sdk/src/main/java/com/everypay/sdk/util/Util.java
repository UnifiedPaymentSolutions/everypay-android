package com.everypay.sdk.util;

import android.text.TextUtils;
import android.util.Base64;

import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class Util {

    public static ErrorHelper execute(Call call) {
        try {
            Response result = call.execute();
            if(result.isSuccessful()) {
                return (ErrorHelper) result.body();
            }
            if (result.errorBody() != null) {
                return new GsonBuilder().create().fromJson(result.errorBody().string(), ErrorHelper.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // base object
            ArrayList<EveryPayError> errors = new ArrayList<>();
            errors.add(new EveryPayError(EveryPayError.EXCEPTION, e.getMessage()));
            return new ErrorHelper(errors);
        }
        // base object
        ArrayList<EveryPayError> errors = new ArrayList<>();
        errors.add(new EveryPayError(EveryPayError.GENERAL_ERROR, "Something went wrong"));
        return new ErrorHelper(errors);
    }

    public static boolean contains(final String haystack, final String needle) {
        return !TextUtils.isEmpty(haystack) && !TextUtils.isEmpty(needle) && haystack.contains(needle);
    }

    public static String getRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        byte [] random = new byte[8];
        secureRandom.nextBytes(random);
        return Base64.encodeToString(random, Base64.NO_WRAP);
    }

    /**
     * Only gets a single value, doesn't support arrays!
     * „https://www.example.ee?result=data1&extra=data2&message=messageText“
     */
    public static String getUrlParamValue(String url, String key, final String fallback) {
        if (TextUtils.isEmpty(url) || !containsIgnoreCase(url, key)) {
            return fallback;
        }
        try {
            final String[] urlParts = url.split("\\?");
            if (urlParts.length < 2) {
                return fallback;
            }
            final String[] queryParts = urlParts[1].split("&");
            for (String queryPart : queryParts) {
                final String[] pair = queryPart.split("=");
                if (pair.length < 2) {
                    continue; // Skip!
                }
                final String currentKey = URLDecoder.decode(pair[0], "UTF-8");
                final String currentValue = URLDecoder.decode(pair[1], "UTF-8");
                if (containsIgnoreCase(currentKey, key)) {
                    return currentValue; // Found it!
                }
            }
            return null;
        } catch (Exception e) {
            Log.getInstance(Util.class).e("getUrlParamValue", e);
        }
        return fallback;
    }

    public static boolean containsIgnoreCase(final String haystack, final String needle) {
        return !TextUtils.isEmpty(haystack) && !TextUtils.isEmpty(needle) && haystack.toLowerCase(Locale.ENGLISH).contains(needle.toLowerCase(Locale.ENGLISH));
    }

}
