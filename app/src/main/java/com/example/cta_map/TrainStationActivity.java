package com.example.cta_map;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TrainStationActivity  extends AppCompatActivity {
    Bundle bb; // Retrieve data from main screen

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_station_activity);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ListView list = (ListView) findViewById(R.id.train_stops);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
        Intent intent = this.getIntent();
        String train_direction = null;
        String train_direction_name = null;
        String target_station_type = null;
        if (intent != null) {
            target_station_type = intent.getStringExtra("target_station_type");
            train_direction_name = intent.getStringExtra("train_direction_name");
            train_direction = intent.getStringExtra("train_direction");
        }
    ArrayList<String> train_stops = chicago_transits.retrieve_line_stations(chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops), target_station_type);
    for (String each_stop: train_stops){
        arrayList.add(each_stop);
        adapter.notifyDataSetChanged();


    }

    }
}
