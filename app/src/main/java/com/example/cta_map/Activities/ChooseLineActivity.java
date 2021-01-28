package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

        String[] line_names = {"RED", "BLUE", "BROWN", "GREEN", "ORANGE", "PURPLE", "PINK", "YELLOW"};
        for (int i=0; i<images.length; i++){
            list.add(new StationLines(line_names[i], images[i]));
        }
        LineAdapter adapter = new LineAdapter(getApplicationContext(), list);
        line_layout.setAdapter(adapter);
        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }
}
