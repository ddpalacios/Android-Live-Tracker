package com.example.cta_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChooseDir extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir);

        ArrayList<StationLines> list = new ArrayList<>();
        RecyclerView line_layout = (RecyclerView) findViewById(R.id.dir_recycler);
        Bundle bb;
        bb = getIntent().getExtras();
        int[] images = {
                bb.getInt("color")};
        String color_name = bb.getString("color_name");

        Button north = (Button) findViewById(R.id.north_btn);
        Button south = (Button) findViewById(R.id.south_btn);
        north.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
                startActivity(intent);
            }
        });


        south.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDir.this, TrainStations.class);
                startActivity(intent);
            }
        });






        dirAdapter adapter = new dirAdapter(getApplicationContext(),images);
        line_layout.setAdapter(adapter);
        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



    }
}