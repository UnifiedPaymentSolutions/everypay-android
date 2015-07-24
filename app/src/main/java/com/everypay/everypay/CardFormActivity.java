package com.everypay.everypay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.braintreepayments.cardform.view.CardForm;


public class CardFormActivity extends AppCompatActivity {

    CardForm cardForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardform);

        cardForm = (CardForm)findViewById(R.id.card_form);
        cardForm.setRequiredFields(this, true, true, true, false, "Purchase");
    }

}
