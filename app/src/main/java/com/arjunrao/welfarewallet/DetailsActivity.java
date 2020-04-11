package com.arjunrao.welfarewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {
    String ACCOUNT_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");
        

    }
}
