package com.example.cta_map;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.Database2;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_stations);
        Bundle bb;
        bb = getIntent().getExtras();
        String station_dir = bb.getString("station_dir");
        String station_type = bb.getString("station_type");
        String main_station_name = bb.getString("station_name");
        Log.e(Thread.currentThread().getName(), main_station_name+" Station");

        RecyclerView station_list = (RecyclerView) findViewById(R.id.stations_view);
        ArrayList<Stops> list = new ArrayList<>();

        CTA_DataBase sqlite = new CTA_DataBase(getApplicationContext());

        ArrayList<Object> get_stops_record = sqlite.excecuteQuery("*", "line_stops_table", null,null);
        for (int i=0; i< get_stops_record.size(); i++){
            HashMap<String, String> stop_record = (HashMap<String, String>) get_stops_record.get(i);
           String current_stop= stop_record.get(station_type.toLowerCase().trim());
           if (current_stop.equals("null")){
               break;
           }
            list.add(new Stops(current_stop, station_type.toLowerCase().trim(), station_dir));
        }
        StopAdapter a = new StopAdapter(getApplicationContext(), list);
        station_list.setAdapter(a);
        station_list.setLayoutManager(new LinearLayoutManager(this));

        }

    }
