package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

// Instances of this class are fragments representing a single
// object in our collection.
public class AllStationView_Fragment extends Fragment {
    private Context main_context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            main_context = context;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_stations_layout, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        ArrayList<ListItem> arrayList = new ArrayList<>();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(main_context);
        ArrayList<Object> all_station_list  =  cta_dataBase.excecuteQuery("*", "USER_FAVORITES", null,null,null);
        cta_dataBase.close();
        if (all_station_list !=null){
            recyclerView.setVisibility(View.VISIBLE);
            for (int i = 0; i < all_station_list.size(); i++) {
                ListItem listItem = new ListItem();
                HashMap<String, String> station = (HashMap<String, String>) all_station_list.get(i);
                listItem.setDirection_id(station.get("STATION_DIR_LABEL"));
                listItem.setMapID(station.get("FAVORITE_MAP_ID"));
                listItem.setTrain_dir_label(station.get("STATION_DIR_LABEL"));
                listItem.setTitle(station.get("STATION_NAME"));
                listItem.setTrain_dir(station.get("STATION_DIR"));
                listItem.setImage(chicago_transits.getTrainImage(station.get("STATION_TYPE")));
                listItem.setTrainLine(station.get("STATION_TYPE"));
                arrayList.add(listItem);
            }

             Handler handler = ((MainActivity)getActivity()).handler;
             Message message = ((MainActivity)getActivity()).message;
            ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
            API_Caller_Thread api_caller = ((MainActivity)getActivity()).api_caller;
            Content_Parser_Thread content_parser = ((MainActivity)getActivity()).content_parser;
            FusedLocationProviderClient fusedLocationClient = ((MainActivity)getActivity()).fusedLocationClient;
            GoogleMap mMap = ((MainActivity)getActivity()).mMap;
            HashMap<String, Object> thread_handling = new HashMap<>();
             thread_handling.put("t1", message.getT1());
            thread_handling.put("api_caller", api_caller);
            thread_handling.put("content_parser", content_parser);
            thread_handling.put("handler", handler);
            thread_handling.put("message", message);

            recyclerView.setAdapter(new RecyclerView_Adapter_frag2( thread_handling, main_context, arrayList, fusedLocationClient, actionBar, mMap));

        }else{
            recyclerView.setVisibility(View.GONE);
            TextView textView = new TextView(main_context);
            textView.setText("No Stations Added.");

        }
    }

}
