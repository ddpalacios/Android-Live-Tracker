package com.example.cta_map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

    public void add_to_list_view( ArrayList<Integer> train_etas, HashMap<String, String> train_info){
        this.adapter.clear();
        Collections.sort(train_etas);
        for (int current_eta : train_etas) {
            train_info.put(train_info.get("train_id"), String.valueOf(current_eta));
            this.arrayList.add("To "+train_info.get("main_station")+": "+current_eta+" Minutes");
            this.adapter.notifyDataSetChanged();

        }

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Clicked", position+" "+id);
            }
        });


    }







}
