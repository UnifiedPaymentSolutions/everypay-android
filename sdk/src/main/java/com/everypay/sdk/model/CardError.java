package com.everypay.sdk.model;


import android.content.Context;

public class CardError extends Exception {

    boolean partialOk;

    public CardError(boolean partialOk, String detailMessage) {
        super(detailMessage);
        this.partialOk = partialOk;
    }

    public static CardError create(Context context, boolean partialOk, int errorStringId) {
        return new CardError(partialOk, context.getString(errorStringId));
    }

    public static void raise(Context context, boolean partialOk, int errorStringId) throws CardError {
        throw create(context, partialOk, errorStringId);
    }

    public boolean isPartialOk() {
        return partialOk;
    }
}
