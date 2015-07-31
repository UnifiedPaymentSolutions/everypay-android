package com.everypay.sdk.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.everypay.sdk.Card;
import com.everypay.sdk.R;


public class CardFormActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 13423;

    public static void startForResult(Activity activity) {
        Intent intent = new Intent(activity, CardFormActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static Card getCardFromResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return (Card)data.getParcelableExtra("card");
        }
        return null;
    }

    EditText holderName;
    EditText number;
    EditText cvc;
    EditText month;
    EditText year;
    Button done;

    int colorNormal;
    int colorInvalid;
    int colorValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardform);

        holderName = (EditText)findViewById(R.id.cc_holder_name);
        number = (EditText)findViewById(R.id.cc_number);
        cvc = (EditText)findViewById(R.id.cc_cvc);
        month = (EditText)findViewById(R.id.cc_month);
        year = (EditText)findViewById(R.id.cc_year);
        done = (Button)findViewById(R.id.btn_done);

        colorNormal = getResources().getColor(R.color.ep_card_field_normal);
        colorInvalid = getResources().getColor(R.color.ep_card_field_invalid);
        colorValid = getResources().getColor(R.color.ep_card_field_valid);

        attachUiEvents();
        setResult(RESULT_CANCELED, null);
    }

    private void attachUiEvents() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = visualValidate(true);
                if (card != null) {
                    Intent result = new Intent();
                    result.putExtra("card", card);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    setResult(RESULT_CANCELED, null);
                }
            }
        });

    }

    private Card visualValidate(boolean toastFirstError) {
        Card card = new Card.Builder()
                .name(holderName.getText().toString().trim())
                .number(number.getText().toString().trim())
                .cvc(cvc.getText().toString().trim())
                .expMonth(month.getText().toString().trim())
                .expYear(year.getText().toString().trim())
                .build();

        toastFirstError = visualValidateField(card.validateName(), holderName, toastFirstError, R.string.cc_holder_name_missing);
        toastFirstError = visualValidateField(card.validateNumber(), number, toastFirstError, R.string.cc_number_invalid);
        toastFirstError = visualValidateField(card.validateCVC(), cvc, toastFirstError, R.string.cc_cvc_invalid);
        toastFirstError = visualValidateField(card.validateExpMonth(), month, toastFirstError, R.string.cc_month_invalid);
        toastFirstError = visualValidateField(card.validateExpYear(), year, toastFirstError, R.string.cc_year_invalid);

        if (card.validateCard()) {
            return card;
        } else {
            if (toastFirstError) {
                toast(R.string.cc_other_invalid);
            }
            return null;
        }
    }

    private boolean visualValidateField(boolean isValid, EditText input, boolean toastError, int errorToastId) {
        if (isValid) {
            input.setTextColor(colorValid);
        } else {
            input.setTextColor(colorInvalid);

            if (toastError) {
                toast(errorToastId);
                toastError = false;
            }
        }
        return toastError;
    }

    private void toast(String fmt, Object... args) {
        String msg = String.format(fmt, args);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toast(int resId, Object... args) {
        toast(getString(resId), args);
    }
}
