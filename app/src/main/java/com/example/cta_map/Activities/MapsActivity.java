package com.example.cta_map.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.MapMarker;
import com.example.cta_map.R;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Content_Parser_Thread;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.Notifier_Thread;
import com.example.cta_map.Threading.Train_Estimations_Thread;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity  implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback{
    private GoogleMap mMap;
    Message message = new Message();




    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();



            displayResults(bundle);
        }
    };


    public void displayResults(Bundle bundle){
                final HashMap<String, Integer> colors = new HashMap<>();
        colors.put("blue", Color.BLUE);
        colors.put("red", Color.RED);
        colors.put("orange", Color.rgb(255,165,0));
        colors.put("brown", Color.rgb(165,42,42));
        colors.put("pink", Color.rgb(231, 84, 128));
        colors.put("purple", Color.rgb(128,0,128));
        colors.put("green", Color.rgb(0,255,0));
        colors.put("yellow", Color.rgb(255,255,0));
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");
        ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");
        ArrayList<HashMap> ignored_trains = (ArrayList<HashMap>) bundle.getSerializable("ignored_trains");
        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", tracking_record.get("station_type").toLowerCase());
        String main_station = bundle.getString("main_station");
        if (bundle.getBoolean("No_Trains")){ Log.e("No trains", bundle.getBoolean("No_Trains")+""); }
        else {
            if (main_station == null) { main_station = tracking_record.get("main_station_name"); }
            MapMarker mapMarker = new MapMarker(mMap);
            mMap.clear();
            Chicago_Transits chicago_transits = new Chicago_Transits();
            String[] main_station_coordinates = chicago_transits.retrieve_station_coordinates(sqlite, main_station.replace("To ", "").replaceAll("’", ""), tracking_record.get("station_type"));
            if (main_station_coordinates == null) {
                Toast.makeText(getApplicationContext(), "COULD NOT FIND MAIN STATION", Toast.LENGTH_SHORT).show();
            } else {
                Marker main_station_marker = mapMarker.addMarker(main_station_coordinates[0],
                        main_station_coordinates[1],
                        main_station, "cyan", 1f);
            }
            PolylineOptions options = new PolylineOptions().width(15).color(colors.get(tracking_record.get("station_type")));
                                for (String each_stop : stops) {
                                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, each_stop, tracking_record.get("station_type"));
                                        double station_lat = Double.parseDouble(station_coord[0]);
                                        double station_lon = Double.parseDouble(station_coord[1]);
                                        LatLng lt = new LatLng(station_lat, station_lon);
                                        options.add(lt); }
            mMap.addPolyline(options);
            Marker target_station_marker = mapMarker.addMarker(tracking_record.get("station_lat"), tracking_record.get("station_lon"), tracking_record.get("station_name"), "default", 1f);
            target_station_marker.showInfoWindow();

            for (HashMap<String, String> train : ignored_trains) {
                String train_lat = train.get("train_lat");
                String train_lon = train.get("train_lon");
                mapMarker.addMarker(train_lat, train_lon, train.get("next_stop"), train.get("station_type"), .5f);
            }

            for (HashMap<String, String> train : chosen_trains) {
                String train_lat = train.get("train_lat");
                String train_lon = train.get("train_lon");
                mapMarker.addMarker(train_lat, train_lon, train.get("next_stop"), train.get("station_type"), 1f);

            }
        }

        sqlite.close();
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        assert mapFragment != null;

        mapFragment.getMapAsync(this);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapMarker mapMarker = new MapMarker(mMap);
        mMap.setMyLocationEnabled(true); // Enable user location permission
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        message.keepSending(true);
        message.setClicked(false);

        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");
        message.setTargetContent(tracking_record);
        String[] target_coordinates = new String[]{tracking_record.get("station_lat"),tracking_record.get("station_lon") };
        chicago_transits.ZoomIn(mMap, (float) 13.3, target_coordinates);
        if (tracking_record == null || tracking_record.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("record", tracking_record+"");
        message.setClicked(false);
        message.keepSending(true);
        message.setTargetContent(tracking_record);
        final Button switch_direction = (Button) findViewById(R.id.switch_direction);

        final Button choose_station = (Button) findViewById(R.id.pickStation);
        final Button toArrival = (Button) findViewById(R.id.show);




        Thread api_call = new Thread(new API_Caller_Thread(message, tracking_record, handler, false), "api caller");
        api_call.start();

        Thread content_parser = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "parser");
        content_parser.start();


        Thread train_estimations = new Thread(new Train_Estimations_Thread(message, false), "estimations");
        train_estimations.start();

        final Thread notifier = new Thread(new Notifier_Thread(message, handler, getApplicationContext(), false), "notifier");
        notifier.start();



        toArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifier.interrupt();
                Intent intent = new Intent(MapsActivity.this, TrainTrackingActivity.class);


                synchronized (message){
                    message.keepSending(false);
                }


                startActivity(intent);
            }
        });



        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifier.interrupt();
                Intent intent = new Intent(MapsActivity.this, mainactivity.class);
                Integer profile_id = Integer.parseInt(tracking_record.get("profile_id"));
                final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+profile_id+"'");
                intent.putExtra("profile_id", user_record.get(0));
                synchronized (message){
                    message.keepSending(false);
                }

                startActivity(intent);


            }
        });


        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target_station_direction;
                String main_station;
                if (message.getDir() ==null) {
                    target_station_direction = tracking_record.get("station_dir");
                    main_station = tracking_record.get("main_station_name");


                }else{
                    target_station_direction = message.getDir();
                    main_station = message.getMainStation();

                }

                notifier.interrupt();
                if (target_station_direction.equals("1")){
                    target_station_direction = "5";
                    main_station = sqlite.get_table_record("main_stations_table",
                            "WHERE train_line = '"+
                                    tracking_record.get("station_type") + "'").get(3);


//                    Log.e("NEW MAIN STATION", main_station);
                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setMainStation(main_station);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }else {
                    target_station_direction = "1";
                    main_station = sqlite.get_table_record("main_stations_table",
                            "WHERE train_line = '"+
                                    tracking_record.get("station_type") + "'").get(2);
//                    Log.e("NEW MAIN STATION", main_station);

                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setMainStation(main_station);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }

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

