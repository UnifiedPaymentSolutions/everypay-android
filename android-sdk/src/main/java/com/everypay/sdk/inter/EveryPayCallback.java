package com.everypay.sdk.inter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class EveryPayCallback<T> implements Callback<T> {

   @Override
   public void onResponse(Call<T> call, Response<T> response) {
       ErrorHelper resp = getBody(response);
       if(resp == null) {
           onFailure(call, null, new RuntimeException());
           return;
       }
       if(resp.isError()) {
           onFailure(call, resp.getErrors().get(0), null);
           return;
       }
       onSuccess(call, response);
   }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure(call, null, t);
    }

    private ErrorHelper getBody(final @NonNull Response<T> response) {
        if(response.body() != null) {
            return (ErrorHelper) response.body();
        }
        return Util.getErrorsIfAny(response.errorBody());
    }
    public abstract void onSuccess(Call<T> call, Response<T> response);
    public abstract void onFailure(Call<T> call, @Nullable EveryPayError error, @Nullable Throwable t);
}
