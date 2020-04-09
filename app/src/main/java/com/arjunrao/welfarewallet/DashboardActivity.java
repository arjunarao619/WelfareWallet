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
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    URL imageURL;
    private DatabaseReference mDatabase;
    CatLoadingView mView;
    String[]items;
// ...

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
}
