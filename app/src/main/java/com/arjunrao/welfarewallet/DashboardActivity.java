package com.arjunrao.welfarewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    String ACCOUNT_NUMBER = "9988776655";
    String[] finallist;
    TextView accnumber;
// ...
private static final String BASE_URL = "https://apisandbox.openbankproject.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        getSupportActionBar().setTitle("WelfareWallet Dashboard");

                final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout2);

        // listen refresh event
                layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getBalanceSimple(ACCOUNT_NUMBER);
                        layout.setRefreshing(false);
                    }
                });

        // refresh complete
                layout.setRefreshing(false);

        //initiate transation TODO
        ImageButton pay = findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this,PaymentActivity.class);
                startActivity(intent);
            }
        });

        ImageButton details = findViewById(R.id.accountdetails);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, DetailsActivity.class);
                intent.putExtra("ACCOUNT_NUMBER",ACCOUNT_NUMBER);
                startActivity(intent);
            }
        });


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
//                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner1);
//                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_spinner_item, areas);
//                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                areaSpinner.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        //// COMPLETED SPINNER POPULATION
        //TODO BEGIN BALANCE INFORMATION FROM OPENAPI and current account number
        getBalanceSimple(ACCOUNT_NUMBER);
        //TODO BEGIN DISPLAYING TRANSACTION HISTORY
        ImageButton transact = findViewById(R.id.transact);
        transact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, TransactionActivity.class);
                intent.putExtra("ACCOUNT_NUMBER",ACCOUNT_NUMBER);
                startActivity(intent);
            }
        });

        //TODO CHANGE ACCOUNT NUMBER FUNCTIONALITY
        accnumber = findViewById(R.id.accnumber);
        accnumber.setText("Account Number : 000");
        getAccountIdsSimple(); //WE NEED TO SPECIFY ACCOUNT NUMBER IN THE BEGINNING, TOO
        ImageButton changeaccount = findViewById(R.id.changeaccount);
        changeaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAccountIdsSimple();

            }
        });

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
        ImageButton signout = findViewById(R.id.signout);
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

    private void getBalanceSimple(final String accountnumber){
        new AsyncTask<Void, Void, String>() {

            @Override

            protected String doInBackground(Void... params) {
                try {
                    JSONObject banksJson = OBPRestClient.getBalance(accountnumber);
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
    }

    private void getAccountIdsSimple(){

        new AsyncTask<Void, Void, String>() {

            @Override

            protected String doInBackground(Void... params) {
                try {
                    JSONObject banksJson = OBPRestClient.getAccountIds();
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
                    JSONObject accountIds = new JSONObject(result);
                    Log.d("ACCOUNTCHANGESUCESFULL",result);
                    JSONArray account_id_array = accountIds.getJSONArray("accounts");
                    ArrayList<String> list = new ArrayList<>();

                    if (account_id_array != null) {
                        for (int i = 0; i < account_id_array.length(); i++) {
                            JSONObject temp = account_id_array.getJSONObject(i);
                            String str = (String) temp.getString("id");
                            list.add(str);
                        }
                    }
                    Object[] mStringArray = list.toArray();
                    //final String[] finallist = new String[100];

                   finallist = new String[mStringArray.length];
                   System.arraycopy(mStringArray, 0, finallist, 0, mStringArray.length);


                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("Select an account number from bank HSBC-TEST");
                    builder.setCancelable(false);

                    builder.setItems(finallist, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ACCOUNT_NUMBER = finallist[which];
                            getBalanceSimple(ACCOUNT_NUMBER);
                            accnumber.setText("Account Number : " + ACCOUNT_NUMBER);
                        }
                    });
                    builder.show();






                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }.execute();

    }



}
