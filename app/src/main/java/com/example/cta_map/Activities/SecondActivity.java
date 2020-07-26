package com.example.cta_map.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SecondActivity extends AppCompatActivity {
    ImageView imageView;
    TextView title, des, NorthBound, SouthBound;
    String data1, data2, north, south;
    int myImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        imageView = findViewById(R.id.imageView2);
        des = findViewById(R.id.description);
        title = findViewById(R.id.titddle);
        NorthBound = findViewById(R.id.north);
        SouthBound = findViewById(R.id.south);
        getData();
        setData();
        final Intent intent = new Intent(this, TrainStationActivity.class);
        NorthBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("NORTH", "CLICKED");
                startActivity(intent);
            }
        });

        SouthBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SOUTH", "CLICKED");
                startActivity(intent);

            }
        });


    }


    private void getData(){
        if (getIntent().hasExtra("myImage") && getIntent().hasExtra("data1")){
            data1 = getIntent().getStringExtra("data1");
            Database2 sqlite = new Database2(getApplicationContext());
            String station_type = data1.replaceAll("Line", "").replaceAll(" ", "").toUpperCase();
            myImage = getIntent().getIntExtra("myImage", 1);
            String query1 ="SELECT northbound FROM main_stations WHERE main_station_type = '"+station_type+"'";
            String query2 ="SELECT southbound1 FROM main_stations WHERE main_station_type = '"+station_type+"'";
            north = sqlite.getValue(query1);
            south = sqlite.getValue(query2);
            sqlite.close();



        }else {
            Log.e("No Data", "none");
        }
    }

    private void setData(){
        title.setText(data1);
        NorthBound.setText(north+" (North)");
        SouthBound.setText(south+" (South)");

        imageView.setImageResource(myImage);
    }
}
