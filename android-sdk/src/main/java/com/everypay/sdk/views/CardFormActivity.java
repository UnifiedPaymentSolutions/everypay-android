package com.everypay.sdk.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.everypay.sdk.R;
import com.everypay.sdk.deviceinfo.DeviceCollector;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.model.CardError;
import com.everypay.sdk.model.CardType;
import com.everypay.sdk.util.Reflect;

import java.io.Serializable;


public class CardFormActivity extends AppCompatActivity {

    private static final String EXTRA_INITIAL_DATA = "com.everypay.sdk.EXTRA_INITIAL_DATA";
    private static final String EXTRA_CARD = "com.everypay.sdk.EXTRA_CARD";
    private static final String EXTRA_DEVICE_INFO = "com.everypay.sdk.EXTRA_DEVICE_INFO";

    private static final String METHOD_SET = "set";
    public static final int REQUEST_CODE = 13423;
    private static final String STATE_PARTIAL_CARD = "com.everypay.STATE_PARTIAL_CARD";

    public static void startForResult(Activity fromActivity) {
        startForResult(fromActivity, null);
    }

    /**
     * Card initialData is optional.
     */
    public static void startForResult(Activity fromActivity, Card initialData) {
        Intent intent = new Intent(fromActivity, CardFormActivity.class);
        if (initialData != null) {
            intent.putExtra(EXTRA_INITIAL_DATA, initialData);
        }
        fromActivity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static Pair<Card, String> getCardAndDeviceInfoFromResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return new Pair<>((Card) data.getParcelableExtra(EXTRA_CARD), data.getStringExtra(EXTRA_DEVICE_INFO));
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

    private FloatingProgress progress;

    int colorNormal;
    int colorInvalid;

    Card partialCard;
    DeviceCollector collector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardform);

        name = (EditText) findViewById(R.id.cc_holder_name);
        number = (EditText) findViewById(R.id.cc_number);
        cvc = (EditText) findViewById(R.id.cc_cvc);
        month = (EditText) findViewById(R.id.cc_month);
        year = (EditText) findViewById(R.id.cc_year);
        typeIcon = (ImageView) findViewById(R.id.cc_type_icon);
        done = (Button) findViewById(R.id.btn_done);

        colorNormal = ContextCompat.getColor(CardFormActivity.this,R.color.ep_card_field_normal);
        colorInvalid = ContextCompat.getColor(CardFormActivity.this,R.color.ep_card_field_invalid);

        partialCard = savedInstanceState != null ? (Card) savedInstanceState.getParcelable(STATE_PARTIAL_CARD) : new Card();

        collector = new DeviceCollector(this);
        collector.start();

        attachUiEvents();
        loadInitialData();
        setResult(RESULT_CANCELED, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
        collector.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void attachUiEvents() {
        name.addTextChangedListener(new CardFormTextWatcher(name, partialCard, CardFormActivity.this.getResources().getString(R.string.ep_field_name)));
        number.addTextChangedListener(new CardFormTextWatcher(number, partialCard, CardFormActivity.this.getResources().getString(R.string.ep_field_number)));
        cvc.addTextChangedListener(new CardFormTextWatcher(cvc, partialCard, CardFormActivity.this.getResources().getString(R.string.ep_field_cvc)));

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
                showSelectDialog(month, CardFormActivity.this.getResources().getString(R.string.ep_field_exp_month), R.string.ep_cc_month, R.array.ep_cc_month_values, R.array.ep_cc_month_names);
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(year, CardFormActivity.this.getResources().getString(R.string.ep_field_exp_year), R.string.ep_cc_year, R.array.ep_cc_year_values, R.array.ep_cc_year_values);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                if (validateWithToast()) {
                    showProgress();
                    collector.collectWithDefaultTimeout(new DeviceCollector.DeviceInfoListener() {
                        @Override
                        public void deviceInfoCollected(String deviceInfo) {
                            hideProgress();
                            Intent result = new Intent();
                            partialCard.setName(partialCard.getName().trim());
                            result.putExtra(EXTRA_CARD, partialCard);
                            result.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
                            setResult(RESULT_OK, result);
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void loadInitialData() {
        if (!TextUtils.isEmpty(partialCard.getName()) && !TextUtils.isEmpty(partialCard.getCVC()) && !TextUtils.isEmpty(partialCard.getNumber()) && !TextUtils.isEmpty(partialCard.getExpMonth()) && !TextUtils.isEmpty(partialCard.getExpYear())) {
            name.setText(partialCard.getName());
            number.setText(partialCard.getNumber());
            cvc.setText(partialCard.getCVC());
            setValueFromArray(month, CardFormActivity.this.getResources().getString(R.string.ep_field_exp_month), R.array.ep_cc_month_values, R.array.ep_cc_month_names, partialCard.getExpMonth());
            setValueFromArray(year, CardFormActivity.this.getResources().getString(R.string.ep_field_exp_year), R.array.ep_cc_year_values, R.array.ep_cc_year_values, partialCard.getExpYear());
        }  else {
            Card initialData = getIntent().getParcelableExtra(EXTRA_INITIAL_DATA);
            if (initialData != null) {
                name.setText(initialData.getName());
                number.setText(initialData.getNumber());
                cvc.setText(initialData.getCVC());
                setValueFromArray(month, CardFormActivity.this.getResources().getString(R.string.ep_field_exp_month), R.array.ep_cc_month_values, R.array.ep_cc_month_names, initialData.getExpMonth());
                setValueFromArray(year, CardFormActivity.this.getResources().getString(R.string.ep_field_exp_year), R.array.ep_cc_year_values, R.array.ep_cc_year_values, initialData.getExpYear());
            }
        }
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
                            Reflect.setString(partialCard, METHOD_SET + fieldName, values[which]);
                        }
                    }
                })
                .show();
    }

    private void setValueFromArray(EditText input, String fieldName, int valuesId, int displayId, String desiredValue) {
        String[] displays = getResources().getStringArray(displayId);
        String[] values = getResources().getStringArray(valuesId);
        for (int i = 0; i < displays.length && i < values.length; ++i) {
            if (values[i].equals(desiredValue)) {
                input.setText(displays[i]);
                Reflect.setString(partialCard, METHOD_SET + fieldName, desiredValue);
                return;
            }
        }
    }

    private void toast(String fmt, Object... args) {
        String msg = String.format(fmt, args);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toast(int resId, Object... args) {
        toast(getString(resId), args);
    }

    private void showProgress() {
        if (progress != null) {
            progress.dismiss();
        }
        progress = FloatingProgress.show(this);
    }

    private void hideProgress() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_PARTIAL_CARD, partialCard);
    }
}
