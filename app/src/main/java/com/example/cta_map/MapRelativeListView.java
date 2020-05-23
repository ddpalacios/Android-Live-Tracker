package com.example.cta_map;

import android.content.Context;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapRelativeListView {
//    private  ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Context  context;
    public ArrayAdapter<String> adapter;

    public MapRelativeListView(Context context){
        this.context = context;
        this.arrayList = new ArrayList<>();
        this.adapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, arrayList);






    }
    public void add_to_list_view( ArrayList<Integer> train_etas, HashMap<String, String> train_info){
        this.adapter.clear();
        Collections.sort(train_etas);
        for (int current_eta : train_etas) {
            this.arrayList.add("To "+train_info.get("main_station")+": "+current_eta+" Minutes");
            this.adapter.notifyDataSetChanged();

        }

    }







}
