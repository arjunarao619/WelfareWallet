package com.arjunrao.welfarewallet;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.arjunrao.welfarewallet.R;
import com.baoyz.widget.PullRefreshLayout;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProductsAdapter adapter;
    List<Product> productList;
    String ACCOUNT_NUMBER = "9988776655";
    String amount,from,to,date,subsidy,comments,balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.refresh_layout);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactionSimple(ACCOUNT_NUMBER);
                layout.setRefreshing(false);
            }
        });

        // refresh complete
        layout.setRefreshing(false);

        Intent intent = getIntent();
        ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");
        ActionBar a = getSupportActionBar();
        a.setTitle(ACCOUNT_NUMBER + ": Transaction History");
        getTransactionSimple(ACCOUNT_NUMBER);

    }

        private void getTransactionSimple(final String accountnumber){
            new AsyncTask<Void, Void, String>() {

                @Override

                protected String doInBackground(Void... params) {
                    try {
                        JSONObject banksJson = OBPRestClient.getTransactions(accountnumber);
                        Log.d("INSIDE ASYNCTASK",banksJson.toString());
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
                   // Log.d("OKOKOKOK",result);
                    try {
                        JSONObject result1 = new JSONObject(result);
                        JSONArray transactionArray = result1.getJSONArray("transactions");
                        productList = new ArrayList<>();

                        ArrayList<String> list = new ArrayList<>();

                        if (transactionArray != null) {
                            for (int i = 0; i < transactionArray.length(); i++) {

                                JSONObject temp = transactionArray.getJSONObject(i);

                                amount = temp.getJSONObject("details").getJSONObject("value").getString("amount");
                                date = temp.getJSONObject("details").getString("completed");
                                balance = temp.getJSONObject("details").getJSONObject("new_balance").getString("amount");
                                comments = temp.getJSONObject("details").getString("description");

                                if(Double.parseDouble(amount) < 0.0){
                                    from = temp.getJSONObject("this_account").getString("id");
                                    to = temp.getJSONObject("other_account").getJSONObject("holder").getString("name");

                                }
                                else if(Double.parseDouble(amount) > 0.0){
                                    to = temp.getJSONObject("this_account").getString("id");
                                    from = temp.getJSONObject("other_account").getJSONObject("holder").getString("name");

                                }
                                //we now have from,to,amount,date write it to the object
                                from = "From: " + from;
                                to = "To: " + to;
                                amount = "Amount: " + amount + " HKD";
                                date = "Date: " + date;
                                balance = "Balance: " + balance + " HKD";
                                comments = "Comments: " + comments;
                                Product p;
                                if(comments.equals("subsidy")){
                                     p = new Product(from,to,amount,date,comments,balance,true);}
                                else{
                                    p = new Product(from,to,amount,date,comments,balance,false);
                                }
                                productList.add(p);

                            }


                        }
                        recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionActivity.this));
                        adapter = new ProductsAdapter(TransactionActivity.this, productList);
                        recyclerView.setAdapter(adapter);


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
