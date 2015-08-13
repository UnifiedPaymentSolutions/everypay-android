package com.everypay.sdk.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.everypay.sdk.Everypay;
import com.everypay.sdk.R;
import com.everypay.sdk.collector.DeviceCollector;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.model.CardError;
import com.everypay.sdk.model.CardType;
import com.everypay.sdk.util.CustomGson;
import com.everypay.sdk.util.Reflect;

import java.util.HashMap;
import java.util.Map;


public class CardFormActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 13423;

    public static void startForResult(Activity activity, String title) {
        Intent intent = new Intent(activity, CardFormActivity.class);
        intent.putExtra("title", title);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static Pair<Card, Map<String, Object>> getCardAndDeviceInfoFromResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return new Pair<>((Card)data.getParcelableExtra("card"), (Map<String, Object>)CustomGson.getInstance().fromJson(data.getStringExtra("deviceInfo"), new HashMap<String, Object>().getClass()));
        }
        return null;
    }

    EditText name;
    EditText number;
    EditText cvc;
    EditText month;
    EditText year;
    ImageView typeIcon;
    Button done;

    int colorNormal;
    int colorInvalid;

    Card partialCard;
    DeviceCollector collector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardform);
        setTitle(getIntent().getStringExtra("title"));

        name = (EditText)findViewById(R.id.cc_holder_name);
        number = (EditText)findViewById(R.id.cc_number);
        cvc = (EditText)findViewById(R.id.cc_cvc);
        month = (EditText)findViewById(R.id.cc_month);
        year = (EditText)findViewById(R.id.cc_year);
        typeIcon = (ImageView)findViewById(R.id.cc_type_icon);
        done = (Button)findViewById(R.id.btn_done);

        colorNormal = getResources().getColor(R.color.ep_card_field_normal);
        colorInvalid = getResources().getColor(R.color.ep_card_field_invalid);

        partialCard = new Card();
        collector = new DeviceCollector(this);
        collector.start();

        attachUiEvents();
        setResult(RESULT_CANCELED, null);
    }

    private void attachUiEvents() {
        name.addTextChangedListener(new CardFormTextWatcher(name, partialCard, "Name"));
        number.addTextChangedListener(new CardFormTextWatcher(number, partialCard, "Number"));
        cvc.addTextChangedListener(new CardFormTextWatcher(cvc, partialCard, "CVC"));

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int iconResId = partialCard.getType().getIconId();
                if (iconResId == CardType.INVALID_ICON) {
                    typeIcon.setVisibility(View.GONE);
                } else {
                    typeIcon.setImageResource(0);
                    typeIcon.setImageResource(iconResId);
                    typeIcon.setVisibility(View.VISIBLE);
                }
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(month, "ExpMonth", R.string.cc_month, R.array.cc_month_values, R.array.cc_month_names);
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(year, "ExpYear", R.string.cc_year, R.array.cc_year_values, R.array.cc_year_values);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Everypay.TAG, "Month now " + partialCard.getExpMonth());
                Log.e(Everypay.TAG, "Year now " + partialCard.getExpYear());
                if (validateWithToast()) {
                    Intent result = new Intent();
                    result.putExtra("card", partialCard);
                    result.putExtra("deviceInfo", CustomGson.getInstance().toJson(collector.collectWithTimeout()));
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    setResult(RESULT_CANCELED, null);
                }
            }
        });
    }

    private boolean validateWithToast() {
        try {
            partialCard.validateCard(this);
            return true;
        } catch (CardError cardError) {
            toast(cardError.getMessage());
            return false;
        }
    }

    private void showSelectDialog(final EditText input, final String fieldName, int titleId, final int valuesId, final int displayId) {
        new AlertDialog.Builder(this)
                .setItems(displayId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] displays = getResources().getStringArray(displayId);
                        String[] values = getResources().getStringArray(valuesId);
                        if (values != null && which < values.length && which < displays.length) {
                            input.setText(displays[which]);
                            Reflect.setString(partialCard, "set" + fieldName, values[which]);
                        }
                    }
                })
                .show();
    }

    private void toast(String fmt, Object... args) {
        String msg = String.format(fmt, args);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toast(int resId, Object... args) {
        toast(getString(resId), args);
    }
}
