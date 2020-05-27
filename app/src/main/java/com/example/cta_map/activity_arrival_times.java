package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("Registered")
public class activity_arrival_times extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: refresh layout for train updates
        setContentView(R.layout.activity_arrival_times);
        super.onCreate(savedInstanceState);
        Time time = new Time();
        Context context = getApplicationContext();
        final HashMap<String, String> current_train_info = (HashMap<String, String>) getIntent().getExtras().get("current_train_info");
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader train_station_stops_reader = chicago_transits.setup_file_reader(context, R.raw.train_line_stops);
        ArrayList<String> all_stops = chicago_transits.retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"));
        Bundle bb;
        bb=getIntent().getExtras();
        assert bb != null;
        final String next_stop = bb.getString("next_stop");
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
        final ListView list = (ListView) findViewById(R.id.train_etas);
        list.setAdapter(adapter);
        String specified_train_direction = current_train_info.get("train_direction");
        String target_station =  current_train_info.get("target_station");

        int idx = 0;
        int start;
        int end;

        if (specified_train_direction.equals("1")){
            start = all_stops.indexOf(target_station.replaceAll("[^a-zA-Z0-9]", ""));
            end = all_stops.indexOf(next_stop)+1;
            ArrayList<Integer> range_of_eta = chicago_transits.calculate_station_range_eta(current_train_info, start, end, Integer.parseInt(specified_train_direction), context);
            List<String> all_stops_till_target = all_stops.subList(start , end);
            idx = all_stops_till_target.size() -1;
            for (int i=0; i < all_stops_till_target.size(); i++){
                String remaining_stop = all_stops_till_target.get(idx);
                arrayList.add("ETA To "+remaining_stop +": "+ range_of_eta.get(i)+" Minutes");
                adapter.notifyDataSetChanged();
                idx--;
            }

        }
        else {
            start = all_stops.indexOf(next_stop);
            end = all_stops.indexOf(target_station.replaceAll("[^a-zA-Z0-9]", ""))+1;
            ArrayList<Integer> range_of_eta = chicago_transits.calculate_station_range_eta(current_train_info, start, end, Integer.parseInt(specified_train_direction), context);
            List<String> all_stops_till_target = all_stops.subList(start , end);
            for (int i=0; i < all_stops_till_target.size(); i++){
                String remaining_stop = all_stops_till_target.get(idx);
                arrayList.add("ETA To "+remaining_stop +": "+ range_of_eta.get(i)+" Minutes");
                adapter.notifyDataSetChanged();
                idx++;
            }
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("val", String.valueOf(list.getItemAtPosition(position)));
                String[] list_item = String.valueOf(list.getItemAtPosition(position)).split(":"); //.replaceAll("[^\\d.]", "");
                String target_station_name = list_item[0].split("To")[1].replaceAll(" ","");
                Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
                intent.putExtra("target_station_name", target_station_name);
                intent.putExtra("target_station_type", current_train_info.get("station_type"));
                intent.putExtra("train_direction", current_train_info.get("train_direction"));
                startActivity(intent);
            }
        });
    }

}
