package com.example.cta_map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.Database2;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseDir extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir);
        Bundle bb;
        bb = getIntent().getExtras();
        final String station_type = bb.getString("station_type");
        int station_image  = bb.getInt("station_color");
        CardView northcard = findViewById(R.id.northboundCardView);
        CardView southcard = findViewById(R.id.southboundCardView);

        CTA_DataBase sqlite = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> get_mainStation_record = sqlite.excecuteQuery("*", "main_stations", "main_station_type = '"+station_type.trim()+"'",null);
        HashMap<String, String> main_station_record = (HashMap<String, String>) get_mainStation_record.get(0);
        String northboundID = main_station_record.get("northbound");
        String southboundID = main_station_record.get("southbound");

        HashMap<String, String> get_northbound_record = (HashMap<String, String>) sqlite.excecuteQuery("*", "cta_stops", "MAP_ID = '"+northboundID+"'",null).get(0);
        HashMap<String, String> get_southbound_record = (HashMap<String, String>) sqlite.excecuteQuery("*", "cta_stops", "MAP_ID = '"+southboundID+"'",null).get(0);
        final String north_station = get_northbound_record.get("station_name");
        final String south_station = get_southbound_record.get("station_name");


        TextView northboundText = findViewById(R.id.NorthBoundText);
        TextView southboundText = findViewById(R.id.SouthBoundText);

        ImageView northboundImg = findViewById(R.id.NorthBoundImage);
        ImageView southboundImg = findViewById(R.id.SouthBoundImage);




        southboundImg.setImageResource(station_image);
        northboundImg.setImageResource(station_image);
        northboundText.setText(north_station +" (North)");
        southboundText.setText(south_station +" (South)");


        northcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
                intent.putExtra("station_dir", "1");
                intent.putExtra("station_type", station_type);
                intent.putExtra("station_name", north_station);
                startActivity(intent);


            }
        });

        southcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
                intent.putExtra("station_dir", "5");
                intent.putExtra("station_type", station_type);
                intent.putExtra("station_name", south_station);
                startActivity(intent);


            }
        });








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