package com.example.cta_map;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity  implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, TrainDirection{
    final boolean[] connect = {true};
    private GoogleMap mMap;
    List<String> ignored_stations;
    ArrayList<Integer> train_etas = new ArrayList<>();
    ArrayList<HashMap> chosen_trains = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        final RelativeLayout train_eta_list = findViewById(R.id.background);
        final Button disconnect = initiate_button(R.id.disconnect, 133, 205,186);
        final Button hide = initiate_button(R.id.show, 133, 205,186);
        final Button switch_direction = initiate_button(R.id.switch_direction, 133, 205,186);
        final Button choose_station = initiate_button(R.id.pickStation, 133, 205,186);
        hide.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (train_eta_list.getVisibility() == View.VISIBLE) {
                    hide.setText("SHOW");
                    train_eta_list.setVisibility(View.GONE);
                    switch_direction.setVisibility(View.GONE);
                    choose_station.setVisibility(View.GONE);

                } else if (train_eta_list.getVisibility() != View.VISIBLE) {
                    hide.setText("HIDE");
                    train_eta_list.setVisibility(View.VISIBLE);
                    switch_direction.setVisibility(View.VISIBLE);
                    choose_station.setVisibility(View.VISIBLE);
                }
            }
        });


        choose_station.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, mainactivity.class);
                connect[0] = false;
                Log.d("Connection Status", "Connection Closed");
                startActivity(intent);
            }
        });


        disconnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (connect[0]) {
                    disconnect.setText("Connect");
                    connect[0] = false;
                    Log.d("Connection Status", "Connection Closed");
                    Toast.makeText(context, "DISCONNECTED", Toast.LENGTH_SHORT).show();

                }else {
                    disconnect.setText("Disconnect");
                    connect[0] = true;
                    Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show();
                    Log.d("Connection Status", "Connection Opened");
                    onMapReady(mMap);

                }

            }
        });

        mapFragment.getMapAsync(this);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true); // Enable user location permission
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        final MapMarker mapMarker = new MapMarker(mMap);
        final Context context = getApplicationContext();
        final Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader train_station_coordinates_reader = chicago_transits.setup_file_reader(context,R.raw.train_stations);
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        final String target_station_type = bb.getString("target_station_type");
        final String target_station_name = bb.getString("target_station_name");
        final String[] specified_train_direction = {bb.getString("train_direction")};
        final String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(train_station_coordinates_reader, target_station_name, target_station_type);
        final Button switch_direction = initiate_button(R.id.switch_direction, 133, 205,186);
        BufferedReader train_station_stops_reader = chicago_transits.setup_file_reader(context, R.raw.train_line_stops);
        final ArrayList<String> stops = chicago_transits.retrieve_line_stations(train_station_stops_reader, target_station_type);
        chicago_transits.ZoomIn(mMap, (float) 13.3, target_station_coordinates);
        Log.e("stops", stops+"");
        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread.currentThread().interrupt();
                Toast.makeText(context, "Switching Directions. Please Wait...", Toast.LENGTH_SHORT).show();

                if (specified_train_direction[0].equals("1")){
                    specified_train_direction[0] = "5";

                }else {
                    specified_train_direction[0] = "1";
                }
            }
        });
        assert target_station_type != null;
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(target_station_type.toLowerCase()));
        Log.e("url", url);
        /*

          Everything is being ran within its own thread.
         This allows us to run our continuous web extraction
         while also performing other user interactions

          */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (connect[0]){
                    try {
                        Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                        final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale", "WrongConstant", "ShowToast", "NewApi"})
                            @Override
                            public void run() {

                                mMap.clear();
                                mapMarker.addMarker(target_station_coordinates[0], target_station_coordinates[1], target_station_name, "default", 1f).showInfoWindow();
                                for (String each_train : train_list) {
                                    BufferedReader train_station_coordinates = chicago_transits.setup_file_reader(context,R.raw.train_stations);
                                    HashMap<String, String> train_info = chicago_transits.get_train_info(train_station_coordinates, each_train, target_station_type); // Feed in given and prepare it as a hashmap with necessary train data
                                    train_info.put("target_station", target_station_name);
                                    train_info.put("target_station_lat", target_station_coordinates[0]);
                                    train_info.put("target_station_lon", target_station_coordinates[1]);


                                    if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
                                        int start = 0;
                                        int end = 0;
                                        mapMarker.addMarker(train_info.get("main_lat"), train_info.get("main_lon"),"Next Stop: "+ train_info.get("main_station"), "cyan", 1f);
                                        if (specified_train_direction[0].equals("1")){
                                            end = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", ""));

                                        }else if (specified_train_direction[0].equals("5")){
                                            start = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", "")) + 1;
                                            end = stops.size();

                                        }
                                        setup_train_direction(train_info, stops, start, end, Integer.parseInt(specified_train_direction[0]), context);
                                    }
                                }

                                Log.d("Update", "DONE.");
                            }

                        });
                        train_etas.clear();
                        chosen_trains.clear();
                        Thread.sleep(2000);


                    } catch (IOException | InterruptedException e) {
                        Toast.makeText(context, "Invalid URL", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private Button initiate_button(int widget, int r, int g, int b) {
        Button button = findViewById(widget);
        button.setBackgroundColor(Color.rgb(r, g,b));
        return button;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        Context context = getApplicationContext();
        final UserLocation userLocation = new UserLocation(context);
        if (userLocation.checkPermissions()) {
            userLocation.getLastLocation(mMap, null, null, null, null);
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
    @Override
    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context) {
        MapMarker mapMarker = new MapMarker(mMap);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        UserLocation userLocation = new UserLocation(context);
        MapRelativeListView mapRelativeListView = new MapRelativeListView(context,findViewById(R.id.list));
        String[] target_station_coordinates = (current_train_info.get("train_lat") +","+current_train_info.get("train_lon")).split(",");
        Time times = new Time();

        ignored_stations = stops.subList(start, end);
        String next_stop = Objects.requireNonNull(current_train_info.get("next_stop")).replaceAll("[^a-zA-Z0-9]", "");

        if (ignored_stations.contains(next_stop)) {
                Marker train_marker = mapMarker.addMarker(current_train_info.get("train_lat"), current_train_info.get("train_lon"), current_train_info.get("next_stop"), current_train_info.get("station_type"), .5f);

            }else {
            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))), Double.parseDouble(current_train_info.get("target_station_lat")), Double.parseDouble(current_train_info.get("target_station_lon")));
                int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                train_etas.add(current_train_eta);
                Collections.sort(train_etas);
                chosen_trains.add(current_train_info);
                current_train_info.put(String.valueOf(current_train_eta), next_stop);
                userLocation.getLastLocation(mMap, target_station_coordinates, current_train_eta, current_train_info, current_train_info.get("station_type"));

        }
        mapRelativeListView.add_to_list_view(train_etas, current_train_info, chosen_trains);
    }
}