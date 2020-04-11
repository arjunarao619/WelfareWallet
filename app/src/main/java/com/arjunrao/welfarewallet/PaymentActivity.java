package com.arjunrao.welfarewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {
    String ACCOUNT_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");


        createSimpleTransaction();
    }

    private void createSimpleTransaction(){
        new AsyncTask<Void, Void, String>() {

            @Override

            protected String doInBackground(Void... params) {
                try {
                    JSONObject banksJson = OBPRestClient.getOAuthedJsonPost("https://apisandbox.openbankproject.com/obp/v4.0.0/banks/hsbc-test/accounts/1155112355/owner/transaction-request-types/SANDBOX_TAN/transaction-requests");
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
