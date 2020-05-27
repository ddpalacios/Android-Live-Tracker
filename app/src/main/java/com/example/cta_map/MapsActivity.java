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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
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
    Bundle bb; // Retrieve data from main screen

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
        Button hide = (Button) findViewById(R.id.show);
        Button choose_station = (Button) findViewById(R.id.pickStation);


        mMap = googleMap;
        mMap.setMyLocationEnabled(true); // Enable user location permission
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        final MapMarker mapMarker = new MapMarker(mMap);
        final Chicago_Transits chicago_transits = new Chicago_Transits();
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
        bb=getIntent().getExtras();
        assert bb != null;
        final String target_station_type = bb.getString("target_station_type");
        final String target_station_name = bb.getString("target_station_name");
        final String[] specified_train_direction = {bb.getString("train_direction")};
        BufferedReader train_station_csv_reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
        final String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(train_station_csv_reader, target_station_name, target_station_type);
        final ArrayList<String> stops = chicago_transits.retrieve_line_stations(chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops), target_station_type);
        chicago_transits.ZoomIn(mMap, (float) 13.3, target_station_coordinates);
        Log.e("stops", stops+"");

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect[0]= false;
                Intent intent = new Intent(MapsActivity.this,TrainTrackingActivity.class);
                intent.putExtra("target_station_type", target_station_type);
                intent.putExtra("target_station_name", target_station_name);
                intent.putExtra("train_direction", specified_train_direction[0]);
                startActivity(intent);
            }
        });

        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect[0] = false;
                Intent intent = new Intent(MapsActivity.this,mainactivity.class);
                intent.putExtra("target_station_type", target_station_type);
                intent.putExtra("target_station_name", target_station_name);
                intent.putExtra("train_direction", specified_train_direction[0]);
                startActivity(intent);

            }
        });

        final HashMap<String, Integer> colors = new HashMap<>();
        colors.put("blue", Color.BLUE);
        colors.put("red", Color.RED);
        colors.put("orange", Color.rgb(255,165,0));
        colors.put("brown", Color.rgb(165,42,42));
        colors.put("pink", Color.rgb(231, 84, 128));
        colors.put("purple", Color.rgb(128,0,128));
        colors.put("green", Color.rgb(0,255,0));
        colors.put("yellow", Color.rgb(255,255,0));


        assert target_station_type != null;
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(target_station_type.toLowerCase()));
        Log.e("url", url);
        /*

          Everything is being ran within its own thread.
         This allows us to run our continuous web extraction
         while also performing other user interactions

          */
        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
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
                                PolylineOptions options = new PolylineOptions().width(15).color(colors.get(target_station_type));
                                for (String each_stop : stops) {
                                    BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_stations);
                                    String[] station_coord = chicago_transits.retrieve_station_coordinates(reader, each_stop, target_station_type);
                                        double station_lat = Double.parseDouble(station_coord[0]);
                                        double station_lon = Double.parseDouble(station_coord[1]);
                                        LatLng lt = new LatLng(station_lat, station_lon);
                                        options.add(lt);

                                }
                                mMap.addPolyline(options);

                                mapMarker.addMarker(target_station_coordinates[0], target_station_coordinates[1], target_station_name, "default", 1f).showInfoWindow();
                                for (String each_train : train_list) {
                                    // prepare each train as a map
                                    HashMap<String, String> train_info = chicago_transits.get_train_info(chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations), each_train,target_station_name ,target_station_type);
                                   int start = 0;
                                   int end =0;
                                    if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
                                        train_info.put("target_station_lat", target_station_coordinates[0]);
                                        train_info.put("target_station_lon", target_station_coordinates[1]);
                                        mapMarker.addMarker(train_info.get("main_lat"), train_info.get("main_lon"),train_info.get("main_station"), "cyan", 1f);
                                        if (specified_train_direction[0].equals("1")){
                                            end = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", ""));

                                        }else if (specified_train_direction[0].equals("5")){
                                            start = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", "")) + 1;
                                            end = stops.size();

                                        }
                                        setup_train_direction(train_info, stops, start, end, Integer.parseInt(specified_train_direction[0]), getApplicationContext());
                                    }
                                }
                                Log.d("Update", "DONE.");
                            }

                        });
                        train_etas.clear();
                        chosen_trains.clear();
                        Thread.sleep(10000);

                    } catch (IOException | InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private Button initiate_button(int widget) {
        Button button = findViewById(widget);
        button.setBackgroundColor(Color.rgb(133, 205, 186));
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
        BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
        String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(reader, current_train_info.get("target_station"), current_train_info.get("station_type"));
        Time times = new Time();

        ignored_stations = stops.subList(start, end);
        String next_stop = Objects.requireNonNull(current_train_info.get("next_stop")).replaceAll("[^a-zA-Z0-9]", "");

        if (ignored_stations.contains(next_stop)) {
                 mapMarker.addMarker(current_train_info.get("train_lat"), current_train_info.get("train_lon"), current_train_info.get("next_stop"), current_train_info.get("station_type"), .5f);

            }else {
            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lon"))));
                int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                train_etas.add(current_train_eta);
                Collections.sort(train_etas);
                chosen_trains.add(current_train_info);
                current_train_info.put(String.valueOf(current_train_eta), next_stop);
                userLocation.getLastLocation(mMap, target_station_coordinates, current_train_eta, current_train_info, current_train_info.get("station_type"));
        }
    }
}