package com.everypay.everypay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.everypay.sdk.EveryPay;

public class MainActivity extends AppCompatActivity {

    EveryPay ep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ep = EveryPay.getInstance();
    }

}
