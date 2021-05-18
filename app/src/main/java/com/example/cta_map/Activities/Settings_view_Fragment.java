package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
        CardView settings_card = view.findViewById(R.id.settings);
        settings_card.setVisibility(View.VISIBLE);
        CardView loc_settings = (CardView) view.findViewById(R.id.loc_settings);
        loc_settings.setVisibility(View.VISIBLE);
        TextView info = (TextView) view.findViewById(R.id.status_info);
        info.setVisibility(View.VISIBLE);


        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null,null,null);
        UserSettings userSettings;
        if (record!= null) {
            userSettings = (UserSettings) record.get(0);
        }else{
            userSettings = new UserSettings();
        }
        cta_dataBase.close();
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
        current_incoming_trains.add(green_status);
        current_incoming_trains.add(yellow_status);
        current_incoming_trains.add(red_status);


        Spinner min_or_stations = view.findViewById(R.id.stations_or_min_spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(STATIONS_ITEM);
        arrayList.add(MINUTES_ITEM);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        min_or_stations.setAdapter(arrayAdapter);


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
