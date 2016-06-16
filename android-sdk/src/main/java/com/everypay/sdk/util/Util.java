package com.everypay.sdk.util;

import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

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
}
