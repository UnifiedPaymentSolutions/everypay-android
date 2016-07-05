package com.everypay.sdk.inter;

import android.support.annotation.Nullable;

import com.everypay.sdk.api.ErrorHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface EveryayCallback<T> extends Callback<T> {

    @Override
    void onResponse(Call<T> call, Response<T> response);

    @Override
    void onFailure(Call<T> call, Throwable t);

    public abstract void onFailure(Call<T> call, @Nullable ErrorHelper errorHelper, @Nullable Throwable t);
}
