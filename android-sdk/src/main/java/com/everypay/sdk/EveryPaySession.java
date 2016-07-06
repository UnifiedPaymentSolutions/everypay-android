package com.everypay.sdk;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.api.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.inter.EveryPay3DsConfirmListener;
import com.everypay.sdk.inter.EveryPayTokenListener;
import com.everypay.sdk.inter.MerchantParamsListener;
import com.everypay.sdk.inter.MerchantPaymentListener;
import com.everypay.sdk.inter.WebAuthListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPay3DsConfirmStep;
import com.everypay.sdk.steps.EveryPayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;
import com.everypay.sdk.util.Log;
import com.everypay.sdk.util.Util;


public class EveryPaySession {


    private static final String EXCEPTION_CARD_IS_NULL = "Card is null";
    private static final String EXCEPTION_LISTENER_IS_NULL = "Listener is null";
    private static final String PAYMENT_STATE_WAITING_FOR_3DS = "waiting_for_3ds_response";
    private static final String TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS = "com.everypay.sdk.TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS";
    private static final String TAG_EVERYPAY_SESSION_SAVE_CARD = "com.everypay.sdk.TAG_EVERYPAY_SESSION_SAVE_CARD";
    private static final String TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT = "com.everypay.sdk.TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT";
    private Handler handler;
    private Context context;
    private String id;
    private EveryPay ep;
    private String apiVersion;
    private String deviceInfo;
    private String accountId;
    private String hmac;
    private EveryPayListener listener;
    private static final Log log = Log.getInstance(EveryPaySession.class);

    private Card card;


    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;
    private EveryPay3DsConfirmStep everyPay3DsConfirmStep;


    public EveryPaySession(Context context, EveryPay ep, Card card, String deviceInfo, EveryPayListener listener, String apiVersion, String accountId) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;
        this.apiVersion = apiVersion;
        this.id = Util.getRandomString();
        this.accountId = accountId;

        if (card == null)
            throw new IllegalArgumentException(EXCEPTION_CARD_IS_NULL);
        this.card = card;

        this.deviceInfo = deviceInfo;

        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException(EXCEPTION_LISTENER_IS_NULL);

        this.merchantParamsStep = ep.getMerchantParamsStep();
        this.everyPayTokenStep = new EveryPayTokenStep();
        this.merchantPaymentStep = ep.getMerchantPaymentStep();
        this.everyPay3DsConfirmStep = new EveryPay3DsConfirmStep();
    }


    public void startPaymentFlow() {
        callStepStarted(merchantParamsStep);
        getMerchantParams(TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS);
    }

    private void getMerchantParams(String tag) {
        log.d("getMerchantParams called");
        merchantParamsStep.run(tag, ep, apiVersion, accountId, new MerchantParamsListener() {
            @Override
            public void onMerchantParamsSucceed(MerchantParamsResponseData responseData) {
                log.d("EverypaySession merchantParams succeed");
                callStepSuccess(merchantParamsStep);
                saveCard(TAG_EVERYPAY_SESSION_SAVE_CARD, responseData);
            }

            @Override
            public void onMerchantParamsFailure(EveryPayError error) {
                log.d("EverypaySession merchantParams failed");
                callStepFailure(merchantParamsStep, error.getMessage());
            }
        });

    }

    private void saveCard(final String tag, final MerchantParamsResponseData merchantParamsResponseData) {
        callStepStarted(everyPayTokenStep);
        log.d("saveCard called");
        everyPayTokenStep.run(tag, ep, merchantParamsResponseData, card, deviceInfo, new EveryPayTokenListener() {
            @Override
            public void onEveryPayTokenSucceed(EveryPayTokenResponseData responseData) {
                log.d("EveryPaySession saveCard succeed");
                callStepSuccess(everyPayTokenStep);
                if(TextUtils.equals(responseData.getPaymentState(), PAYMENT_STATE_WAITING_FOR_3DS)) {
                    startwebViewStep(context,buildUrlForWebView(ep, responseData.getPaymentReference(), responseData.getSecureCodeOne(), merchantParamsResponseData.getHmac()), id, ep);
                } else {
                    merchantPayment(TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT, responseData, merchantParamsResponseData.getHmac());
                }
            }

            @Override
            public void onEveryPayTokenFailure(EveryPayError error) {
                log.d("EveryPaySession saveCard failure");
                callStepFailure(everyPayTokenStep, error.getMessage());
            }
        });
    }

    private void merchantPayment(final String tag, final EveryPayTokenResponseData responseData, final String hmac) {
        log.d("merchantPayment called");
        callStepStarted(merchantPaymentStep);
        merchantPaymentStep.run(tag, ep, hmac, responseData, new MerchantPaymentListener() {
            @Override
            public void onMerchantPaymentSucceed(MerchantPaymentResponseData responseData) {
                log.d("EveryPaySession callMakePayment succeed");
                callStepSuccess(merchantPaymentStep);
                callFullSuccess();
            }

            @Override
            public void onMerchantPaymentFailure(EveryPayError error) {
                log.d("EveryPaySession callMakePayment failure");
                callStepFailure(merchantPaymentStep, error.getMessage());
            }
        });
    }


    private String buildUrlForWebView(EveryPay ep, String paymentReference, String secureCodeOne, String hmac) {
        this.hmac = hmac;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(ep.getEveryPayHost())
                .path("/authentication3ds/new")
                .appendQueryParameter("payment_reference", paymentReference)
                .appendQueryParameter("secure_code_one", secureCodeOne)
                .appendQueryParameter("mobile_3ds_hmac", hmac)
                .build();
        return uri.toString();
    }

    private void startwebViewStep(Context context, String url, String id, EveryPay ep) {
        PaymentBrowserActivity.start(ep, context, url, id, new WebAuthListener() {
            @Override
            public void onWebAuthSucceed(String paymentReference) {
                log.d("EveryPaySession webView finished");
                encryptedPaymentInstrumentsConfirm(TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS, paymentReference);
            }

            @Override
            public void onWebAuthFailure(EveryPayError error) {

            }
        });
    }

    private void encryptedPaymentInstrumentsConfirm(String tag, String paymentReference) {
        log.d("encryptedPaymentInstrumentsConfirm");
        everyPay3DsConfirmStep.run(tag, ep, paymentReference, hmac, apiVersion, new EveryPay3DsConfirmListener() {
            @Override
            public void onEveryPay3DsConfirmSucceed(EveryPayTokenResponseData responseData) {
                log.d("EveryPaySession encryptedPaymentInstrumentsConfirm succeed");
                merchantPayment(TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT, responseData, hmac);
            }

            @Override
            public void onEveryPay3DsConfirmFailure(EveryPayError error) {

            }
        });
    }

    private void callStepStarted(final Step step) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepStarted(step.getType());
                }
            });
        }
    }

    private void callStepSuccess(final Step step) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepSuccess(step.getType());
                }
            });
        }
    }

    private void callFullSuccess() {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.fullSuccess();
                }
            });
        }
    }

    private void callStepFailure(final Step step, final String errorMessage) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepFailure(step.getType(), errorMessage);
                }
            });
        }
    }

}
