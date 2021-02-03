package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseDir extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir);
        Bundle bb;
        bb = getIntent().getExtras();
        final HashMap<String, String> tracking_station = (HashMap<String, String>) bb.getSerializable("tracking_station");
        if (tracking_station == null){
            Toast.makeText(getApplicationContext(), "No Data Passed. ", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Stops> list = new ArrayList();
        RecyclerView main_direction_rv = findViewById(R.id.main_stations_rv);
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> MAIN_STATIONS = cta_dataBase.excecuteQuery("*", "MAIN_STATIONS", "STATION_TYPE = '"+tracking_station.get("station_type").trim()+"'",null,null);

        HashMap<String, String> main_station_record = (HashMap<String, String>) MAIN_STATIONS.get(0);
        list.add(new Stops(tracking_station, main_station_record.get("NORTHBOUND"),main_station_record.get("STATION_TYPE").toLowerCase(),null));
        list.add(new Stops(tracking_station, main_station_record.get("SOUTHBOUND"),main_station_record.get("STATION_TYPE").toLowerCase() ,null));
        StopAdapter adapter = new StopAdapter(getApplicationContext(), list);
        main_direction_rv.setAdapter(adapter);
        main_direction_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        final String northbound = main_station_record.get("NORTHBOUND");
//        final String southbound = main_station_record.get("SOUTHBOUND");





//        CardView northcard = findViewById(R.id.northboundCardView);
//        CardView southcard = findViewById(R.id.southboundCardView);
//
//        ArrayList<Object> MAIN_STATIONS = sqlite.excecuteQuery("*", "MAIN_STATIONS", "STATION_TYPE = '"+tracking_station.get("station_type").trim()+"'",null);
//        HashMap<String, String> main_station_record = (HashMap<String, String>) MAIN_STATIONS.get(0);
//        final String northbound = main_station_record.get("NORTHBOUND");
//        final String southbound = main_station_record.get("SOUTHBOUND");
//
//        ImageView northboundImg = findViewById(R.id.NorthBoundImage);
//        ImageView southboundImg = findViewById(R.id.SouthBoundImage);
//
//
//        HashMap<String, Integer> images = new HashMap<>();
//        images.put("RED",   R.drawable.red);
//        images.put("BLUE",   R.drawable.blue);
//        images.put("BROWN",   R.drawable.brown);
//        images.put("GREEN",   R.drawable.green);
//        images.put("ORANGE",   R.drawable.orange);
//        images.put("PURPLE",   R.drawable.purple);
//        images.put("PINK",   R.drawable.pink);
//        images.put("YELLOW",   R.drawable.yellow);
//
//        TextView northboundText = findViewById(R.id.NorthBoundText);
//        TextView southboundText = findViewById(R.id.SouthBoundText);
//        southboundImg.setImageResource(images.get(tracking_station.get("station_type")));
//        northboundImg.setImageResource(images.get(tracking_station.get("station_type")));
//        northboundText.setText(northbound +" (North)");
//        southboundText.setText(southbound +" (South)");
//
//
//        northcard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
//                tracking_station.put("station_dir","1");
//                tracking_station.put("main_station", northbound);
//                intent.putExtra("tracking_station", (Serializable) tracking_station);
//                startActivity(intent);
//            }
//        });
//
//        southcard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
//                tracking_station.put("station_dir","0");
//                tracking_station.put("main_station", southbound);
//                intent.putExtra("tracking_station", (Serializable) tracking_station);
//                startActivity(intent);
//            }
//        });








//        Database2 sqlite = new Database2(getApplicationContext());
//        ArrayList<StationLines> list = new ArrayList<>();
//        RecyclerView line_layout = (RecyclerView) findViewById(R.id.dir_recycler);
//        int[] images = {
//                bb.getInt("color")};
//        final String color_name = bb.getString("color_name");
//        String south_q = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+color_name.toUpperCase()+"'";
//        String north_q = "SELECT northbound FROM main_stations WHERE main_station_type = '"+color_name.toUpperCase()+"'";
//        String N_main_station = sqlite.getValue(north_q);
//        String S_main_station = sqlite.getValue(south_q);
//
//
//
//
//
//
//        Button north = (Button) findViewById(R.id.north_btn);
//        Button south = (Button) findViewById(R.id.south_btn);
//
//
//        north.setText(N_main_station);
//        south.setText(S_main_station);
//
//        north.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
//                intent.putExtra("station_type", color_name);
//                intent.putExtra("station_dir", "1");
//                startActivity(intent);
//            }
//        });
//
//
//        south.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
//                intent.putExtra("station_type", color_name);
//                intent.putExtra("station_dir", "5");
//
//                startActivity(intent);
//            }
//        });
//
//
//
//
//
//
//        dirAdapter adapter = new dirAdapter(getApplicationContext(),images);
//        line_layout.setAdapter(adapter);
//        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
//
//
    }
}