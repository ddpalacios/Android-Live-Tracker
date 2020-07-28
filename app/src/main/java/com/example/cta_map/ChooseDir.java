package com.example.cta_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.Database2;

import java.util.ArrayList;

public class ChooseDir extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir);
        Database2 sqlite = new Database2(getApplicationContext());
        ArrayList<StationLines> list = new ArrayList<>();
        RecyclerView line_layout = (RecyclerView) findViewById(R.id.dir_recycler);
        Bundle bb;
        bb = getIntent().getExtras();
        int[] images = {
                bb.getInt("color")};
        final String color_name = bb.getString("color_name");
        String south_q = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+color_name.toUpperCase()+"'";
        String north_q = "SELECT northbound FROM main_stations WHERE main_station_type = '"+color_name.toUpperCase()+"'";
        String N_main_station = sqlite.getValue(north_q);
        String S_main_station = sqlite.getValue(south_q);






        Button north = (Button) findViewById(R.id.north_btn);
        Button south = (Button) findViewById(R.id.south_btn);


        north.setText(N_main_station);
        south.setText(S_main_station);

        north.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
                intent.putExtra("station_type", color_name);
                intent.putExtra("station_dir", "1");
                startActivity(intent);
            }
        });


        south.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
                intent.putExtra("station_type", color_name);
                intent.putExtra("station_dir", "5");

                startActivity(intent);
            }
        });






        dirAdapter adapter = new dirAdapter(getApplicationContext(),images);
        line_layout.setAdapter(adapter);
        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



    }
}