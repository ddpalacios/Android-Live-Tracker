package com.example.cta_map.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MapView_Fragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Train> current_incoming_trains;
    MapView_Adapter_frag mapViewAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.train_times_frag_layout, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         /*
        MapView Status tab.
         */
        // We can grab these variables staticly because we can assume that the user is within the UI, meaning MainActivty static variables would not be null
        Message message = ((MainActivity)getActivity()).message; //get message object from running MainActivity()
        Context context = ((MainActivity)getActivity()).context;
        Fragment fragment = ((MainActivity)getActivity()).frg;

        current_incoming_trains = message.getOld_trains(); // Retrieves our most up-to-date trains
        Switch location_switch = (Switch) view.findViewById(R.id.location_switch);
        location_switch.setVisibility(View.VISIBLE);
        TextView main_title = view.findViewById(R.id.main_title);
        main_title.setText("Status");
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        CTA_DataBase cta_dataBase = new CTA_DataBase(MainActivity.context);
        int IsSharingLocation = ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.ACCESS_FINE_LOCATION);
        ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null,null,null);
        UserSettings userSettings;



        // Creating/Loading our user settings
        if (record!= null) {
            userSettings = (UserSettings) record.get(0);
        }else{
            userSettings = new UserSettings();



        }





        if (UserLocation != null && IsSharingLocation ==0){
            userSettings.setIs_sharing_loc("1");
            if (!location_switch.isChecked()){
                location_switch.setChecked(true);

            }
        }else {
            userSettings.setIs_sharing_loc("0");
            if (location_switch.isChecked()){
                location_switch.setChecked(false);

            }


        }
        if (UserLocation != null && IsSharingLocation ==0 && userSettings.getIs_sharing_loc().equals("1")){
            if (!location_switch.isChecked()){
                location_switch.setChecked(true);

            }
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
        }else {
            if (location_switch.isChecked()){
                location_switch.setChecked(false);

            }
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");
        }




        location_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && location_switch.isPressed()){

                if (ContextCompat.checkSelfPermission(MainActivity.context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.context1, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        ActivityCompat.requestPermissions((Activity) MainActivity.context1,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        ActivityCompat.requestPermissions((Activity) MainActivity.context1,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    }
                }

                // without location pop-up permission
                else if (userSettings.getIs_sharing_loc().equals("0")) {
                    userSettings.setIs_sharing_loc("1");
                    cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "1", "STOP_ID = '1'");
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, userSettings.getIs_sharing_loc(), CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    MainActivity.message.getT1().interrupt(); // Reset train status
                    Log.e("API", "RESET!");
                }

//                }
            }else {
                // if gets turned off.
                if (location_switch.isPressed()) {
                    if (userSettings.getIs_sharing_loc().equals("1")) {

                        userSettings.setIs_sharing_loc("0");
                        cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, userSettings.getIs_sharing_loc(), CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                        cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "0", "STOP_ID = '1'");

                        MainActivity.message.getT1().interrupt(); // Reset train status
                        Log.e("API", "Location turned off!");
                    }
                }
            }
        });


        cta_dataBase.close();



        FloatingActionButton switch_dir = view.findViewById(R.id.switch_dir_button);
        switch_dir.setOnClickListener(v -> {
            MainActivity.bar.setTitle("Switching Directions...");
            message.getT1().interrupt();
            String dir = message.getDir();
            message.setGreenNotified(false);
            message.setYellowNotified(false);
            message.setRedNotified(false);
            message.setApproachingNotified(false);
            if (dir !=null) {
                if (dir.equals("1")) {
                    message.setDir("5");

                } else {
                    message.setDir("1");
                }
            }


        });

        GoogleMap mMap = ((MainActivity)getActivity()).mMap;
        if (current_incoming_trains!=null) {
            mapViewAdapter = new MapView_Adapter_frag(message,context, mMap, current_incoming_trains, fragment);
            recyclerView.setAdapter(mapViewAdapter);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
