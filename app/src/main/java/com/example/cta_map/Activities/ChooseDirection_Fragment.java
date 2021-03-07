package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;


// Instances of this class are fragments representing a single
// object in our collection.
public class ChooseDirection_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private Context Maincontext;
//    public ChooseDirection_Fragment(FragmentManager context){
//        this.context = context;
//    }



    private FragmentActivity myContext;

    // Later fix b/c of deprecation?
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Maincontext = context;


        if (context instanceof Activity){
            myContext= (FragmentActivity) context;
        }

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_dir_frag3_layout, container, false);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Bundle b = this.getArguments();
        HashMap<String, String> tracking_station = (HashMap<String, String>) b.getSerializable("tracking_station");
        CTA_DataBase cta_dataBase = new CTA_DataBase(Maincontext);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        HashMap<String, String> main_stations = (HashMap<String, String>) cta_dataBase.excecuteQuery("*", "MAIN_STATIONS", "STATION_TYPE = '"+tracking_station.get("train_line")+"'", null,null).get(0);
        ArrayList<ListItem> arrayList = new ArrayList<>();

        ListItem listItem = new ListItem();
        listItem.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
        listItem.setTitle(main_stations.get("NORTHBOUND"));
        listItem.setTrain_dir("1");

        arrayList.add(listItem);

        ListItem listItem2 = new ListItem();
        listItem2.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
        listItem2.setTitle(main_stations.get("SOUTHBOUND"));
        listItem.setTrain_dir("5");
        arrayList.add(listItem2);


        FragmentManager fragManager = myContext.getSupportFragmentManager();
        recyclerView.setAdapter(new RecyclerView_Adapter_frag3(tracking_station, fragManager, arrayList));

    }






}
