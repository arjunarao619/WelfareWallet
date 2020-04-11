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
                createSimpleTransaction(ACCOUNT_NUMBER,accountto,amount,remarks);

            }
        });








    }

    private void createSimpleTransaction(final String accountfrom, final String accountto, final String amount, final String remarks){
        new AsyncTask<Void, Void, String>() {

            @Override

            protected String doInBackground(Void... params) {
                try {
                    Log.w("DETAILS",accountfrom + " " + accountto+ " " + amount + " " + remarks);
                    JSONObject banksJson = OBPRestClient.getOAuthedJsonPost("https://apisandbox.openbankproject.com/obp/v4.0.0/banks/hsbc-test/accounts/" + accountfrom + "/owner/transaction-request-types/SANDBOX_TAN/transaction-requests",accountto,amount,remarks);

                    return banksJson.toString();
                } catch (ExpiredAccessTokenException e) {
                    // login again / re-authenticate
                    redoOAuth();
                    return "";
                } catch (ObpApiCallFailedException e) {
                    return "Sorry, there was an error!";
                }
            }
            @Override
            protected void onPostExecute(String result) {
                Log.d("OKOKOKOK",result);
                try {
                    Log.d("PERFECT",result);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void redoOAuth() {
        OBPRestClient.clearAccessToken(this);
        Intent oauthActivity = new Intent(this, OAuthActivity.class);
        startActivity(oauthActivity);
    }

}
