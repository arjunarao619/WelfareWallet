package com.arjunrao.welfarewallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONObject;

public class ConfirmTransferActivity extends AppCompatActivity {
    CatLoadingView catLoadingView;

    TextView to,from,amount2,remarks2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_transfer);

        Intent intent = getIntent();
        final String ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");
        final String accountto = intent.getStringExtra("to");
        final String amount = intent.getStringExtra("amount");
        final String remarks = intent.getStringExtra("remarks");

        to = findViewById(R.id.to);
        from = findViewById(R.id.from);
        remarks2 = findViewById(R.id.remarks2);
        amount2 = findViewById(R.id.tv_amount);

       to.setText(accountto);from.setText(ACCOUNT_NUMBER);remarks2.setText(remarks);amount2.setText(amount);

        Button transfer = findViewById(R.id.btn_start_transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSimpleTransaction(ACCOUNT_NUMBER,accountto,amount,remarks);
            }
        });

        Button cancel = findViewById(R.id.btn_cancel_transfer);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }

    private void createSimpleTransaction(final String accountfrom, final String accountto, final String amount, final String remarks){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute(){
                catLoadingView = new CatLoadingView();
                catLoadingView.setCanceledOnTouchOutside(false);
                catLoadingView.show(getSupportFragmentManager(),"");
            }
            @Override

            protected String doInBackground(Void... params) {
                try {
                   // Log.w("DETAILS",accountfrom + " " + accountto+ " " + amount + " " + remarks);
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
              //  Log.d("OKOKOKOK",result);
                try {
                    //Log.d("PERFECT",result);
                    catLoadingView.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmTransferActivity.this);
                    builder.setTitle("Transfer Completed Successfully Check API Console for details!");
                    builder.setMessage("Press OK to go to Dashboard");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ConfirmTransferActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();

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
