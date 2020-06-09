package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.cta_map.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapRelativeListView {
    private ArrayList<String> arrayList;
    private Context  context;
    public ArrayAdapter<String> adapter;
    private ListView list;


    public MapRelativeListView(Context context, View list){
        this.context = context;
        this.arrayList = new ArrayList<>();
        this.adapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, arrayList);
        this.list = (ListView) list;
        this.list.setAdapter(this.adapter);

    }

    public void add_to_list_view(final ArrayList<Integer> train_etas,
                                 final SharedPreferences TRAIN_RECORD,
                                 final HashMap<String, String> current_train_info,
                                 final ArrayList<HashMap> chosen_trains,
                                 final boolean[] connect,
                                 String dir){
        this.adapter.clear();
        final String[]  specified_train_direction = new String[]{String.valueOf(TRAIN_RECORD.getInt("station_dir", 5))};
        final String target_station_name = TRAIN_RECORD.getString("station_name", null);
        final String target_station_type =  TRAIN_RECORD.getString("station_type", null);

        final Context context = this.context;
        if (train_etas.size() == 0 || current_train_info == null){
            if (dir.equals("1")) {
                this.arrayList.add(0, "No Trains Available." +" (North Bound)");
            }
            else if (dir.equals("5")){
                this.arrayList.add(0, "No Trains Available." +" (South Bound)");

            }
        }
        else {
            if (current_train_info.get("train_direction").equals("1")) {
                this.arrayList.add(0, target_station_name +" (North Bound)");
            }
           else if (current_train_info.get("train_direction").equals("5")){
                this.arrayList.add(0, target_station_name +" (South Bound)");

            }

            for (int current_eta : train_etas) {
                this.arrayList.add("To " + current_train_info.get("main_station") + ": " + current_eta + " Minutes");
                this.adapter.notifyDataSetChanged();

            }
        }
        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 ){
                    Toast.makeText(context, list.getItemAtPosition(position)+"", Toast.LENGTH_SHORT).show();
                }
               else {
                    if (connect[0]) {
                        connect[0] = false;
                        Log.d("Connection Status", "Connection Closed");
                        Toast.makeText(context, "DISCONNECTED", Toast.LENGTH_SHORT).show();
                    }
                    String[] list_item = String.valueOf(list.getItemAtPosition(position)).split(":");
                    String key = list_item[1].replaceAll("[^\\d.]", "");
                    for (HashMap<String, String> each_train : chosen_trains) {
                        if (each_train.containsKey(key)) {
                            Log.e("next ", each_train.get("next_stop") + "");
                            Intent intent = new Intent(context, activity_arrival_times.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("current_train_info", each_train);
                            context.startActivity(intent);
                        }

                    }
                }
            }
        });
    }
}
