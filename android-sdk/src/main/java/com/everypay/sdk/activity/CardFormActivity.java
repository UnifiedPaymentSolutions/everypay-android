package com.everypay.sdk.activity;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.everypay.sdk.R;
import com.everypay.sdk.fragment.CardFormFragment;
import com.everypay.sdk.model.Card;


public class CardFormActivity extends BaseActivity {

    public static final String EXTRA_INITIAL_DATA = "com.everypay.sdk.EXTRA_INITIAL_DATA";
    public static final String EXTRA_CARD = "com.everypay.sdk.EXTRA_CARD";

    private static final String METHOD_SET = "set";
    public static final int REQUEST_CODE = 13423;
    private static final String STATE_PARTIAL_CARD = "com.everypay.sdk.STATE_PARTIAL_CARD";
    private static final String TAG_CARD_FORM_FRAGMENT = "com.everypay.sdk.TAG_CARD_FORM_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ep_activity_cardform);
        setUpCardFormFragment();
    }

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

    public static Card getCardFromResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return data.getParcelableExtra(EXTRA_CARD);
        }
        return null;
    }


    private void setUpCardFormFragment() {
        CardFormFragment fragment = (CardFormFragment) getSupportFragmentManager().findFragmentByTag(TAG_CARD_FORM_FRAGMENT);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment == null) {
            if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable(EXTRA_INITIAL_DATA) != null) {
                fragment = CardFormFragment.newInstance(getIntent().getExtras().getParcelable(EXTRA_INITIAL_DATA));
            } else {
                fragment = CardFormFragment.newInstance();
            }


        }
        transaction.add(R.id.cardform_fragment_container, fragment, TAG_CARD_FORM_FRAGMENT).commit();
    }


}
