package com.example.cta_map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Button disconnect;

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        disconnect = findViewById(R.id.disconnect);
        InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
        BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
        final Chicago_Transits chicago_transits = new Chicago_Transits(reader);

        mMap = googleMap;
        Bundle bb;
        bb=getIntent().getExtras();
        assert bb != null;

        final String [] station_coordinates = bb.getStringArray("station_coordinates");
        final String train_dir = bb.getString("train_direction");
        final String station_name = bb.getString("station_name");
        final String station_type = bb.getString("station_type");
        assert station_coordinates != null;
        float zoomLevel = 13.1f; //This goes up to 21
        LatLng chicago = new LatLng(Double.parseDouble(station_coordinates[0]), Double.parseDouble(station_coordinates[1]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chicago, zoomLevel));



        ////////////////////////////////////////////////////

        HashMap <String, String> StationTypeKey = TrainLineKeys();

        assert station_type != null;
        final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+StationTypeKey.get(station_type.toLowerCase());


        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {


                while (connect[0]){
                    try {
                        Document content = Jsoup.connect(url).get();
                        final ArrayList<Integer> indexies = get_trains_from(train_dir, content);
                        final ArrayList<String> train_coordinates = new ArrayList<>();
                        final ArrayList<String> approaching_trains = new ArrayList<>();
                        final ArrayList<String> next_stop = new ArrayList<>();
                        final ArrayList<String> train_destination = new ArrayList<>();

                        final String station_destination = content.select("destNm").text();
                        final String[] isApproaching = content.select("isApp").text().split(" ");
                        final String[] latitude = content.select("lat").text().split(" ");
                        final String[] longtitude = content.select("lon").text().split(" ");
                        final String[] nextStop_tags = content.select("nextStaNm").text().split(" ");



                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {

                                for (Integer index : indexies){
                                    train_coordinates.add((latitude[index] + ","+ longtitude[index]));
                                    approaching_trains.add(isApproaching[index]);
                                    next_stop.add(nextStop_tags[index]);


                                }
                                ArrayList<Double> train_distance_from_station = calculate_nearest_train_from(train_coordinates, station_coordinates);
                                Double nearest_train = Collections.min(train_distance_from_station);


                                mMap.clear();
                                for (int i=0; i<indexies.size();i++){
                                    Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default");

                                    String currentLat = train_coordinates.get(i).split(",")[0];
                                    String currentLon = train_coordinates.get(i).split(",")[1];

                                    String isApproaching = approaching_trains.get(i);
                                    String nextStop = next_stop.get(i);
                                    Double current_distance_from_station = train_distance_from_station.get(i);

                                    @SuppressLint("DefaultLocale") Marker train_marker = addMarker(currentLat, currentLon, String.format("%.2f", current_distance_from_station)+"km", station_type);





//                                    Marker station_marker = mMap.addMarker(new MarkerOptions().position(train_station_marker).title(station_name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                    if (current_distance_from_station <= .2){
                                        Log.e("ARRIVED", "Train arrived at: "+station_name);
                                        Log.e("Size", train_coordinates.size()+"");



                                    }

                                }

                                Log.d("progress", "done.");




                            }

                        });

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


                        Thread.sleep(1500);

                    } catch (IOException | InterruptedException e) {
                        Log.d("Error", "Error in extracting");
                    }







                }

            }
        }).start();



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
        colors.put("blue", BitmapDescriptorFactory.HUE_BLUE );
        colors.put("green", BitmapDescriptorFactory.HUE_GREEN );
        colors.put("orange", BitmapDescriptorFactory.HUE_ORANGE );
        colors.put("red", BitmapDescriptorFactory.HUE_RED);
        colors.put("yellow", BitmapDescriptorFactory.HUE_YELLOW);


        LatLng train_marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        return mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(BitmapDescriptorFactory.defaultMarker(colors.get(color))));


    }



    private ArrayList<Integer> get_trains_from(String dir, Document content){
        final ArrayList<Integer> indexies = new ArrayList<>();
        String[] train_direction = content.select("trDr").text().split(" ");


        for (int i=0; i< train_direction.length; i++){
            String elem = train_direction[i];
            if (elem.equals(dir)){
                indexies.add(i);
            }

        }

        return indexies;
    }
    private HashMap<String, String> TrainLineKeys(){
        HashMap<String, String> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red", "red");
        TrainLineKeyCodes.put("green", "g");
        TrainLineKeyCodes.put("purple", "p");
        TrainLineKeyCodes.put("orange", "org");
        TrainLineKeyCodes.put("pink", "pink");
        TrainLineKeyCodes.put("yellow", "y");
        TrainLineKeyCodes.put("blue", "blue");

        return TrainLineKeyCodes;
    }



    private ArrayList<Double>  calculate_nearest_train_from(ArrayList<String> chosen_trains,String[] station_coordinates) {
        ArrayList<Double> train_distance = new ArrayList<>();
        final int R = 6371; // Radious of the earth

        double station_lat = Double.parseDouble(station_coordinates[0]);
        double station_lon = Double.parseDouble(station_coordinates[1]);

        for (String coord : chosen_trains){
            String[] train_cord = coord.split(",");
            double train_lat = Double.parseDouble(train_cord[0]);
            double train_lon = Double.parseDouble(train_cord[1]);
            Double latDistance = toRad(train_lat-station_lat);
            Double lonDistance = toRad(train_lon-station_lon);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(toRad(station_lat)) * Math.cos(toRad(train_lat)) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double distance = R * c;

            train_distance.add(distance);
        }
//        Collections.sort(train_distance);



        return  train_distance;//train_distance.get(0) * 0.62137;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }










}
