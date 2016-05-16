package com.everypay.everypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.everypay.sdk.EveryPay1;
import com.everypay.sdk.EveryPayListener1;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.StepType;
import com.everypay.sdk.views.CardFormActivity;

public class MainActivity extends AppCompatActivity {

    EveryPay1 ep;

    StepStatusViews[] statuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ep = EveryPay1.getDefault();

        attachUiEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CardFormActivity.REQUEST_CODE) {
            hideStatusViews(StepType.CARD_INPUT);

            Pair<Card, String> result = CardFormActivity.getCardAndDeviceInfoFromResult(resultCode, data);
            if (result != null) {
                statuses[0].good.setVisibility(View.VISIBLE);
                EveryPay1.getDefault().startFullPaymentFlow(result.first, result.second, new EveryPayListener1() {

                    @Override
                    public void stepStarted(StepType step) {
                        Log.d(EveryPay1.TAG, "Started step " + step);
                        hideStatusViews(step);
                        statuses[step.ordinal()].progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void stepSuccess(StepType step) {
                        Log.d(EveryPay1.TAG, "Completed step " + step);
                        hideStatusViews(step);
                        statuses[step.ordinal()].good.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void fullSuccess() {
                        toast(MainActivity.this.getResources().getString(R.string.ep_toast_payment_successful));
                    }

                    @Override
                    public void stepFailure(StepType step, Exception e) {
                        Log.e(EveryPay1.TAG, "Error in step " + step, e);
                        hideStatusViews(step);
                        statuses[step.ordinal()].bad.setVisibility(View.VISIBLE);
                        toast(MainActivity.this.getResources().getString(R.string.ep_toast_step_failed), step, e);
                    }
                });
            } else {
                statuses[0].bad.setVisibility(View.VISIBLE);
                toast(MainActivity.this.getResources().getString(R.string.ep_err_no_valid_card));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void attachUiEvents() {
        statuses = new StepStatusViews[StepType.values().length];
        statuses[0] = new StepStatusViews(R.id.card_good, R.id.card_bad, R.id.card_progress);
        statuses[1] = new StepStatusViews(R.id.credentials_good, R.id.credentials_bad, R.id.credentials_progress);
        statuses[2] = new StepStatusViews(R.id.token_good, R.id.token_bad, R.id.token_progress);
        statuses[3] = new StepStatusViews(R.id.payment_good, R.id.payment_bad, R.id.payment_progress);

        for (StepStatusViews views : statuses) {
            views.good.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.tint_good));
            views.bad.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.tint_bad));
        }
        hideAllStatusViews();

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button)findViewById(R.id.start)).setText(MainActivity.this.getResources().getString(R.string.ep_btn_restart));
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

    private class StepStatusViews {
        public ImageView good;
        public ImageView bad;
        public ProgressBar progress;

        public StepStatusViews(int goodId, int badId, int progressId) {
            good = (ImageView)findViewById(goodId);
            bad = (ImageView)findViewById(badId);
            progress = (ProgressBar)findViewById(progressId);
        }
    }

}
