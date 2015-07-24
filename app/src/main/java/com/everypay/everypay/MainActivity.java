package com.everypay.everypay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.EveryPayListener;
import com.everypay.sdk.steps.StepType;

public class MainActivity extends AppCompatActivity {

    EveryPay ep;


    ImageView credentialsGood;
    ImageView credentialsBad;
    ProgressBar credentialsProgress;

    ImageView cardGood;
    ImageView cardBad;
    ProgressBar cardProgress;

    ImageView tokenGood;
    ImageView tokenBad;
    ProgressBar tokenProgress;

    ImageView paymentGood;
    ImageView paymentBad;
    ProgressBar paymentProgress;

    ImageView[] good;
    ImageView[] bad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ep = EveryPay.getInstance();

        attachUiEvents();
    }

    private void attachUiEvents() {
        credentialsGood = (ImageView)findViewById(R.id.credentials_good);
        credentialsBad = (ImageView)findViewById(R.id.credentials_bad);
        credentialsProgress = (ProgressBar)findViewById(R.id.credentials_progress);

        cardGood = (ImageView)findViewById(R.id.card_good);
        cardBad = (ImageView)findViewById(R.id.card_bad);
        cardProgress = (ProgressBar)findViewById(R.id.card_progress);

        tokenGood = (ImageView)findViewById(R.id.token_good);
        tokenBad = (ImageView)findViewById(R.id.token_bad);
        tokenProgress = (ProgressBar)findViewById(R.id.token_progress);

        paymentGood = (ImageView)findViewById(R.id.payment_good);
        paymentBad = (ImageView)findViewById(R.id.payment_bad);
        paymentProgress = (ProgressBar)findViewById(R.id.payment_progress);

        good = new ImageView[] { credentialsGood, cardGood, tokenGood, paymentGood };
        bad = new ImageView[] { credentialsBad, cardBad, tokenBad, paymentBad };

        for (ImageView img : good)
            img.setColorFilter(getResources().getColor(R.color.tint_good));
        for (ImageView img : bad)
            img.setColorFilter(getResources().getColor(R.color.tint_bad));


        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ep.startFullPaymentFlow(new EveryPayListener() {
                    @Override
                    public void stepSuccess(StepType step) {

                    }

                    @Override
                    public void fullSuccess() {

                    }

                    @Override
                    public void stepFailure(StepType step) {

                    }
                });
            }
        });
    }

}
