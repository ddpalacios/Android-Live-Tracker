package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.LineAdapter;
import com.example.cta_map.R;
import com.example.cta_map.StationLines;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseLineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_activity);
        ArrayList<StationLines> list = new ArrayList<>();
        RecyclerView line_layout = (RecyclerView) findViewById(R.id.main_line);
        Log.e("context", getBaseContext()+" context");

        int[] images = {
                R.drawable.red,
                R.drawable.blue,
                R.drawable.brown,
                R.drawable.green,
                R.drawable.orange,
                R.drawable.purple,
                R.drawable.pink,
                R.drawable.yellow
                };

        CTA_DataBase sqlite = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> record = sqlite.excecuteQuery("*", "main_stations", null);

        for (int i=0; i<record.size(); i++){
            HashMap<String, String> main_station_record = (HashMap<String, String>) record.get(i);
            list.add(new StationLines(main_station_record.get("main_station_type"), images[i]));

        }

        LineAdapter adapter = new LineAdapter(getApplicationContext(), list);
        line_layout.setAdapter(adapter);
        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }
}
