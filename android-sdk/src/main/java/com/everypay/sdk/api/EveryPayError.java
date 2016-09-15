package com.everypay.sdk.api;


import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.R;

import java.io.IOException;
import java.io.Serializable;

import javax.net.ssl.SSLException;

public class EveryPayError implements Serializable {
    private static final long serialVersionUID = -6753639718147888568L;
    public static final int EXCEPTION = 1000;
    public static final int GENERAL_ERROR = 999;
    public static final long ERROR_CODE_NETWORK_FAILURE = 1001;
    private static final long ERROR_CODE_SSL_EXCEPTION = 1002;
    public static  final long ERROR_WEB_AUTH_FAILED = 1003;
    public static  final long ERROR_HTTP = 1004;
    public long code;
    public String message;

    public EveryPayError() {
        this(GENERAL_ERROR, null);
    }

    public EveryPayError(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public static EveryPayError from(final Context appContext, final EveryPayError error, Throwable t) {
        if (error != null) {
            return error;
        } else if (t != null) {
            return from(appContext, t);
        }
        return new EveryPayError();
    }

    public static EveryPayError from(final Context appContext, @Nullable Throwable t) {
        final EveryPayError error = new EveryPayError();
        if (t instanceof SSLException) {
            error.code = ERROR_CODE_SSL_EXCEPTION;
            error.message = appContext.getString(R.string.ep_err_ssl_exception);
        } else if (t instanceof IOException) {
            error.code = ERROR_CODE_NETWORK_FAILURE;
            error.message = appContext.getString(R.string.ep_err_network_failure);
        } else {
            error.code = GENERAL_ERROR;
            error.message = !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : appContext.getString(R.string.ep_err_unknown);
        }
        return error;
    }
    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
