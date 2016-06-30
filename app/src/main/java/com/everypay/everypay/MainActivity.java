package com.everypay.everypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.everypay.everypay.fragment.SingleChoiceDialogFragment;
import com.everypay.everypay.util.DialogUtil;
import com.everypay.sdk.EveryPay;
import com.everypay.sdk.EveryPayListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.StepType;
import com.everypay.sdk.views.CardFormActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SingleChoiceDialogFragment.SingleChoiceDialogFragmentListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_ACCOUNT_ID_CHOICE = 100;
    private static final String ACCOUNT_ID_3DS = "EUR3D1";
    private static final String ACCOUNT_ID_NON_3DS = "EUR1";
    private static final String EXTRA_CARD = "com.everypay.everypay.EXTRA_CARD";
    private static final String TAG_ACCOUNT_CHOICE_DIALOG = "com.everypay.everypay.TAG_ACCOUNT_CHOICE_DIALOG";
    private static final String EXTRA_DEVICE_INFO = "com.everypay.everypay.EXTRA_DEVICE_INFO";
    private static final String STATE_ACCOUNT_ID_CHOICES = "com.everypay.everypay.STATE_ACCOUNT_ID_CHOICES";
    private static com.everypay.sdk.util.Log log = com.everypay.sdk.util.Log.getInstance(MainActivity.class);
    EveryPay ep;

    private ArrayList<String> accountIdChoices;
    StepStatusViews[] statuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPlayServices()) {
            ep = EveryPay.getDefault();

            attachUiEvents();
        } else {
            finish();
        }
        if(savedInstanceState != null) {
            accountIdChoices = savedInstanceState.getStringArrayList(STATE_ACCOUNT_ID_CHOICES);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CardFormActivity.REQUEST_CODE) {
            hideStatusViews(StepType.CARD_INPUT);
            Pair<Card, String> result = CardFormActivity.getCardAndDeviceInfoFromResult(resultCode, data);
            if (result != null) {
                accountIdChoices = new ArrayList<>();
                accountIdChoices.add(ACCOUNT_ID_3DS);
                accountIdChoices.add(ACCOUNT_ID_NON_3DS);
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_CARD, result.first);
                extras.putString(EXTRA_DEVICE_INFO, result.second);
                SingleChoiceDialogFragment fragment = SingleChoiceDialogFragment.newInstance(getString(R.string.title_choose_account), getString(R.string.text_choose_account_id), accountIdChoices, extras);
                DialogUtil.showDialogFragment(MainActivity.this, fragment, TAG_ACCOUNT_CHOICE_DIALOG, null, REQUEST_CODE_ACCOUNT_ID_CHOICE);
                statuses[0].good.setVisibility(View.VISIBLE);

            } else {
                statuses[0].bad.setVisibility(View.VISIBLE);
                toast(MainActivity.this.getResources().getString(R.string.ep_err_no_valid_card));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void attachUiEvents() {
        statuses = new StepStatusViews[StepType.values().length - 1];
        statuses[0] = new StepStatusViews(R.id.card_good, R.id.card_bad, R.id.card_progress);
        statuses[1] = new StepStatusViews(R.id.credentials_good, R.id.credentials_bad, R.id.credentials_progress);
        statuses[2] = new StepStatusViews(R.id.token_good, R.id.token_bad, R.id.token_progress);
        statuses[3] = new StepStatusViews(R.id.payment_good, R.id.payment_bad, R.id.payment_progress);

        for (StepStatusViews views : statuses) {
            views.good.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.tint_good));
            views.bad.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.tint_bad));
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

                CardFormActivity.startForResult(MainActivity.this, initial);
            }
        });
    }

    private void hideAllStatusViews() {
        for (StepStatusViews views : statuses) {
            views.good.setVisibility(View.GONE);
            views.bad.setVisibility(View.GONE);
            views.progress.setVisibility(View.GONE);
        }
    }

    private void hideStatusViews(StepType step) {
        StepStatusViews views = statuses[step.ordinal()];
        views.good.setVisibility(View.GONE);
        views.bad.setVisibility(View.GONE);
        views.progress.setVisibility(View.GONE);
    }

    private void toast(String fmt, Object... args) {
        String msg = String.format(fmt, args);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSingleChoicePicked(int requestCode, int position, Bundle extras) {
        Card card = extras.getParcelable(EXTRA_CARD);
        String deviceInfo = extras.getString(EXTRA_DEVICE_INFO);
        String accountId  = accountIdChoices.size() > position ? accountIdChoices.get(position) : null;
        if (card != null && !TextUtils.isEmpty(deviceInfo) && accountId != null) {
            EveryPay.getDefault().startFullPaymentFlow(card, deviceInfo, new EveryPayListener() {

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
                public void fullSuccess() {
                    toast(MainActivity.this.getResources().getString(R.string.ep_toast_payment_successful));
                }

                @Override
                public void stepFailure(StepType step, Exception e) {
                    log.e("Error in step " + step, e);
                    hideStatusViews(step);
                    statuses[step.ordinal()].bad.setVisibility(View.VISIBLE);
                    toast(MainActivity.this.getResources().getString(R.string.ep_toast_step_failed), step, e);
                }
            }, accountId);
        }
    }

    @Override
    public void onSingleChoiceCanceled(int requestCode, Bundle extras) {

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
    }
}
