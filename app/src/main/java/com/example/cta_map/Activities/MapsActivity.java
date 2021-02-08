package com.example.cta_map.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cta_map.CustomInfoWindowAdapter;
import com.example.cta_map.DataBase.CTA_DataBase;
//import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.MapMarker;
//import com.example.cta_map.Displayers.UserLocation;
import com.example.cta_map.Displayers.NotificationBuilder;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Backend.Threading.Notifier_Thread;
//import com.example.cta_map.Backend.Threading.Train_Estimations_Thread;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

            public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
                private GoogleMap mMap;
                Message message = null;
                private Train NotificationTrain = null;
                private Thread api_caller, content_parser;
                boolean greenNotified = false;
                boolean RedNotified = false;
                boolean YellowNotified = false;
                @SuppressLint("HandlerLeak")
                private final Handler handler = new Handler() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void handleMessage(android.os.Message msg) {
                        Bundle bundle = msg.getData();
                        mMap.clear();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                displayResults(bundle);
                        }
                    }
                };


                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_maps);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    SupportMapFragment mapFragment;
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.setTitle("Live Map Viewer");
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);

                }

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    InitiateOnclickListeners(mMap);

                }

                private void   InitiateOnclickListeners(final GoogleMap mMap) {
                    message = new Message();

                    FloatingActionButton switch_dir = findViewById(R.id.map_switch_dir);
//                    Button stationLookup = findViewById(R.id.stationLookup);
                    final TextView search_result = findViewById(R.id.search_res);
                    SearchView station_search = findViewById(R.id.Station_searchView);
                    Spinner line_drop_down = (Spinner) findViewById(R.id.line_selection);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.line_names, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    line_drop_down.setAdapter(adapter);

//                    stationLookup.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mMap.clear();
//                            message.keepSending(false);
//                            if (content_parser != null) {
//                                content_parser.interrupt();
//                            }
//                            if (message.getTarget_type() != null) {
//                                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//                                ArrayList<Object> all_stations = cta_dataBase.excecuteQuery("*", "MARKERS", "marker_type = '" + message.getTarget_type() + "'", null, null);
//                                cta_dataBase.close();
//                                for (Object station : all_stations) {
//                                    HashMap<String, String> current_station = (HashMap<String, String>) station;
//                                    MapMarker marker = new MapMarker(mMap, getApplicationContext(), message);
//                                    marker.addMarker(Double.parseDouble(current_station.get("marker_lat")), Double.parseDouble(current_station.get("marker_lon")),
//                                            "Station# " + current_station.get("marker_id"),
//                                            message.getTarget_type(),
//                                            1f,
//                                            false,
//                                            false,
//                                            true,
//                                            current_station.get("marker_name"), false);
//                                }
//                            }
//                        }
//                    });



                    line_drop_down.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            message.keepSending(false);
                            if (content_parser !=null) {
                                content_parser.interrupt();
                            }
                            message.setOld_trains(null);
                            Toast.makeText(getApplicationContext(), parentView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            mMap.clear();
                            message.setTarget_type(parentView.getSelectedItem().toString().toLowerCase());
                            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                            ArrayList<Object> all_stations = cta_dataBase.excecuteQuery("*", "MARKERS",   "marker_type = '"+parentView.getSelectedItem().toString().toLowerCase()+"'",null,null);
                            cta_dataBase.close();
                            for (Object station : all_stations){
                                HashMap<String, String> current_station = (HashMap<String, String>) station;
                                MapMarker marker = new MapMarker(mMap, getApplicationContext(), message);
                                marker.addMarker(Double.parseDouble(current_station.get("marker_lat")), Double.parseDouble(current_station.get("marker_lon")),
                                        "Station# "+current_station.get("marker_id"),
                                        parentView.getSelectedItem().toString().toLowerCase(),
                                        1f,
                                        false,
                                        false,
                                        true,
                                        current_station.get("marker_name"),false);
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });



                    station_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String search_string) {
                            if (message.getTarget_type() != null && !message.IsSending()) {
                                mMap.clear();
                                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                                ArrayList<Object> target_stations = cta_dataBase.excecuteQuery("*", "MARKERS", "marker_type = '" + message.getTarget_type() + "' AND marker_name", search_string, null);
                                cta_dataBase.close();
                                if (target_stations != null) {
                                    search_result.setText("Search Result: "+target_stations.size());
                                    for (Object station : target_stations) {
                                        HashMap<String, String> current_station = (HashMap<String, String>) station;
                                        Log.e("search", current_station.get("marker_id") + "# - " + current_station.get("marker_name"));
                                        MapMarker marker = new MapMarker(mMap, getApplicationContext(), message);
                                        marker.addMarker(
                                                Double.parseDouble(current_station.get("marker_lat")),
                                                Double.parseDouble(current_station.get("marker_lon")),
                                                "Station# "+current_station.get("marker_id"),
                                                current_station.get("marker_type"),
                                                1f,
                                                false,
                                                false,
                                                true,
                                                current_station.get("marker_name"),false);
                                    }
                                }else{
                                    search_result.setText("Search Result: 0");
                                }
                            }
                            return false;
                        }

                    });

                    switch_dir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (message.getDir().equals("1")){
                                message.setDir("5");
                                Toast.makeText(getApplicationContext(), "New Dir: " + message.getDir(), Toast.LENGTH_SHORT).show();
                            }else {
                                message.setDir("1");
                                Toast.makeText(getApplicationContext(), "New Dir: " + message.getDir(), Toast.LENGTH_SHORT).show();
                            }
                            message.setDirectionChanged(true);
                            content_parser.interrupt();
                        }
                    });


                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        /*
                        Select Target Station / Set Notifications
                         */
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                            if (message.getOld_trains() != null){



                                String TRAIN_ID  =  marker.getSnippet().replaceAll("Train#", "").trim();
                                Train selected_train = trainLookup(TRAIN_ID, false);


                                if (selected_train !=null){ // If found selected train...
                                    message.setSendingNotifications(false);

                                    if (selected_train.getSelected() && selected_train.getViewIcon()){
                                        selected_train.setViewIcon(false);
                                        NotificationTrain = null;
                                        content_parser.interrupt();
                                        return;
                                    }
                                    // Reset all markers
                                    for (Train train : message.getOld_trains()){
                                        if (train.getViewIcon()){
                                            train.setViewIcon(false);
                                            train.setSelected(false);
                                        }
                                    }
                                    // Then set the selected marker
                                    selected_train.setSelected(true);
                                    selected_train.setViewIcon(true);
                                    NotificationTrain = selected_train;

                                    initiateNotifications(selected_train);
                                    content_parser.interrupt();


                                }

                            }else{
                                String MAP_ID = marker.getSnippet().replaceAll("Station# ", "");
                                Object target_stations = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + MAP_ID + "'", null, null).get(0);
                                if (target_stations != null) {
                                    HashMap<String, String> new_target_station = (HashMap<String, String>) target_stations;
                                    StartThreads(new_target_station);
                                    Toast.makeText(getApplicationContext(), new_target_station.get("STATION_NAME") + " " + message.getTarget_type(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "No Station Found.", Toast.LENGTH_SHORT).show();
                                }

                            }
                            cta_dataBase.close();

                        }
                    });


                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (message.getOld_trains() != null){
                                String TRAIN_ID  =  marker.getSnippet().replaceAll("Train#", "").trim();
                                Train selected_train = trainLookup(TRAIN_ID, false);
                                if (selected_train !=null) {
                                    if (selected_train.getSelected() && !selected_train.getViewIcon()){
                                        selected_train.setSelected(false);
                                        content_parser.interrupt();
                                        return false;
                                    }
                                    for (Train train : message.getOld_trains()){
                                        if (train.getViewIcon()){
                                            continue;
                                        }
                                        train.setSelected(false);
                                    }
                                    selected_train.setSelected(true);
                                    content_parser.interrupt();

                                }
                            }
                            return false;
                        }
                    });



                }

                private Train trainLookup(String train_id, Boolean findSelected){
                    if (!findSelected) {
                        for (Train train : message.getOld_trains()) {
                            if (train.getRn().equals(train_id)) {
                                return train;
                            }

                        }
                    }else{
                        for (Train train : message.getOld_trains()) {
                            if (train.getSelected() && !train.getViewIcon()) {
                                return train;
                            }

                        }

                    }

                    return null;
                }

                private HashMap<String, String> getTrackingStation(){
                    Bundle bundle = getIntent().getExtras();
                    try {
                        HashMap<String, String> tracking_station = (HashMap<String, String>) bundle.getSerializable("tracking_station");
                        message.setDir(tracking_station.get("station_dir"));
                        message.setTarget_name(tracking_station.get("target_station_name"));
                        message.setTarget_type(tracking_station.get("station_type"));
                        return tracking_station;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }

                private void StartThreads(HashMap<String , String> target_station){
                    message.setDir("1");
                    message.setTarget_name(target_station.get("STATION_NAME"));
                    mMap.clear();
                    api_caller =  new  Thread(new API_Caller_Thread(message));
                    content_parser = new Thread(new Content_Parser_Thread(getApplicationContext(), message, handler, target_station));
                    message.keepSending(true);
                    api_caller.start();
                    content_parser.start();

                }
                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void displayResults(Bundle bundle) {
                    mMap.clear();
                    if (NotificationTrain != null) {
                        Log.e("TRAIN", "PREVIOUS NOTIFICATION TRAIN? "+ NotificationTrain.getRn() );
                    }
                    ArrayList<Train> all_chosen_trains = (ArrayList<Train>) bundle.getSerializable("new_incoming_trains");
                    message.setOld_trains(all_chosen_trains);
                    for (Train train: all_chosen_trains){
                        Log.e("TRAIN", "RN: "+train.getRn()+". Is Selected? "+train.getSelected() +" Icon? "+ train.getViewIcon());
                    }
                    Log.e("TRAIN", "DONE");
                    if (all_chosen_trains.size() > 0) {
                        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                        ArrayList<Object> target_station_record = cta_dataBase.excecuteQuery("*",
                                                                                "CTA_STOPS",
                                                                                "MAP_ID = '" + all_chosen_trains.get(0).getTarget_id() + "'",
                                                                                null,
                                                                                null);

                        HashMap<String, String> target_station = (HashMap<String, String>) target_station_record.get(0);
                        plot_marker(null, target_station); // Plot Target Station
                        for (Train train : all_chosen_trains) {  // Find Train that has an icon
                            if (train.getViewIcon() && train.getSelected()) {
                                NotificationTrain = train;
                                break;
                            }
                        }
                        for (Train train : all_chosen_trains){
                            if (NotificationTrain !=null) {
                                if (NotificationTrain.getRn().equals(train.getRn())) {
                                    train.setSelected(true);
                                    train.setViewIcon(true);
                                    plot_marker(train, null);
                                    continue;
                                }
                            }
                            plot_marker(train, null);
                        }
                        Train selected_train = trainLookup(null, true);
                        if (selected_train!=null) {
                            plot_marker(selected_train, null);
                        }
                        cta_dataBase.close();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                private void initiateNotifications(Train selectedTrain){
                    message.setSendingNotifications(true);
                    Intent notificationIntent = new Intent(getApplicationContext() ,mainactivity.class);
                    if (selectedTrain.getStatus().equals("GREEN") && !greenNotified ) {
                        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent);
                        notificationBuilder.notificationDialog("CTA_map", "");
                        Toast.makeText(getApplicationContext(), "NOTIFIED FOR "+ selectedTrain.getRn() +" Status: " +selectedTrain.getStatus(), Toast.LENGTH_SHORT).show();

                        greenNotified = true;
                    }
                    else if (selectedTrain.getStatus().equals("YELLOW") && !YellowNotified){
                        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent );
                        notificationBuilder.notificationDialog("CTA_map", "");
                        Toast.makeText(getApplicationContext(), "NOTIFIED FOR "+ selectedTrain.getRn() +" Status: " +selectedTrain.getStatus(), Toast.LENGTH_SHORT).show();

                        YellowNotified = true;
                    }else if (selectedTrain.getStatus().equals("RED") && !RedNotified){
                        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent);
                        notificationBuilder.notificationDialog("CTA_map", "");
                        Toast.makeText(getApplicationContext(), "NOTIFIED FOR "+ selectedTrain.getRn(), Toast.LENGTH_SHORT).show();

                        RedNotified = true;

                    }

                }
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void plot_marker(Train train, HashMap<String, String > target_station){
                final MapMarker mapMarker = new MapMarker(mMap, getApplicationContext(), message);
                if (train == null){
                    mapMarker.addMarker(
                            Double.parseDouble(Objects.requireNonNull(target_station.get("LAT"))),
                            Double.parseDouble(Objects.requireNonNull(target_station.get("LON"))),
                            "Station# "+target_station.get("MAP_ID"),
                            "target",
                            1f,
                            false,
                            true,
                            false,
                            target_station.get("STATION_NAME"), false);
                        }else {
                        if (train.getSelected()) {
                            if (!train.getViewIcon()) {
                                mapMarker.addMarker(
                                        train.getLat(),
                                        train.getLon(),
                                        "Train# " + train.getRn(),
                                        train.getStatus().toLowerCase(),
                                        1f,
                                        true,
                                        false,
                                        false,
                                        " " + train.getTarget_eta() + "m",
                                        false).showInfoWindow();
                            }else{
                                mapMarker.addMarker(
                                        train.getLat(),
                                        train.getLon(),
                                        "Train# " + train.getRn(),
                                        train.getStatus().toLowerCase(),
                                        1f,
                                        true,
                                        false,
                                        false,
                                        " " + train.getTarget_eta() + "m",
                                        true).showInfoWindow();

                            }
                        }else {
                            if (!train.getViewIcon()) {
                                mapMarker.addMarker(
                                        train.getLat(),
                                        train.getLon(),
                                        "Train# " + train.getRn(),
                                        train.getTrain_type().toLowerCase(),
                                        1f,
                                        true,
                                        false,
                                        false,
                                        " " + train.getTarget_eta() + "m", false);

                            }
                        }
                    }
                    }

            }
