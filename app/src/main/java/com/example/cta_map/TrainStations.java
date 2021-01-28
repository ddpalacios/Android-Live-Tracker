package com.example.cta_map;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.L_stops;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_stations);
        Bundle bb;
        bb = getIntent().getExtras();
        HashMap<String, String> tracking_station = (HashMap<String, String>) bb.getSerializable("tracking_station");

        RecyclerView station_list = (RecyclerView) findViewById(R.id.stations_view);
        ArrayList<Stops> list = new ArrayList<>();

        CTA_DataBase sqlite = new CTA_DataBase(getApplicationContext());

        ArrayList<Object> get_stops_record = sqlite.excecuteQuery(tracking_station.get("station_type").toUpperCase(), "L_STOPS", null, null);
        for (int i = 0; i < get_stops_record.size(); i++) {
            HashMap<String, String> stop_record = (HashMap<String, String>) get_stops_record.get(i);
            if (stop_record.get(tracking_station.get("station_type").toUpperCase()).equals("null")){
                break;
            }
            list.add(new Stops(tracking_station, stop_record.get(tracking_station.get("station_type").toUpperCase()), tracking_station.get("station_type").toLowerCase().trim(), tracking_station.get("station_dir")));
//        }
        StopAdapter a = new StopAdapter(getApplicationContext(), list);
        station_list.setAdapter(a);
        station_list.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    }
