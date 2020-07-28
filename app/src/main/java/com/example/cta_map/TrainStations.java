package com.example.cta_map;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.Database2;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_stations);


//
        RecyclerView station_list = (RecyclerView) findViewById(R.id.stations_view);
        ArrayList<Stops> list = new ArrayList<>();
        Database2 sqlite = new Database2(getApplicationContext());
        Bundle bb = getIntent().getExtras();
        String station_type = bb.getString("station_type");
        String station_dir = bb.getString("station_dir");
        ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", station_type.replaceAll(" ", ""));
//        int type = TrainLineKeyCodes.get(station_type.replaceAll(" ", "").toLowerCase());
        if (station_type.equals("purple")){
            line_stops.subList(9,18).clear();
        }

        for (int i=0; i<line_stops.size(); i++){
            list.add(new Stops(line_stops.get(i), station_type.replaceAll(" ","").toLowerCase(), station_dir));

        }

        StopAdapter a = new StopAdapter(getApplicationContext(), list);
        station_list.setAdapter(a);
        station_list.setLayoutManager(new LinearLayoutManager(this));










    }

    }
