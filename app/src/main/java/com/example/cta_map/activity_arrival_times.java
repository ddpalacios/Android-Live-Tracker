package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@SuppressLint("Registered")
public class activity_arrival_times extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_arrival_times);
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        HashMap<String,String> current_train_info = (HashMap<String,String>) getIntent().getExtras().get("current_train_info");
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader train_station_stops_reader = chicago_transits.setup_file_reader(context, R.raw.train_line_stops);
        ArrayList<String> all_stops = chicago_transits.retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"));
        for (String each_stop : all_stops){



        }




    }



}
