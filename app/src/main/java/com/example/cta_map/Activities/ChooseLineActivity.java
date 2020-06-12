package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.R;

import java.util.ArrayList;

public class ChooseLineActivity extends AppCompatActivity {
    @SuppressLint({"WrongConstant", "ShowToast"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_line);
        super.onCreate(savedInstanceState);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor TRAIN_SELECTION_VALUES =  getSharedPreferences("Train_Selection_Values", MODE_PRIVATE).edit();
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        final ListView list = (ListView) findViewById(R.id.station_lines);
        list.setAdapter(adapter);
        String[] station_lines = new String[]{"Red", "Blue", "Brown", "Green", "Orange", "Purple", "Pink", "Yellow"};
        for (String lines : station_lines){
            arrayList.add(lines);
            adapter.notifyDataSetChanged();
        }


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseLineActivity.this, ChooseDirectionActivity.class);
                intent.putExtra("target_station_type", String.valueOf(list.getItemAtPosition(position)));
                startActivity(intent);
            }
        });

    }
}