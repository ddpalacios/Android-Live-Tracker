package com.example.cta_map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Button disconnect, switchDir;
    private TextView target_station_view, main_station_view, arrival_time_view, nearest_train_dist_view,num_trains_view;
    final boolean[] connect = {true};
    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Context context = getApplicationContext();
        HashMap <String, String> StationTypeKey = TrainLineKeys();
        disconnect = findViewById(R.id.disconnect);
        switchDir = findViewById(R.id.switch_direction);
        main_station_view = findViewById(R.id.main_station_view);
        target_station_view = findViewById(R.id.target_station_view);
        arrival_time_view = findViewById(R.id.arrival_time_view);
        nearest_train_dist_view = findViewById(R.id.nearest_train_dist_view);
        num_trains_view = findViewById(R.id.num_trains_view);
        mMap = googleMap;
        Bundle bb;
        bb=getIntent().getExtras();

        assert bb != null;
        final String station_name = bb.getString("target_station_name");
        final String station_type = bb.getString("target_station_type");
        final String [] station_coordinates = bb.getStringArray("target_station_coordinates");
        final String[] train_dir = {bb.getString("train_direction")};
        ZoomIn(13.1f, station_coordinates);
        String type  = StationTypeKey.get(station_type.toLowerCase());
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s", type);
        Log.e("url", url);


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (connect[0]){
                    final ArrayList<HashMap> chosen_trains = new ArrayList<>();
                    final ArrayList<Double> train_distances_from_target = new ArrayList<>();


                    try {

                        Document content = Jsoup.connect(url).get();
                        String[] train = content.select("train").outerHtml().split("</train>");
                        for (String each_train: train){
                            BufferedReader reader = get_csv_reader();
                            final Chicago_Transits chicago_transits = new Chicago_Transits(reader);


                            HashMap<String, String> train_info = get_train_info(each_train);


                            if (train_info.get("train_direction").equals(train_dir[0])){
                                String main_station_name = train_info.get("main_station");
                                String[] main_station_coordinates = chicago_transits.retrieve_station_coordinates(main_station_name, station_type);


                                Double main_station_lat = Double.parseDouble(main_station_coordinates[0]);
                                Double main_station_lon = Double.parseDouble(main_station_coordinates[1]);

                                Double target_station_lat = Double.parseDouble(station_coordinates[0]);
                                Double target_station_lon = Double.parseDouble(station_coordinates[1]);

                                Double currentLat = Double.parseDouble(train_info.get("train_lat"));
                                Double currentLon = Double.parseDouble(train_info.get("train_lon"));

                                Double train_to_main = calculate_coordinate_distance(currentLat, currentLon, main_station_lat, main_station_lon);
                                Double train_to_target = calculate_coordinate_distance(target_station_lat, target_station_lon, currentLat, currentLon);
                                Double main_to_target_distance = calculate_coordinate_distance(target_station_lat, target_station_lon, main_station_lat, main_station_lon);
//                                Log.e("main name", main_station_name);

                                if (withinBounds(train_to_main, main_to_target_distance)){// Train threshold to determine if train has passed target station
                                    continue;

                                }else {
                                    train_distances_from_target.add(train_to_target);
                                    String[] train_coordinates = (train_info.get("train_lat") +","+ train_info.get("train_lon")).split(",");
                                    Log.e("KEY", train_to_target+" Value: "+ train_coordinates[0] +","+train_coordinates[1] );
                                    train_info.put(String.valueOf(train_to_target), train_coordinates[0] +","+train_coordinates[1]);
                                    train_info.put("train_to_target", String.valueOf(train_to_target));
                                    train_info.put("train_to_main", String.valueOf(train_to_main));
                                    train_info.put("main_lan", String.valueOf(main_station_lat));
                                    train_info.put("main_lon", String.valueOf(main_station_lon));
                                    chosen_trains.add(train_info);
                                }

                            }


                        }
                        Collections.sort(train_distances_from_target);
                        display_on_user_interface(chosen_trains, station_coordinates, station_name, station_type, train_distances_from_target);


                        switchDir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (train_dir[0].equals("1")){
                                    train_dir[0] = "5";

                                }else {
                                    train_dir[0] = "1";
                                }

                            }
                        });

//                        Log.d("update", "done.");
                        sleep(2500);


                        }catch (IOException | InterruptedException e){
                            e.printStackTrace();
                        }




                        disconnect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMap.clear();
                                    Log.d("Connection Status", "Connection Closed");
                                    connect[0] = false;
                                    Intent intent = new Intent(MapsActivity.this, mainactivity.class);
                                    startActivity(intent);
                                }
                            });
                    }

                }
            }).start();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  BufferedReader get_csv_reader(){
        InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
        BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
        return reader;

    }
    private void sleep(int milli) throws InterruptedException {
        Thread.sleep(milli);

    }
    private void display_on_user_interface(final ArrayList<HashMap> chosen_trains,
                                           final String[] station_coordinates,
                                           final String station_name,
                                           final String station_type,
                                           final ArrayList<Double> sorted_train_distances
                                          ){
        runOnUiThread(new Runnable() {
            @SuppressLint({"SetTextI18n", "LongLogTag"})
            @Override
            public void run() {
                int idx =0;
                mMap.clear();
                for (HashMap<String, String>current_train : chosen_trains) {




//                    String train_to_track = current_train.get(String.valueOf(sorted_train_distances.get(idx)));
//                    String trainLat = current_train.get(sorted_train_distances.get(idx))[0];
//                    String trainLon = current_train.get(sorted_train_distances.get(idx))[1];
//                    Log.e("COORD", ""+train_to_track);



                    target_station_view.setText("TRACKING: "+station_name);
                    main_station_view.setText("TOWARDS: "+current_train.get("main_station"));
                    num_trains_view.setText("NUMBER OF TRAINS: "+String.valueOf(chosen_trains.size()));
                    Log.e("Distance", current_train.get("train_to_target"));
                    String main_station_lat = current_train.get("main_lan");
                    String main_station_lon = current_train.get("main_lon");
                    String train_lat = current_train.get("train_lat");
                    String train_lon =  current_train.get("train_lon");
                    Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default");
                    station_marker.showInfoWindow();
                    Marker main_marker = addMarker(main_station_lat, main_station_lon, current_train.get("main_station"), "main");
                    Marker train_marker = addMarker(train_lat, train_lon,  "Next Stop: "+ current_train.get("train_to_target"), station_type);
//
                    if (current_train.get("isApproaching").equals("1")){
                        addMarker(train_lat, train_lon,  "Arrived at: "+station_name, "green");

                    }

                    if (Double.parseDouble( current_train.get("train_to_target")) <= .2) { // Check if arrived at station
                        Log.e("ARRIVED", "Train arrived at: " + station_name);
                        addMarker( train_lat, train_lon,  "Arrived at: "+station_name, "orange");


                    }
                    idx++;
                    if (idx == 20){
                        break;
                    }
                }
                Log.d("DONE", "DONE");
            }

        });



    }
    private Double calculate_coordinate_distance(double lat1, double lon1, double lat2, double lon2){
        final int R = 6371; // Radious of the earth


        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);


        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;

    }
    private HashMap<String, String> get_train_info(String each_train){
    HashMap<String, String> train_info = new HashMap<>();

    String currTrain= each_train.replaceAll("\n", "")
            .replaceAll(" ", "")
            .replaceAll("<train>", "</train>")
            .replaceAll("</train>", "");

    currTrain = currTrain.replaceAll("<nextStaId>[\\s\\S]*?</nextStaId>","");
    currTrain = currTrain.replaceAll("<nextStpId>[\\s\\S]*?</nextStpId>","");
    currTrain = currTrain.replaceAll("<rn>[\\s\\S]*?</rn>","");
    currTrain = currTrain.replaceAll("<destSt>[\\s\\S]*?</destSt>","");

    String main_station =  get_xml_tag_value(currTrain, "<destNm>", "</destNm>");
    String train_direction =  get_xml_tag_value(currTrain, "<trDr>", "</trDr>");
    String next_train_stop =  get_xml_tag_value(currTrain, "<nextStaNm>", "</nextStaNm>");
    String predicted_arrival_time =  get_xml_tag_value(currTrain, "<arrT>", "</arrT>");
    String isApproaching =  get_xml_tag_value(currTrain, "<isApp>", "</isApp>");
    String isDelayed =  get_xml_tag_value(currTrain, "<isDly>", "</isDly>");
    String train_lat =  get_xml_tag_value(currTrain, "<lat>", "</lat>");
    String train_lon =  get_xml_tag_value(currTrain, "<lon>", "</lon>");

    train_info.put("isApproaching", isApproaching);
    train_info.put("isDelayed", isDelayed);
    train_info.put("main_station", main_station.toLowerCase().replace(" ", ""));
    train_info.put("arrival_time", predicted_arrival_time);
    train_info.put("next_stop", next_train_stop.toLowerCase().replace(" ", ""));
    train_info.put("train_direction", train_direction);
    train_info.put("train_lat", train_lat);
    train_info.put("train_lon", train_lon);


    return train_info;
}

    private String get_xml_tag_value(String raw_xml, String startTag, String endTag){

        return StringUtils.substringBetween(raw_xml, startTag, endTag);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finishAndRemoveTask();
            Thread.currentThread().interrupt();
            connect[0] = false;

        }
        return super.onKeyDown(keyCode, event);
    }
    @SuppressLint("DefaultLocale")
    private Marker addMarker(String lat, String lon, String title, String color){
        HashMap<String, Float> colors = new HashMap<>();
        colors.put("default", BitmapDescriptorFactory.HUE_MAGENTA);
        colors.put("main", BitmapDescriptorFactory.HUE_AZURE);
        colors.put("blue", BitmapDescriptorFactory.HUE_BLUE );
        colors.put("purple", BitmapDescriptorFactory.HUE_CYAN );
        colors.put("pink", BitmapDescriptorFactory.HUE_BLUE );
        colors.put("green", BitmapDescriptorFactory.HUE_GREEN );
        colors.put("brown",BitmapDescriptorFactory.HUE_GREEN );
        colors.put("orange", BitmapDescriptorFactory.HUE_ORANGE );
        colors.put("red", BitmapDescriptorFactory.HUE_RED);
        colors.put("yellow", BitmapDescriptorFactory.HUE_YELLOW);
        LatLng train_marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        return mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(BitmapDescriptorFactory.defaultMarker(colors.get(color))));


    }
    private void ZoomIn(Float zoomLevel, String[] coord){
        assert coord != null;
        LatLng position = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));

    }
    private HashMap<String, String> TrainLineKeys(){
        HashMap<String, String> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red", "red");
        TrainLineKeyCodes.put("blue", "blue");
        TrainLineKeyCodes.put("brown", "brn");
        TrainLineKeyCodes.put("green", "g");
        TrainLineKeyCodes.put("orange", "org");
        TrainLineKeyCodes.put("pink", "pink");
        TrainLineKeyCodes.put("purple", "p");
        TrainLineKeyCodes.put("yellow", "y");

        return TrainLineKeyCodes;
    }
    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
    private boolean withinBounds(double dist1, double dist2){

        if (dist1 >= 0 && dist1 <= dist2) { // Train threshold to determine if train has passed target station
            return true;

        }else{
            return false;
        }
    }
}