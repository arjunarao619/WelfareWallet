package com.arjunrao.welfarewallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.arjunrao.welfarewallet.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProductsAdapter adapter;
    List<Product> productList;
    String ACCOUNT_NUMBER = "9988776655";
    String amount,from,to,date,subsidy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Intent intent = getIntent();
        ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");

        getTransactionSimple(ACCOUNT_NUMBER);








    }

        private void getTransactionSimple(final String accountnumber){
            new AsyncTask<Void, Void, String>() {

                @Override

                protected String doInBackground(Void... params) {
                    try {
                        JSONObject banksJson = OBPRestClient.getTransactions(accountnumber);
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
                        JSONObject result1 = new JSONObject(result);
                        JSONArray transactionArray = result1.getJSONArray("transactions");
                        productList = new ArrayList<>();

                        ArrayList<String> list = new ArrayList<>();

                        if (transactionArray != null) {
                            for (int i = 0; i < transactionArray.length(); i++) {

                                JSONObject temp = transactionArray.getJSONObject(i);

                                amount = temp.getJSONObject("details").getJSONObject("value").getString("amount");
                                date = temp.getJSONObject("details").getString("completed");
                                if(Double.parseDouble(amount) < 0.0){
                                    from = temp.getJSONObject("this_account").getString("id");
                                    to = temp.getJSONObject("other_account").getJSONObject("holder").getString("name");

                                }
                                else if(Double.parseDouble(amount) > 0.0){
                                    to = temp.getJSONObject("this_account").getString("id");
                                    from = temp.getJSONObject("other_account").getJSONObject("holder").getString("name");

                                }
                                //we now have from,to,amount,date write it to the object





                                Product p = new Product(from,to,amount,date);
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
