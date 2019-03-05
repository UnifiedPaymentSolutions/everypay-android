package com.everypay.everypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.everypay.everypay.fragment.MessageDialogFragment;
import com.everypay.everypay.fragment.SingleChoiceDialogFragment;
import com.everypay.everypay.util.DialogUtil;
import com.everypay.sdk.EveryPay;
import com.everypay.sdk.EveryPayListener;
import com.everypay.sdk.activity.CardFormActivity;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.StepType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.everypay.everypay.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SingleChoiceDialogFragment.SingleChoiceDialogFragmentListener {


    private static final String ACCOUNT_ID_3DS = "EUR3D1";
    private static final String ACCOUNT_ID_NON_3DS = "EUR1";
    private static final String API_VERSION = "2";
    private static final String EXTRA_CARD = "com.everypay.everypay.EXTRA_CARD";
    private static final String TAG_ACCOUNT_CHOICE_DIALOG = "com.everypay.everypay.TAG_ACCOUNT_CHOICE_DIALOG";
    private static final String TAG_ENVIRONMENT_CHOICE_DIALOG = "com.everypay.everypay.TAG_ENVIRONMENT_CHOICE_DIALOG";
    private static final String TAG_START_FULL_PAYMENT_FLOW = "com.everypay.everypay.TAG_START_FULL_PAYMENT_FLOW";
    private static final String EXTRA_DEVICE_INFO = "com.everypay.everypay.EXTRA_DEVICE_INFO";
    private static final String STATE_ACCOUNT_ID_CHOICES = "com.everypay.everypay.STATE_ACCOUNT_ID_CHOICES";
    private static final String KEY_STAGING_ENVIRONMENT = "Staging Environment";
    private static final String KEY_DEMO_ENVIRONMENT = "Demo Environment";
    private static final String STATE_BASE_URL_CHOICES = "com.everypay.everypay.STATE_BASE_URL_CHOICES";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG_MESSAGE_DIALOG = "com.everypay.everypay.TAG_MESSAGE_DIALOG";
    private static com.everypay.sdk.util.Log log = com.everypay.sdk.util.Log.getInstance(MainActivity.class);

    private ArrayList<String> accountIdChoices;
    private ArrayList<String> environments;
    private HashMap<String, ArrayList<String>> baseUrlMap;
    private StepStatusViews[] statuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPlayServices()) {
            attachUiEvents();
        } else {
            finish();
        }
        if (savedInstanceState != null) {
            accountIdChoices = savedInstanceState.getStringArrayList(STATE_ACCOUNT_ID_CHOICES);
        }
    }

    private void initializeChoices() {
        // Account Id choices
        if(accountIdChoices == null) {
            accountIdChoices = new ArrayList<>();
        }
        accountIdChoices.clear();
        accountIdChoices.add(ACCOUNT_ID_3DS);
        accountIdChoices.add(ACCOUNT_ID_NON_3DS);
        // base URL choices
        if(baseUrlMap == null) {
            baseUrlMap = new HashMap<>();
        }
        baseUrlMap.clear();
        // array of URLs in the order: merchantApiBaseUrl, EveryPayApiBaseUrl, EveryPayHost
        ArrayList<String> stagingURLs = new ArrayList<>();
        stagingURLs.add(EveryPay.MERCHANT_API_URL_STAGING);
        stagingURLs.add(EveryPay.EVERYPAY_API_URL_STAGING);
        stagingURLs.add(EveryPay.EVERYPAY_API_STAGING_HOST);
        ArrayList<String> demoURLs = new ArrayList<>();
        demoURLs.add(EveryPay.MERCHANT_API_URL_DEMO);
        demoURLs.add(EveryPay.EVERYPAY_API_URL_DEMO);
        demoURLs.add(EveryPay.EVERYPAY_API_DEMO_HOST);

        baseUrlMap.put(KEY_STAGING_ENVIRONMENT, stagingURLs);
        baseUrlMap.put(KEY_DEMO_ENVIRONMENT, demoURLs);

        environments = new ArrayList<>();
        environments.add(KEY_STAGING_ENVIRONMENT);
        environments.add(KEY_DEMO_ENVIRONMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CardFormActivity.REQUEST_CODE) {
            hideStatusViews(StepType.CARD_INPUT);
            Card result = CardFormActivity.getCardFromResult(resultCode, data);
            if (result != null) {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_CARD, result);
                SingleChoiceDialogFragment fragment = SingleChoiceDialogFragment.newInstance(getString(R.string.title_choose_environment), getString(R.string.text_choose_environment), environments, extras);
                DialogUtil.showDialogFragment(MainActivity.this, fragment, TAG_ENVIRONMENT_CHOICE_DIALOG, null);
                statuses[0].good.setVisibility(View.VISIBLE);

            } else {
                statuses[0].bad.setVisibility(View.VISIBLE);
                displayMessageDialog(getString(R.string.ep_title_no_valid_card), getString(R.string.ep_err_no_valid_card));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void attachUiEvents() {
        // we need to count out web auth step and confirm3Ds step
        statuses = new StepStatusViews[StepType.values().length];

        statuses[0] = new StepStatusViews(R.id.card_good, R.id.card_bad, R.id.card_progress);
        statuses[1] = new StepStatusViews(R.id.credentials_good, R.id.credentials_bad, R.id.credentials_progress);
        statuses[2] = new StepStatusViews(R.id.token_good, R.id.token_bad, R.id.token_progress);
        statuses[3] = new StepStatusViews(R.id.payment_good, R.id.payment_bad, R.id.payment_progress);

        for (int i = 0; i < statuses.length - 2; i++) {
            StepStatusViews view = statuses[i];
            view.good.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.tint_good));
            view.bad.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.tint_bad));
        }
        hideAllStatusViews();

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) findViewById(R.id.start)).setText(MainActivity.this.getResources().getString(R.string.ep_btn_restart));
                hideAllStatusViews();
                statuses[0].progress.setVisibility(View.VISIBLE);

                // Initial card data is entirely optional.
                // Use null to let the user fill out the card form.
                Card initial = new Card();
                initial.setName(MainActivity.this.getResources().getString(R.string.ep_initial_card_name));
                initial.setNumber(MainActivity.this.getResources().getString(R.string.ep_initial_card_number));
                initial.setExpYear(MainActivity.this.getResources().getString(R.string.ep_initial_card_exp_year));
                initial.setExpMonth(MainActivity.this.getResources().getString(R.string.ep_initial_card_exp_month));
                initial.setCVC(MainActivity.this.getResources().getString(R.string.ep_initial_card_cvc));

                initializeChoices();

                CardFormActivity.startForResult(MainActivity.this, initial);
            }
        });
    }

    private void hideAllStatusViews() {
        for (int i = 0; i < statuses.length - 2; i++) {
            StepStatusViews view = statuses[i];
            view.good.setVisibility(View.GONE);
            view.bad.setVisibility(View.GONE);
            view.progress.setVisibility(View.GONE);
        }
    }

    private void hideStatusViews(StepType step) {
        if(!step.equals(StepType.WEB_AUTH_STEP)) {
            StepStatusViews views = statuses[step.ordinal()];
            views.good.setVisibility(View.GONE);
            views.bad.setVisibility(View.GONE);
            views.progress.setVisibility(View.GONE);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EveryPay.getDefault() != null) {
            EveryPay.getDefault().removeListener(TAG_START_FULL_PAYMENT_FLOW);
        }

    }

    @Override
    public void onSingleChoicePicked(String tag, int position, Bundle extras) {
        if (TextUtils.equals(TAG_ENVIRONMENT_CHOICE_DIALOG, tag)) {
            String baseURLKey = environments.size() > position ? environments.get(position) : null;
            if (!TextUtils.isEmpty(baseURLKey)) {
                ArrayList<String> baseURLs = baseUrlMap.get(baseURLKey);
                if (baseURLs != null && baseURLs.size() != 0) {
                    EveryPay.with(this).setEverypayApiBaseUrl(baseURLs.get(1)).setMerchantApiBaseUrl(baseURLs.get(0)).setEveryPayHost(baseURLs.get(2)).build(API_VERSION).setDefault();
                    SingleChoiceDialogFragment dialogFragment = SingleChoiceDialogFragment.newInstance(getString(R.string.title_choose_account), getString(R.string.text_choose_account_id), accountIdChoices, extras);
                    DialogUtil.showDialogFragment(MainActivity.this, dialogFragment, TAG_ACCOUNT_CHOICE_DIALOG, null);
                }
            }
        } else if (TextUtils.equals(TAG_ACCOUNT_CHOICE_DIALOG, tag)) {
            Card card = extras.getParcelable(EXTRA_CARD);
            String accountId = accountIdChoices.size() > position ? accountIdChoices.get(position) : null;
            if (card != null && accountId != null) {
                EveryPay ep = EveryPay.getDefault();
                if(ep != null) {
                    ep.startFullPaymentFlow(TAG_START_FULL_PAYMENT_FLOW, card, new EveryPayListener() {

                        @Override
                        public void stepStarted(StepType step) {
                            log.d("Started step " + step);
                            hideStatusViews(step);
                            statuses[step.ordinal()].progress.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void stepSuccess(StepType step) {
                            log.d("Completed step " + step);
                            hideStatusViews(step);
                            statuses[step.ordinal()].good.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void fullSuccess(MerchantPaymentResponseData responseData) {
                            displayMessageDialog(getString(R.string.ep_title_payment_successful), getString(R.string.ep_text_payment_successful, responseData));
                        }

                        @Override
                        public void stepFailure(StepType step, EveryPayError error) {
                            hideStatusViews(step);
                            if (!step.equals(StepType.WEB_AUTH_STEP)) {
                                statuses[step.ordinal()].bad.setVisibility(View.VISIBLE);
                            }
                            displayMessageDialog(getString(R.string.ep_title_step_failed), getString(R.string.ep_text_step_failed, step, error.getMessage()));
                        }
                    }, accountId);
                }
            }
        }
    }

    private void displayMessageDialog(String title, String message) {
        MessageDialogFragment fragment = MessageDialogFragment.newInstance(title, message, getString(R.string.ep_btn_ok));
        DialogUtil.showDialogFragment(MainActivity.this, fragment, TAG_MESSAGE_DIALOG, null);
    }

    @Override
    public void onSingleChoiceCanceled(String tag, Bundle extras) {
        log.d("onSingleChoiceCanceled - tag " + tag);
    }

    private class StepStatusViews {
        public ImageView good;
        public ImageView bad;
        public ProgressBar progress;

        public StepStatusViews(int goodId, int badId, int progressId) {
            good = (ImageView) findViewById(goodId);
            bad = (ImageView) findViewById(badId);
            progress = (ProgressBar) findViewById(progressId);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                log.e("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STATE_ACCOUNT_ID_CHOICES, accountIdChoices);
        outState.putSerializable(STATE_BASE_URL_CHOICES, baseUrlMap);
    }
}
