package com.arjunrao.welfarewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {
    String ACCOUNT_NUMBER;
    TextInputEditText accountfromedit,accounttoedit,amountedit,remarksedit;
    String accountto,remarks,amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");

        accountfromedit = findViewById(R.id.fromedit);
        accountfromedit.setText(ACCOUNT_NUMBER);
        accountfromedit.setEnabled(false);



        Button pay = findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                accounttoedit = findViewById(R.id.toedit);
                accountto = accounttoedit.getText().toString();
                remarksedit = findViewById(R.id.remarksedit);
                remarks = remarksedit.getText().toString();

                amountedit = findViewById(R.id.amountedit);
                amount = amountedit.getText().toString();

                Intent intent = new Intent(PaymentActivity.this, ConfirmTransferActivity.class);
                intent.putExtra("ACCOUNT_NUMBER",ACCOUNT_NUMBER);
                intent.putExtra("to",accountto);
                intent.putExtra("amount",amount);
                intent.putExtra("remarks",remarks);

                startActivity(intent);

                //createSimpleTransaction(ACCOUNT_NUMBER,accountto,amount,remarks);

            }
        });








    }



}
