package com.example.cta_map;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_stations);
        RecyclerView station_list = (RecyclerView) findViewById(R.id.stations_view);
        ArrayList<Stops> list = new ArrayList<>();

        for (int i=0; i<20; i++){
            list.add(new Stops("Granville", R.drawable.blue));

        }

        StopAdapter a = new StopAdapter(getApplicationContext(), list);
        station_list.setAdapter(a);
        station_list.setLayoutManager(new LinearLayoutManager(this));










    }

    }
