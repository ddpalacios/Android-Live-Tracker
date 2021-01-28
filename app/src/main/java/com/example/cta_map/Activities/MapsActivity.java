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
import android.widget.Button;
import android.widget.ImageView;
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
                private Thread api_caller, content_parser;
                final Train[] main_selection = new Train[1];
                boolean greenNotified = false;
                boolean RedNotified = false;
                boolean YellowNotified = false;
                String SELECTED_TRAIN = null;
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
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void displayResults(Bundle bundle) {
                    final ArrayList<Object> all_chosen_trains = (ArrayList<Object>) bundle.getSerializable("new_incoming_trains");
                    assert all_chosen_trains != null;
                    Object main_record = all_chosen_trains.get(0);
                    final Train main_train  = (Train) main_record;
                    String target_id = main_train.getTarget_id();
                    CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                    ArrayList<Object> target_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS","MAP_ID = '"+target_id+"'", null);
                    HashMap<String, String> target_station = (HashMap<String, String>) target_record.get(0);
                    final MapMarker mapMarker = new MapMarker(mMap, getApplicationContext());
                    mapMarker.addMarker(null,Double.parseDouble(target_station.get("LAT")),
                                        Double.parseDouble(target_station.get("LON")),
                                        target_station.get("STATION_NAME"), target_station.get("STATION_NAME"), "target", 1f, false, "Target").showInfoWindow();

                    Log.e("Notification", "FOUND TRAIN (NOTIFY?): "+ hasSelectedTrain(main_selection, all_chosen_trains));
                    Boolean haveSelectedTrain = hasSelectedTrain(main_selection, all_chosen_trains);
                    for (Object record : all_chosen_trains) {
                        Train incoming_train = (Train) record;
                        if (haveSelectedTrain) {
                            Train selectedTrain = main_selection[0];
                            initiateNotifications(selectedTrain);
                            MapMarker main_marker = new MapMarker(mMap, getApplicationContext());
                            main_marker.addMarker(selectedTrain,
                                    selectedTrain.getLat(),
                                    selectedTrain.getLon(),
                                    selectedTrain.getNextStaNm(),
                                    selectedTrain.getTarget_eta()+"m", selectedTrain.getStatus().toLowerCase(),
                                    1f,
                                    false,
                                    "Train#"+selectedTrain.getRn()+"\nTo "+target_station.get("STATION_NAME")).showInfoWindow();
                            if (!selectedTrain.getRn().equals(incoming_train.getRn())){
                                createMarker(incoming_train, false, target_station);
                            }
                        }else {
                            if (SELECTED_TRAIN != null && SELECTED_TRAIN.equals(incoming_train.getRn())) {
                                MapMarker train_marker = new MapMarker(mMap, getApplicationContext());
                                BigDecimal bd = BigDecimal.valueOf(incoming_train.getNext_stop_distance()).setScale(2, RoundingMode.HALF_UP);
                                train_marker.addMarker(incoming_train,
                                        incoming_train.getLat(),
                                        incoming_train.getLon(),
                                        incoming_train.getNextStaNm(),
                                        bd.toString() + "mi",
                                        incoming_train.getTrain_type(),
                                        1f,
                                        true,
                                        "Train #" + incoming_train.getRn() + "\nNxt. Stop\n" + incoming_train.getNextStaNm()).showInfoWindow();

                            }
                            createMarker(incoming_train, false, target_station);

                        }

                    }

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (marker.getTitle().equals("Target")){
                                return false;
                            }

                            try {
                                String selected_train_rn  = marker.getTitle().substring(marker.getTitle().indexOf("#")+1, marker.getTitle().indexOf("Nxt.")).trim();
                                Log.e("marker", selected_train_rn);
                                SELECTED_TRAIN = selected_train_rn;
                                Log.e("Notification", "Selected: "+ SELECTED_TRAIN);



                            }catch (Exception e ){
                                String selected_train_rn  = marker.getTitle().substring(marker.getTitle().indexOf("#")+1, marker.getTitle().indexOf("To")).trim();
                                Log.e("marker", selected_train_rn);
                                SELECTED_TRAIN = selected_train_rn;
                                Log.e("Notification", "Selected: "+ SELECTED_TRAIN);


                            }
                            return false;
                        }
                    });


                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Train selected_train;
                            greenNotified = false;
                            RedNotified = false;
                            YellowNotified = false;
                            try{
                                String selected_train_rn  = marker.getTitle().substring(marker.getTitle().indexOf("#")+1, marker.getTitle().indexOf("To")).trim();
                                if (main_selection[0] !=null) {
                                    if (main_selection[0].getRn().equals(selected_train_rn.trim())) {
                                        Log.e("Notification", "OFF");
                                        Arrays.fill(main_selection, null);
                                        SELECTED_TRAIN = selected_train_rn;
                                        content_parser.interrupt();
                                        return;

                                    }
                                }}catch (Exception e){
                                Log.e("Notification", "ON");
                            }
                            for (Object record: all_chosen_trains) {
                                Train train = (Train) record;
                                if (train.getRn().equals(marker.getTitle().replaceAll("[^0-9]", ""))) {
                                    selected_train = train;
                                    Arrays.fill(main_selection, null);
                                    main_selection[0] = selected_train;
                                    Log.e("Notification", "ON FOR: "+ main_selection[0].getRn());

                                    break;
                                }
                            }
                            try {
                                content_parser.interrupt();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });

                }


                private void createMarker(Train incoming_train, Boolean isSelectedTrain, HashMap<String, String> target_station){
                    if (!isSelectedTrain) {
                        MapMarker train_marker = new MapMarker(mMap, getApplicationContext());
                        BigDecimal bd = BigDecimal.valueOf(incoming_train.getNext_stop_distance()).setScale(2, RoundingMode.HALF_UP);
                        train_marker.addMarker(incoming_train,
                                incoming_train.getLat(),
                                incoming_train.getLon(),
                                incoming_train.getNextStaNm(),
                                bd.toString() + "mi",
                                incoming_train.getTrain_type(),
                                1f,
                                true,
                                "Train #" + incoming_train.getRn() + "\nNxt. Stop\n" + incoming_train.getNextStaNm());
                    }else{
                        MapMarker main_marker = new MapMarker(mMap, getApplicationContext());
                        main_marker.addMarker(incoming_train,
                                incoming_train.getLat(),
                                incoming_train.getLon(),
                                incoming_train.getNextStaNm(),
                                incoming_train.getTarget_eta()+"m", incoming_train.getStatus().toLowerCase(),
                                1f,
                                false,
                                "Train#"+incoming_train.getRn()+"\nTo "+target_station.get("STATION_NAME")).showInfoWindow();

                    }


                }


                @RequiresApi(api = Build.VERSION_CODES.O)
                private void initiateNotifications(Train selectedTrain){
                    Intent notificationIntent = new Intent(getApplicationContext() ,mainactivity.class);
                    if (selectedTrain.getStatus().equals("GREEN") && !greenNotified ) {
                        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent);
                        notificationBuilder.notificationDialog("CTA_map", "");
                        greenNotified = true;
                    }
                    else if (selectedTrain.getStatus().equals("YELLOW") && !YellowNotified){
                        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent );
                        notificationBuilder.notificationDialog("CTA_map", "");
                        YellowNotified = true;
                    }else if (selectedTrain.getStatus().equals("RED") && !RedNotified){
                        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent);
                        notificationBuilder.notificationDialog("CTA_map", "");
                        RedNotified = true;

                    }

                }



                private Boolean hasSelectedTrain( Train[] main_selection, ArrayList<Object> all_chosen_trains){
                    if (main_selection[0] !=null) {
                        for (Object record : all_chosen_trains) {
                            Train train = (Train) record;
                            if (main_selection[0].getRn().equals(train.getRn())) {
                                return true;
                            }
                        }
                    }
                    return false;
                }




                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_maps);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert mapFragment != null;
                    ActionBar actionBar = getSupportActionBar();
                    assert actionBar != null;
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeButtonEnabled(true);
                    actionBar.setTitle("Live Map Viewer");
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(this);

                }
                @SuppressLint("ShowToast")
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    final Message message = new Message();
                    ImageView mapImage = findViewById(R.id.mapImage);
                    Bundle bundle = getIntent().getExtras();
                    FloatingActionButton switch_dir = findViewById(R.id.map_switch_dir);
                    Chicago_Transits chicago_transits = new Chicago_Transits();
                    HashMap<String, String> tracking_station = (HashMap<String, String>) bundle.getSerializable("tracking_station");
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, Double.parseDouble(tracking_station.get("LAT")), Double.parseDouble(tracking_station.get("LON")));

                    message.setDir(tracking_station.get("station_dir"));
                    message.setSwitchDir(false);
                    message.setTarget_name(tracking_station.get("target_station_name"));
                    message.setTarget_type(tracking_station.get("station_type"));
                    Button test_threads = findViewById(R.id.run_map_threads);
                    Toast.makeText(getApplicationContext(), "Current Direction: "+ message.getDir(), Toast.LENGTH_SHORT).show();


                    final HashMap<String, String> finalTracking_station = tracking_station;
                    test_threads.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           api_caller =  new  Thread(new API_Caller_Thread(message, finalTracking_station.get("station_type")));
                            content_parser = new Thread(new Content_Parser_Thread(getApplicationContext(), message, handler));
                            api_caller.start();
                            content_parser.start();
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
                            content_parser.interrupt();
                        }
                    });
                }

                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }

                @Override
                public void onMyLocationClick(@NonNull Location location) {

                }
            }