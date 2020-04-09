package com.arjunrao.welfarewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    URL imageURL;
    private DatabaseReference mDatabase;
    CatLoadingView mView;
    String[]items;
// ...
private static final String BASE_URL = "https://apisandbox.openbankproject.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        ////// begin spinner population
        mDatabase = FirebaseDatabase.getInstance().getReference("EnrolledSchemes");

        //populate the spinner
        int count = 0;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();


                for(DataSnapshot myItem : dataSnapshot.getChildren()){

                    Log.d("ok",myItem.getKey());
                    String areaname = myItem.getKey();
                    areas.add(areaname);


                }
                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner1);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        //// COMPLETED SPINNER POPULATION
        //TODO BEGIN BALANCE INFORMATION FROM OPENAPI

//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(BASE_URL + "/obp/v4.0.0/banks/hsbc-test/balances", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Log.d("gg",responseBody.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.wtf("OO",responseBody.toString());
//                error.printStackTrace();
//            }
//        });
        new AsyncTask<Void, Void, String>() {

            @Override
            /**
             * @return A String containing the json representing the available banks, or an error message
             */
            protected String doInBackground(Void... params) {
                try {
                    JSONObject banksJson = OBPRestClient.getBalance();
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
                    JSONObject balance1 = result1.getJSONObject("balance");
                    String balance = balance1.getString("amount");
                    TextView balancetext = findViewById(R.id.balance);
                    balancetext.setText(balance);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
















        Intent intent = getIntent();
        String email = intent.getStringExtra("USEREMAIL");
        String name = intent.getStringExtra("USERNAME");
        String image = intent.getStringExtra("USERIMAGE");

        Bitmap profile_picture = getFacebookProfilePicture(image);

        CircleImageView imageView = findViewById(R.id.profile_image);
        imageView.setImageBitmap(profile_picture);

        TextView name1 = findViewById(R.id.welcomename);
        String joined = "Welcome, " +  name;
        name1.setText(joined);





        //on clicking signout
        Button signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(),"");

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // this code will be executed after 2 seconds
                    }
                }, 5000);
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            }
        });

    }

    public Bitmap getFacebookProfilePicture(String uid){
        try {

            uid = Profile.getCurrentProfile().getId();

            imageURL = new URL("https://graph.facebook.com/" + uid + "/picture?type=large");
            Log.w("OK",imageURL.toString());


            new RetrieveProfilePictureTask().execute();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public class RetrieveProfilePictureTask extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;

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
            Log.d("AIGHT",String.valueOf(bitmap.getHeight()));
            CircleImageView circleImageView = findViewById(R.id.profile_image);
            circleImageView.setImageBitmap(bitmap);
        }


    }
    private void redoOAuth() {
        OBPRestClient.clearAccessToken(this);
        Intent oauthActivity = new Intent(this, OAuthActivity.class);
        startActivity(oauthActivity);
    }

}
