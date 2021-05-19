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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

public class Settings_view_Fragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList< UserSettings> current_incoming_trains;
    SettingsView_Adapter_frag mapViewAdapter;
    public  static String STATIONS_ITEM = "Stations";
    public static String MINUTES_ITEM = "Minutes";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.train_times_frag_layout, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Message message = ((MainActivity)getActivity()).message; //get message object from running MainActivity()
        Context context = ((MainActivity)getActivity()).context;
        Fragment fragment = ((MainActivity)getActivity()).frg;
        TextView main_title = view.findViewById(R.id.main_title);
        main_title.setText("Settings");
        Switch location_switch = view.findViewById(R.id.location_switch);
        CardView settings_card = view.findViewById(R.id.settings);
        settings_card.setVisibility(View.VISIBLE);
        CheckBox asMin_checkbox = view.findViewById(R.id.asMinutes);
        CheckBox asStations_checkbox = view.findViewById(R.id.asStations);
        CardView loc_settings = (CardView) view.findViewById(R.id.loc_settings);
        loc_settings.setVisibility(View.VISIBLE);
        TextView info = (TextView) view.findViewById(R.id.status_info);
        info.setVisibility(View.VISIBLE);
//        Spinner stations_or_min_spinner = view.findViewById(R.id.stations_or_min_spinner);
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null,null,null);
        UserSettings userSettings;
        if (record!= null) {
            userSettings = (UserSettings) record.get(0);
        }else{
            userSettings = new UserSettings();
        }



        if (userSettings.getAsMinutes() != null && userSettings.getAsStations() !=null){
            if (userSettings.getAsMinutes().equals("1")){
                asMin_checkbox.setChecked(true);
                asStations_checkbox.setChecked(false);
            }else{
                asMin_checkbox.setChecked(false);
                asStations_checkbox.setChecked(true);
            }

//            if (MainActivity.message != null && MainActivity.message.getT1()!=null) {
//                MainActivity.message.getT1().interrupt();
//            }
        }

        asMin_checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                userSettings.setAsMinutes("1");
                userSettings.setAsStations("0");
                asStations_checkbox.setChecked(false);


            }else{
                userSettings.setAsMinutes("0");
                userSettings.setAsStations("1");

            }

            cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_STATIONS, userSettings.getAsStations(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
            cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_MINUTES, userSettings.getAsMinutes(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");

//            if (MainActivity.message != null && MainActivity.message.getT1()!=null) {
//                MainActivity.message.getT1().interrupt();
//            }

        });

        asStations_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    userSettings.setAsStations("1");
                    userSettings.setAsMinutes("0");
                    asMin_checkbox.setChecked(false);



                }else{
                    userSettings.setAsStations("0");
                    userSettings.setAsMinutes("1");

                }

                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_STATIONS, userSettings.getAsStations(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_MINUTES, userSettings.getAsMinutes(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
            }
        });

//
//        final boolean[] clicked = {false};
//        stations_or_min_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if (!clicked[0]){
//                    clicked[0] = true;
//                    return;
//                }
//                String option  = parent.getSelectedItem().toString();
//
//                if (option.equals(STATIONS_ITEM)){
//                    userSettings.setAsStations("1");
//                    userSettings.setAsMinutes("0");
//
//
//                }else{
//                    userSettings.setAsMinutes("1");
//                    userSettings.setAsStations("0");
//
//                    ;
//
//                }
//                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_STATIONS, userSettings.getAsStations(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
//                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_MINUTES, userSettings.getAsMinutes(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
////
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        if (userSettings.getAsMinutes().equals("1")){
//            stations_or_min_spinner.setSelection(1);
//        }else{
//            stations_or_min_spinner.setSelection(0);
//
//        }


        cta_dataBase.close();


        int IsSharingLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);
        if (UserLocation != null && IsSharingLocation ==0){
            userSettings.setIs_sharing_loc("1");
            location_switch.setChecked(true);
        }else {
            userSettings.setIs_sharing_loc("0");
            location_switch.setChecked(false);

        }



        if (UserLocation != null && IsSharingLocation ==0 && userSettings.getIs_sharing_loc().equals("1")){
            location_switch.setChecked(true);
            userSettings.setIs_sharing_loc("1");
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
        }else {
            location_switch.setChecked(false);
            userSettings.setIs_sharing_loc("0");
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");
        }


        location_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CTA_DataBase cta_database = new CTA_DataBase(context);
            if (isChecked){
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){


                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.context1, Manifest.permission.ACCESS_FINE_LOCATION)){

                        ActivityCompat.requestPermissions((Activity) MainActivity.context1,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }else{
                        ActivityCompat.requestPermissions((Activity) MainActivity.context1,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    }


                }


            }else{
                userSettings.setIs_sharing_loc("0");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, userSettings.getIs_sharing_loc(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
                cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "0", "STOP_ID = '1'");
                MainActivity.message.getT1().interrupt();
                Log.e("API", "Location turned off!");
                return;
            }
            cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "1", "STOP_ID = '1'");
            cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, "1", CTA_DataBase.USER_SETTINGS_ID +" = '1'");

            if (MainActivity.message !=null && MainActivity.message.getT1()!=null){
                MainActivity.message.getT1().interrupt();
            }
            Log.e("API", "Location turned ON!");
            cta_database.close();
        });




        current_incoming_trains = new ArrayList<>();
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        UserSettings green_status = new UserSettings();
        green_status.setStatus("Green");
        UserSettings yellow_status = new  UserSettings();
        yellow_status.setStatus("Yellow");
        UserSettings red_status = new  UserSettings();
        red_status.setStatus("Red");
        UserSettings grey_status = new  UserSettings();
        grey_status.setStatus("Gray");
        current_incoming_trains.add(green_status);
        current_incoming_trains.add(yellow_status);
        current_incoming_trains.add(red_status);
        current_incoming_trains.add(grey_status);

//        Spinner min_or_stations = view.findViewById(R.id.stations_or_min_spinner);
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add(STATIONS_ITEM);
//        arrayList.add(MINUTES_ITEM);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
//                android.R.layout.simple_spinner_item, arrayList);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        min_or_stations.setAdapter(arrayAdapter);


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


        mapViewAdapter = new SettingsView_Adapter_frag(message,context, current_incoming_trains, fragment);
        recyclerView.setAdapter(mapViewAdapter);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
