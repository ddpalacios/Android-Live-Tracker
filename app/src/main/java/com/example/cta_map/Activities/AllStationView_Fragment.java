package com.example.cta_map.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

// Instances of this class are fragments representing a single
// object in our collection.
public class AllStationView_Fragment extends Fragment {
    private Context main_context;
    Context context;
    Message message;
    GoogleMap mMap;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.frag_rv);
        message = ((MainActivity)getActivity()).message;
        context = ((MainActivity)getActivity()).context;
        mMap = ((MainActivity)getActivity()).mMap;
        Fragment fragment = ((MainActivity)getActivity()).frg;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        FloatingActionButton switch_dir = view.findViewById(R.id.switch_dir_button);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.AddStationFloatingButton);
        floatingActionButton.setOnClickListener(v -> {
            if (message.getT1()!=null) {
               new Chicago_Transits().StopThreads(message, context);

            }
            Intent intent = new Intent(context, ChooseTrainLineActivity.class);
            startActivity(intent);
        });


        Switch location_switch = view.findViewById(R.id.location_switch);
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

            }
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
        }else {
            if (location_switch.isChecked()){

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













//        location_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked){
//                if (ContextCompat.checkSelfPermission(MainActivity.context,
//                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//
//
//                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.context1, Manifest.permission.ACCESS_FINE_LOCATION)){
//
//                        ActivityCompat.requestPermissions((Activity) MainActivity.context1,
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                    }else{
//                        ActivityCompat.requestPermissions((Activity) MainActivity.context1,
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//
//                    }
//
//
//                }
//
//
//            }else{
//                userSettings.setIs_sharing_loc("0");
//                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, userSettings.getIs_sharing_loc(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
//                cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "0", "STOP_ID = '1'");
//                MainActivity.message.getT1().interrupt();
//                Log.e("API", "Location turned off!");
//                return;
//            }
//            cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "1", "STOP_ID = '1'");
//            cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, "1", CTA_DataBase.USER_SETTINGS_ID +" = '1'");
//
//            if (MainActivity.message !=null && MainActivity.message.getT1()!=null){
//                MainActivity.message.getT1().interrupt();
//            }
//            Log.e("API", "Location turned ON!");
//        });

        cta_dataBase.close();



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

        ArrayList<ListItem> arrayList = new ArrayList<>();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ArrayList<Object> all_station_list  =  cta_dataBase.excecuteQuery("*", "USER_FAVORITES", null,null,null);
        view.findViewById(R.id.list_item).setOnClickListener(v -> {
        ArrayList<Object> record1= cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+message.getTARGET_MAP_ID()+"'", null,null);

            if (record1!=null) {
                Station target_station = (Station) record.get(0);
                Chicago_Transits chicago_transits1 = new Chicago_Transits();
                chicago_transits1.ZoomIn(mMap, 12f,
                        target_station.getLat(),
                        target_station.getLon());
            }
        });


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
            ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
            API_Caller_Thread api_caller = ((MainActivity)getActivity()).api_caller;
            Content_Parser_Thread content_parser = ((MainActivity)getActivity()).content_parser;
            FusedLocationProviderClient fusedLocationClient = ((MainActivity)getActivity()).fusedLocationClient;
            HashMap<String, Object> thread_handling = new HashMap<>();
             thread_handling.put("t1", message.getT1());
            thread_handling.put("api_caller", api_caller);
            thread_handling.put("content_parser", content_parser);
            thread_handling.put("handler", handler);
            thread_handling.put("message", message);
            CardView nearestTrainCardView = (CardView) view.findViewById(R.id.list_item);



            createTrainCard(view, message.getNearestTrain());

            nearestTrainCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null,null,null);
                    Train train = null;
                    if (record == null) {
                        train = message.getNearestTrain();
                    }else{
                        HashMap<String, String> tracking_record = (HashMap<String, String>) record.get(0);
                        String tracking_id = tracking_record.get(CTA_DataBase.TRAIN_ID);
                        for (Train train1 : message.getOld_trains()){
                            if (train1.getRn().equals(tracking_id)){
                                train = train1;
                                break;
                            }
                        }
                    }
                    if (train != null) {
                        chicago_transits.ZoomIn(mMap, 13f, train.getLat(), train.getLon());
                    }



                }
            });



            nearestTrainCardView.setOnLongClickListener(v -> {
                ArrayList<Object> record2 = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null,null,null);
                Train train = null;
                if (record2 == null) {
                    train = message.getNearestTrain();
                }else{
                    HashMap<String, String> tracking_record = (HashMap<String, String>) record2.get(0);
                    String tracking_id = tracking_record.get(CTA_DataBase.TRAIN_ID);
                    for (Train train1 : message.getOld_trains()){
                        if (train1.getRn().equals(tracking_id)){
                            train = train1;
                            break;
                        }
                    }
                }
                if (train != null) {
                    if (train.getIsNotified()) { // Resets all trains + its notifications handler
                        chicago_transits.reset(message.getOld_trains(), message);
                        chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                        chicago_transits.refresh(fragment);
                        cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                        chicago_transits.stopService(context);
                        chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());


                    } else {
                        chicago_transits.reset(message.getOld_trains(), message); // Resets all trains + its notifications handler
                        nearestTrainCardView.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
                        // Setting selected train for notifications
                        train.setSelected(true);
                        train.setNotified(true);
                        chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                        chicago_transits.refresh(fragment);
                        cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
                        cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
                        chicago_transits.ZoomIn(mMap, 13f, train.getLat(), train.getLon());


                    }
                }

                message.getT1().interrupt();
                cta_dataBase.close();
                return false;

            });

            recyclerView.setAdapter(new RecyclerView_Adapter_frag2( thread_handling, main_context, arrayList, fusedLocationClient, actionBar, mMap));

        }else{
            recyclerView.setVisibility(View.GONE);
            TextView textView = new TextView(main_context);
            textView.setText("No Stations Added.");

        }

        cta_dataBase.close();
    }



    private void createTrainCard(View view, Train train) {
//        TextView tracking_description = view.findViewById(R.id.tracking_description);
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null,null,null);
        cta_dataBase.close();
        if (message.getCurrentNotificationTrain()!= null && record !=null){
            CardView nearestTrainCardView = (CardView) view.findViewById(R.id.list_item);
            nearestTrainCardView.setBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
            train = message.getCurrentNotificationTrain();

//            tracking_description.setText("Currently Tracking");
        }

        TextView main_title, isSch, train_line, train_eta, status_label;
        ImageView train_image, status_image;
        train_image = (ImageView) view.findViewById(R.id.train_image);
        main_title = (TextView) view.findViewById(R.id.title_item);
        train_line = (TextView)  view.findViewById(R.id.train_line_subtitle);
        train_eta = (TextView)  view.findViewById(R.id.title_eta);
        status_image = (ImageView)  view.findViewById(R.id.StatusImage);
        status_label = (TextView)  view.findViewById(R.id.status_label);
        isSch = (TextView) view.findViewById(R.id.isSch);

        train_line.setVisibility(View.VISIBLE);
        train_eta.setVisibility(View.VISIBLE);
        status_image.setVisibility(View.VISIBLE);
        status_label.setVisibility(View.VISIBLE);
        isSch.setVisibility(View.VISIBLE);


            if (train!=null) {
                Chicago_Transits chicago_transits = new Chicago_Transits();
                final float scale = context.getResources().getDisplayMetrics().density;
                main_title.setText("To " + train.getDestNm()); // Set its main title
                train_line.setText((train.getRt() != null ? chicago_transits.train_line_code_to_regular(train.getRt()) + " line" : "N/A"));
                train_line.setTextColor(Color.parseColor(getColor(train.getRt())));
                train_image.setImageResource(chicago_transits.getTrainImage(train.getRt()));
                isSch.setTextSize((int) (7 * scale + 0.5f));
                train_eta.setTextSize(7 * scale + 0.5f);

                if (train.getIsSch()) { // if its scheduled and delayed
                    main_title.setWidth((int) (100 * scale + 0.5f));
                    if (train.getIsDly().equals("1")) {
                        isSch.setText("Delayed");
                        train_eta.setTextColor(Color.parseColor("#FF0000"));

                    } else {
                        isSch.setText("Scheduled");
                        train_eta.setTextColor(Color.parseColor("#3367D6"));

                    }
                } else if (train.getIsDly().equals("1")) { // if its just delayed
                    main_title.setWidth((int) (100 * scale + 0.5f));
                    isSch.setText("Delayed");
                    train_eta.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    isSch.setVisibility(View.INVISIBLE); // if neither, make invisible
                }

                if (train.getIsApp().equals("1")) {
                    train_eta.setText("Due");
                } else {
                    train_eta.setText(train.getTarget_eta() + "m");
                }


                status_image.setImageResource(chicago_transits.getStatusColor(train.getStatus()));
                status_label.setText(chicago_transits.getStatusMessage(train.getStatus()));

                if (train.getStatus() == null) {
                    status_label.setText("");
                    status_image.setVisibility(View.INVISIBLE);
                } else {
                    status_image.setVisibility(View.VISIBLE);
                    status_label.setTextColor(Color.parseColor(getColor(chicago_transits.TrainLineKeys(train.getStatus()))));
                }


                if (train.getIsDly().equals("1") || train.getIsApp().equals("1")) {
                    isSch.setTextColor(Color.parseColor("#FF0000"));
                }

            }else{
                Chicago_Transits chicago_transits = new Chicago_Transits();
                main_title.setText("");
                train_line.setText("");
                train_eta.setText("");
                status_image.setImageResource(R.drawable.gray);
                train_image.setImageResource(chicago_transits.getTrainImage(message.getTarget_type()));
                status_label.setText("N/A");
            }
    }

    private String getColor(String train_line){
        HashMap<String, String> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red","#F44336");
        TrainLineKeyCodes.put("blue","#384cff");
        TrainLineKeyCodes.put("brn", "#a34700");
        TrainLineKeyCodes.put("g", "#0B8043");
        TrainLineKeyCodes.put("org", "#ffad33");
        TrainLineKeyCodes.put("y", "#b4ba0b");

        TrainLineKeyCodes.put("pink","#ff66ed");
        TrainLineKeyCodes.put("p","#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());

    }



}
