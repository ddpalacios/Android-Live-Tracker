package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapRelativeListView {
//    private  ArrayAdapter<String> adapter;
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

    public void add_to_list_view(final ArrayList<Integer> train_etas, final HashMap<String, String> current_train_info, final ArrayList<HashMap> chosen_trains){
        this.adapter.clear();
        final Context context = this.context;
        for (int current_eta : train_etas) {
//            Log.e("LIST", "To "+current_train_info.get("main_station")+": "+current_eta+" Minutes");

            current_train_info.put(current_train_info.get("train_id"), String.valueOf(current_eta));
            this.arrayList.add("To "+current_train_info.get("main_station")+": "+current_eta+" Minutes");
            this.adapter.notifyDataSetChanged();

        }

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] list_item = String.valueOf(list.getItemAtPosition(position)).split(":"); //.replaceAll("[^\\d.]", "");
                String key = list_item[1].replaceAll("[^\\d.]", "");
                for (HashMap<String, String>each_train : chosen_trains){
                    if (each_train.containsKey(key)) {
                        Intent intent = new Intent(context, activity_arrival_times.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("current_train_info", each_train);
                        intent.putExtra("next_stop", each_train.get(key));
                        context.startActivity(intent);
                    }
                    else{
                        continue;
                    }

                }
            }
        });


    }







}
