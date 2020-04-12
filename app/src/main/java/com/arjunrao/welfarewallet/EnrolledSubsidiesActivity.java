package com.arjunrao.welfarewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class EnrolledSubsidiesActivity extends AppCompatActivity {

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = true;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_subsidies);
        getSupportActionBar().setTitle("Your Subsidy Schemes");

        mDatabase = FirebaseDatabase.getInstance().getReference("EnrolledSchemes");

        //populate the spinner
        final int count = 0;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();
                PieChartView pieChartView = findViewById(R.id.chart);
                List<SliceValue> pieData = new ArrayList<>();

                String[] colors = new String[]{"#e53935","FF4081","#ff0099cc","#D81B60","#ffffbb33"};
                ArrayList<String> colorlist = new ArrayList<>();
                colorlist.add("#e53935");
                colorlist.add("#FF4081");
                colorlist.add("#ff0099cc");
                colorlist.add("#D81B60");
                colorlist.add("#ffffbb33");

                int count1=0;
                for(DataSnapshot myItem : dataSnapshot.getChildren()){

                    Integer areavalue = myItem.getValue(Integer.class);
                    String areaname = myItem.getKey();
                    areas.add(areaname);
                    String a = "#D81B60";
                    if(count1 == 1)a = "#ff0099cc";
                    if(count1 == 2)a = "#e53935";
                    if(count1 == 3)a = "#FF4081";
                    if(count1 == 4)a = "#ffffbb33";
                    if(count1 > 4)a = "#e53935";
                    pieData.add(new SliceValue(areavalue, Color.parseColor(a)).setLabel((areaname + "\n" + areavalue.toString() + " HKD")));

                    count1++;



                }

                PieChartData pieChartData = new PieChartData(pieData);

                pieChartData.setHasLabels(true);
                pieChartData.setHasLabels(true).setValueLabelTextSize(12);
                //pieChartData.setHasCenterCircle(true).setCenterText1("Sales in million");
                //pieChartData.setHasCenterCircle(true).setCenterText1("Your subsidies").setCenterText1FontSize(0).setCenterText1Color(Color.parseColor("#0097A7"));

                pieChartView.setPieChartData(pieChartData);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });







    }


}
