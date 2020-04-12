package com.arjunrao.welfarewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.Profile;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity {
    String ACCOUNT_NUMBER;
    URL imageURL;
    CatLoadingView catLoadingView;
    TextView balancetext,currencytext,banktext,addresstext,routingtext,useridtext,accountid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();


        String name = intent.getStringExtra("USERNAME");
        String image = intent.getStringExtra("USERIMAGE");

        Bitmap profile_picture = getFacebookProfilePicture(image);

        CircleImageView imageView = findViewById(R.id.profile_image2);
        imageView.setImageBitmap(profile_picture);

        TextView name1 = findViewById(R.id.username);
        String joined = "USERNAME: " +  name;
        name1.setText(joined);
        ACCOUNT_NUMBER = intent.getStringExtra("ACCOUNT_NUMBER");
        getAccountInfoSimple(ACCOUNT_NUMBER);
        balancetext = findViewById(R.id.balance2);
        currencytext = findViewById(R.id.currency);
        banktext = findViewById(R.id.bank);
        addresstext = findViewById(R.id.address);
        routingtext = findViewById(R.id.routing);
        accountid = findViewById(R.id.accountid);
        useridtext = findViewById(R.id.userid);




    }

    public Bitmap getFacebookProfilePicture(String uid){
        try {

            uid = Profile.getCurrentProfile().getId();

            imageURL = new URL("https://graph.facebook.com/" + uid + "/picture?type=large");
       //     Log.w("OK",imageURL.toString());


            new DetailsActivity.RetrieveProfilePictureTask().execute();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public class RetrieveProfilePictureTask extends AsyncTask<Void, Void, Void> {

        Bitmap bitmap;

        @Override
        protected void onPreExecute(){
            catLoadingView = new CatLoadingView();
            catLoadingView.setCanceledOnTouchOutside(false);
            catLoadingView.show(getSupportFragmentManager(),"");
        }
        @Override
        protected Void doInBackground(Void ... voids) {

            try {
                //  Toast.makeText(MainActivity.this,imageURL.toString(),Toast.LENGTH_LONG).show();
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {


        }
        @Override
        protected void onPostExecute(Void voids) {
         //   Log.d("AIGHT",String.valueOf(bitmap.getHeight()));
            CircleImageView circleImageView = findViewById(R.id.profile_image2);
            circleImageView.setImageBitmap(bitmap);


        }


    }
    private void redoOAuth() {
        OBPRestClient.clearAccessToken(this);
        Intent oauthActivity = new Intent(this, OAuthActivity.class);
        startActivity(oauthActivity);
    }


    private void getAccountInfoSimple(final String accountnumber){
        new AsyncTask<Void, Void, String>() {

            @Override

            protected String doInBackground(Void... params) {
                try {
                    JSONObject banksJson = OBPRestClient.getAccountInfo(accountnumber);
                   // Log.wtf("OH",banksJson.toString());
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
                    balancetext.setText(result1.getJSONObject("balance").getString("amount"));
                    banktext.setText(result1.getString("bank_id"));
                    currencytext.setText(result1.getJSONObject("balance").getString("currency"));
                    JSONArray addressarray = result1.getJSONArray("account_routings");
                    addresstext.setText("Address: "+addressarray.getJSONObject(0).getString("address"));
                    JSONArray routingarray = result1.getJSONArray("owners");
                    routingtext.setText("Sandbox URL: " + routingarray.getJSONObject(0).getString("provider"));
                    useridtext.setText("Bank User ID: "+ routingarray.getJSONObject(0).getString("id"));
                    accountid.setText(result1.getString("id"));
                    catLoadingView.dismiss();



                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
