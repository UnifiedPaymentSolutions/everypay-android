package com.everypay.sdk.views;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.EditText;

import com.everypay.sdk.R;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.model.CardError;
import com.everypay.sdk.util.Reflect;

public class CardFormTextWatcher implements TextWatcher {

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
        Reflect.setString(card, "set" + fieldName, s.toString());
        s.replace(0, s.length(), Reflect.getString(card, "get" + fieldName));

        Context context = input.getContext();
        Resources res = context.getResources();
        try {
            Reflect.call(card, "validate" + fieldName, new Pair<Class, Object>(Context.class, context));
            input.setTextColor(res.getColor(R.color.ep_card_field_normal));
        } catch (CardError e) {
            if (e.isPartialOk()) {
                input.setTextColor(res.getColor(R.color.ep_card_field_normal));
            } else {
                input.setTextColor(res.getColor(R.color.ep_card_field_invalid));
            }
        } catch (Throwable tr) {
            throw new RuntimeException("Unexpected exception from card validation.", tr);
        }
    }

}
