package com.everypay.sdk.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.EditText;

import com.everypay.sdk.R;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.model.CardError;
import com.everypay.sdk.util.Reflect;

public class CardFormTextWatcher implements TextWatcher {

    private static final String METHOD_SET = "set";
    private static final String METHOD_VALIDATE = "validate";
    boolean selfChange = false;

    EditText input;
    Card card;
    String fieldName;

    public CardFormTextWatcher(EditText input, Card card, String fieldName) {
        this.input = input;
        this.card = card;
        this.fieldName = fieldName;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (selfChange)
            return;

        selfChange = true;
        reformat(s);
        selfChange = false;
    }

    private void reformat(Editable s) {
        Reflect.setString(card, METHOD_SET + fieldName, s.toString());

        Context context = input.getContext();
        Resources res = context.getResources();
        try {
            Reflect.call(card, METHOD_VALIDATE + fieldName, new Pair<Class, Object>(Context.class, context));
            input.setTextColor(ContextCompat.getColor(context,R.color.ep_card_field_normal));
        } catch (CardError e) {
            if (e.isPartialOk()) {
                input.setTextColor(ContextCompat.getColor(context,R.color.ep_card_field_normal));
            } else {
                input.setTextColor(ContextCompat.getColor(context,R.color.ep_card_field_invalid));
            }
        } catch (Throwable tr) {
            throw new RuntimeException("Unexpected exception from card validation.", tr);
        }
    }

}
