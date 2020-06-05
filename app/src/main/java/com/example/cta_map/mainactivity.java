package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Bundle bb;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
        final ListView list = (ListView) findViewById(R.id.station_lines);
        list.setAdapter(adapter);
       String[] main_menu = new String[]{"Add Favorite Station", "Find Station"};
        for (String items: main_menu){
            arrayList.add(items);
            adapter.notifyDataSetChanged();
        }


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent favorite_station_intent = new Intent(mainactivity.this,ChooseLineActivity.class);
                Intent browse_station_intent = new Intent(mainactivity.this,ChooseLineActivity.class);
                SharedPreferences.Editor connect = getSharedPreferences("CONNECT", MODE_PRIVATE).edit();

                if (position == 0){
                    connect.putBoolean("connection", false);
                    connect.apply();
                    startActivity(favorite_station_intent);


                }
                if (position == 1){
                    connect.putBoolean("connection", true);
                    connect.apply();

                    startActivity(browse_station_intent);

                }

//                Intent intent = new Intent(mainactivity.this,ChooseDirectionActivity.class);
//                intent.putExtra("target_station_type", String.valueOf(list.getItemAtPosition(position)));
//                startActivity(intent);
            }
        });

//        String[] station_lines = new String[]{"Red", "Blue", "Brown", "Green", "Orange", "Purple", "Pink", "Yellow"};
//
//        for (String lines : station_lines){
//            arrayList.add(lines);
//            adapter.notifyDataSetChanged();
//        }
//
//
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mainactivity.this,ChooseDirectionActivity.class);
//                intent.putExtra("target_station_type", String.valueOf(list.getItemAtPosition(position)));
//                startActivity(intent);
//            }
//        });
    }
}