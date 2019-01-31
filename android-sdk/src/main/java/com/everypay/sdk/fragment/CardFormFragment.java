package com.everypay.sdk.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.everypay.sdk.deviceinfo.DeviceCollector;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.model.CardError;
import com.everypay.sdk.model.CardType;
import com.everypay.sdk.util.CardFormTextWatcher;
import com.everypay.sdk.util.DialogUtil;
import com.everypay.sdk.util.Reflect;
import com.everypay.sdk.R;

public class CardFormFragment extends BaseFragment implements DeviceCollector.DeviceInfoListener, ProgressDialogFragment.ProgressDialogFragmentListener {

    private static final String EXTRA_CARD = "com.everypay.sdk.EXTRA_CARD";
    private static final String EXTRA_DEVICE_INFO = "com.everypay.sdk.EXTRA_DEVICE_INFO";

    private static final String METHOD_SET = "set";
    private static final String STATE_PARTIAL_CARD = "com.everypay.STATE_PARTIAL_CARD";
    private static final String ARG_INITIAL_CARD_DATA = "com.everypay.sdk.ARG_INITIAL_CARD_DATA";
    private static final String TAG_DEVICEINFO_COLLECTOR_PROGRESS = "com.everypay.sdk.TAG_DEVICEINFO_COLLECTOR_PROGRESS";

    private EditText name;
    private EditText number;
    private EditText cvc;
    private EditText month;
    private EditText year;
    private ImageView typeIcon;
    private Button done;

    private int colorNormal;
    private int colorInvalid;

    private Card partialCard;
    private DeviceCollector collector;

    public static CardFormFragment newInstance(Card initialData) {
        CardFormFragment fragment = new CardFormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_INITIAL_CARD_DATA, initialData);
        fragment.setArguments(args);
        return fragment;
    }

    public static CardFormFragment newInstance() {
        return new CardFormFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ep_fragment_cardform, container, false);
        name = (EditText) rootView.findViewById(R.id.cc_holder_name);
        number = (EditText) rootView.findViewById(R.id.cc_number);
        cvc = (EditText) rootView.findViewById(R.id.cc_cvc);
        month = (EditText) rootView.findViewById(R.id.cc_month);
        year = (EditText) rootView.findViewById(R.id.cc_year);
        typeIcon = (ImageView) rootView.findViewById(R.id.cc_type_icon);
        done = (Button) rootView.findViewById(R.id.btn_done);

        colorNormal = ContextCompat.getColor(getActivity(), R.color.ep_card_field_normal);
        colorInvalid = ContextCompat.getColor(getActivity(), R.color.ep_card_field_invalid);

        partialCard = savedInstanceState != null ? (Card) savedInstanceState.getParcelable(STATE_PARTIAL_CARD) : new Card();

        if(getRetainObject() != null) {
            if(getRetainObject() instanceof DeviceCollector) {
                collector = (DeviceCollector) getRetainObject();
                collector.setListener(this);
            }
        }else {
            collector = new DeviceCollector(getActivity());
            collector.setListener(CardFormFragment.this);
        }
        collector.start();
        attachUiEvents();
        loadInitialData();

        return rootView;
    }


    private void attachUiEvents() {
        name.addTextChangedListener(new CardFormTextWatcher(name, partialCard, getActivity().getResources().getString(R.string.ep_field_name)));
        number.addTextChangedListener(new CardFormTextWatcher(number, partialCard, getActivity().getResources().getString(R.string.ep_field_number)));
        cvc.addTextChangedListener(new CardFormTextWatcher(cvc, partialCard, getActivity().getResources().getString(R.string.ep_field_cvc)));

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
                showSelectDialog(month, getActivity().getResources().getString(R.string.ep_field_exp_month), R.string.ep_cc_month, R.array.ep_cc_month_values, R.array.ep_cc_month_names);
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(year, getActivity().getResources().getString(R.string.ep_field_exp_year), R.string.ep_cc_year, R.array.ep_cc_year_values, R.array.ep_cc_year_values);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateWithToast()) {
                    if(collector != null && collector.getListener() == null) {
                        collector.setListener(CardFormFragment.this);
                    }
                    DialogUtil.showDialogFragment(ProgressDialogFragment.newInstance(), TAG_DEVICEINFO_COLLECTOR_PROGRESS, CardFormFragment.this);
                    collector.collectWithDefaultTimeout();
                }
            }
        });
    }

    private void loadInitialData() {
        if (!TextUtils.isEmpty(partialCard.getName()) && !TextUtils.isEmpty(partialCard.getCVC()) && !TextUtils.isEmpty(partialCard.getNumber()) && !TextUtils.isEmpty(partialCard.getExpMonth()) && !TextUtils.isEmpty(partialCard.getExpYear())) {
            name.setText(partialCard.getName());
            number.setText(partialCard.getNumber());
            cvc.setText(partialCard.getCVC());
            setValueFromArray(month, getActivity().getResources().getString(R.string.ep_field_exp_month), R.array.ep_cc_month_values, R.array.ep_cc_month_names, partialCard.getExpMonth());
            setValueFromArray(year, getActivity().getResources().getString(R.string.ep_field_exp_year), R.array.ep_cc_year_values, R.array.ep_cc_year_values, partialCard.getExpYear());
        } else {
            Card initialData = getArguments().getParcelable(ARG_INITIAL_CARD_DATA);
            if (initialData != null) {
                name.setText(initialData.getName());
                number.setText(initialData.getNumber());
                cvc.setText(initialData.getCVC());
                setValueFromArray(month, getActivity().getResources().getString(R.string.ep_field_exp_month), R.array.ep_cc_month_values, R.array.ep_cc_month_names, initialData.getExpMonth());
                setValueFromArray(year, getActivity().getResources().getString(R.string.ep_field_exp_year), R.array.ep_cc_year_values, R.array.ep_cc_year_values, initialData.getExpYear());
            }
        }
    }

    private boolean validateWithToast() {
        try {
            partialCard.validateCard(getActivity());
            return true;
        } catch (CardError cardError) {
            Toast.makeText(getActivity(),cardError.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void showSelectDialog(final EditText input, final String fieldName, int titleId, final int valuesId, final int displayId) {
        new AlertDialog.Builder(getActivity())
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
       outState.putParcelable(STATE_PARTIAL_CARD, partialCard);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(collector != null) {
            collector.setListener(null);
            setRetainObject(collector);
        }
    }

    public void retrieveCardData(String deviceInfo, Card card) {
        Intent result = new Intent();
        result.putExtra(EXTRA_CARD, card);
        result.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        getActivity().setResult(Activity.RESULT_OK, result);
        getActivity().finish();
    }

    @Override
    public void deviceInfoCollected(String deviceInfo) {
        if(getActivity() == null) {
            return;
        }
        DialogUtil.dismissDialogFragment(CardFormFragment.this, TAG_DEVICEINFO_COLLECTOR_PROGRESS);
        partialCard.setName(partialCard.getName().trim());
        retrieveCardData(deviceInfo, partialCard);
    }

    @Override
    public void onProgressDialogFragmentCanceled(String tag) {
        if (TextUtils.equals(tag, TAG_DEVICEINFO_COLLECTOR_PROGRESS)) {
            collector.setListener(null);
            collector.cancel();
        }
    }
}

