package com.everypay.sdk.steps;

import android.support.annotation.Nullable;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.requestdata.MerchantParamsRequestData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.inter.EveryPayCallback;
import com.everypay.sdk.inter.MerchantParamsListener;
import com.everypay.sdk.util.Log;

import retrofit2.Call;
import retrofit2.Response;

public class MerchantParamsStep extends Step {

    private static final Log log = Log.getInstance(MerchantParamsStep.class);

    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PARAMS;
    }

    public void run(final String tag, final EveryPay ep, String apiVersion, String accountId, final MerchantParamsListener listener) {
        log.d("MerchantParamsStep run called");
        if (ep != null) {
            ep.setListener(tag, listener);
        }
        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(ep.getContext(), ep.getMerchantUrl()).getApiCalls();
        MerchantParamsRequestData requestData = new MerchantParamsRequestData(apiVersion, accountId);
        Call<MerchantParamsResponseData> call = apiCalls.callGetParams(requestData);
        call.enqueue(new EveryPayCallback<MerchantParamsResponseData>() {

            @Override
            public void onSuccess(Call<MerchantParamsResponseData> call, Response<MerchantParamsResponseData> response) {
                log.d("callGetParams successful : " + response.body().toString());
                final MerchantParamsListener listener = ep.getListener(tag, true, MerchantParamsListener.class);
                if (listener != null) {
                    listener.onMerchantParamsSucceed(response.body());
                }
            }

            @Override
            public void onFailure(Call<MerchantParamsResponseData> call, @Nullable EveryPayError error, @Nullable Throwable t) {
                log.d("callGetParams failure");
                final MerchantParamsListener listener = ep.getListener(tag, true, MerchantParamsListener.class);
                if (listener != null) {
                    listener.onMerchantParamsFailure(EveryPayError.from(ep.getContext(), error, t));
                }
            }
        });
    }
}
