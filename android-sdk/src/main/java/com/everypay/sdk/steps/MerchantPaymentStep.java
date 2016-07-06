package com.everypay.sdk.steps;


import android.support.annotation.Nullable;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.api.requestdata.MerchantPaymentRequestData;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.inter.EveryPayCallback;
import com.everypay.sdk.inter.MerchantPaymentListener;
import com.everypay.sdk.util.Log;

import retrofit2.Call;
import retrofit2.Response;


public class MerchantPaymentStep extends Step {

    private static final Log log = Log.getInstance(MerchantPaymentStep.class);
    @Override
    public final StepType getType() {
        return StepType.MERCHANT_PAYMENT;
    }

    public void run(final String tag, final EveryPay ep, String hmac, EveryPayTokenResponseData everypayResponse, MerchantPaymentListener listener) {
        log.d("MerchantPaymentStep callMakePaymentStarted");
        if(ep != null) {
            ep.setListener(tag, listener);
        }
        MerchantApi.MerchantApiCalls apiCalls = MerchantApi.getInstance(ep.getContext(), ep.getMerchantUrl()).getApiCalls();
        MerchantPaymentRequestData requestData = new MerchantPaymentRequestData(hmac, everypayResponse);
        final Call<MerchantPaymentResponseData> call = apiCalls.callMakePayment(requestData);
        call.enqueue(new EveryPayCallback<MerchantPaymentResponseData>() {
            @Override
            public void onSuccess(Call<MerchantPaymentResponseData> call, Response<MerchantPaymentResponseData> response) {
                log.d("callMakePayment success");
                final MerchantPaymentListener listener = ep.getListener(tag, true, MerchantPaymentListener.class);
                if(listener != null) {
                    listener.onMerchantPaymentSucceed(response.body());
                }
            }

            @Override
            public void onFailure(Call<MerchantPaymentResponseData> call, @Nullable EveryPayError error, @Nullable Throwable t) {
                log.d("callMakePayment failed");
                final MerchantPaymentListener listener = ep.getListener(tag, true, MerchantPaymentListener.class);
                if(listener != null) {
                    listener.onMerchantPaymentFailure(EveryPayError.from(ep.getContext(), error, t));
                }
            }
        });
    }
}
