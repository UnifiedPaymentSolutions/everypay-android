package com.everypay.everypay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.EveryPayListener;
import com.everypay.sdk.steps.StepType;

public class MainActivity extends AppCompatActivity {

    EveryPay ep;

    StepStatusViews[] statuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ep = EveryPay.getInstance();

        attachUiEvents();
    }

    private void attachUiEvents() {
        statuses = new StepStatusViews[StepType.values().length];
        statuses[0] = new StepStatusViews(R.id.credentials_good, R.id.credentials_bad, R.id.credentials_progress);
        statuses[1] = new StepStatusViews(R.id.card_good, R.id.card_bad, R.id.card_progress);
        statuses[2] = new StepStatusViews(R.id.token_good, R.id.token_bad, R.id.token_progress);
        statuses[3] = new StepStatusViews(R.id.payment_good, R.id.payment_bad, R.id.payment_progress);

        for (StepStatusViews views : statuses) {
            views.good.setColorFilter(getResources().getColor(R.color.tint_good));
            views.bad.setColorFilter(getResources().getColor(R.color.tint_bad));
        }
        hideAllStatusViews();

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllStatusViews();

                ep.startFullPaymentFlow(new EveryPayListener() {
                    @Override
                    public void stepStarted(StepType step) {
                        hideStatusViews(step);
                        statuses[step.ordinal()].progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void stepSuccess(StepType step) {
                        hideStatusViews(step);
                        statuses[step.ordinal()].good.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void fullSuccess() {
                        Toast.makeText(MainActivity.this, "Payment completed!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void stepFailure(StepType step, Exception e) {
                        Log.e(EveryPay.TAG, "Error", e);
                        hideStatusViews(step);
                        statuses[step.ordinal()].bad.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Step " + step + " failed: " + e, Toast.LENGTH_LONG).show();
                        ((Button)findViewById(R.id.start)).setText("Restart");
                    }
                });
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
