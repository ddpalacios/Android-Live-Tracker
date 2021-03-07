package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
public class AllStationView_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private FragmentActivity myContext;
    private Context main_context;
    private FrameLayout frameLayout;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity){
            myContext= (FragmentActivity) context;
            main_context = context;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.find_station_frag2_layout, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        frameLayout = view.findViewById(R.id.user_frag2);
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        ArrayList<ListItem> arrayList = new ArrayList<>();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(main_context);
        ArrayList<Object> all_station_list  =  cta_dataBase.excecuteQuery("*", "USER_FAVORITES", null,null,null);
        if (all_station_list !=null){
            recyclerView.setVisibility(View.VISIBLE);
            for (int i = 0; i < all_station_list.size(); i++) {
                ListItem listItem = new ListItem();
                HashMap<String, String> station = (HashMap<String, String>) all_station_list.get(i);
                listItem.setDirection_id(station.get("STATION_DIR_LABEL"));
                listItem.setTitle(station.get("STATION_NAME"));
                listItem.setImage(chicago_transits.getTrainImage(station.get("STATION_TYPE")));
                arrayList.add(listItem);

        }
            FragmentManager fragManager = myContext.getSupportFragmentManager();
            recyclerView.setAdapter(new RecyclerView_Adapter_frag2(fragManager, arrayList));

        }else{
            recyclerView.setVisibility(View.GONE);
            TextView textView = new TextView(main_context);
            textView.setText("No Stations Added.");
            frameLayout.addView(textView);

        }
    }




    public Integer getTrainImage(HashMap<String, String> station){
        if (station.get("PINK").equals("1")){
            return R.drawable.pink;

        }else if (station.get("BRN").equals("1")){
            return R.drawable.brown;

        } else if (station.get("BLUE").equals("1")){
            return R.drawable.blue;

        } else if (station.get("ORG").equals("1")){
            return R.drawable.orange;

        } else if (station.get("P").equals("1")){
            return R.drawable.purple;

        } else if (station.get("RED").equals("1")){
            return R.drawable.red;

        } else if (station.get("Y").equals("1")){
            return R.drawable.yellow;

        } else if (station.get("G").equals("1")){
            return R.drawable.green;

        }else{
            return R.drawable.red;

        }
    }




}
