package com.example.cta_map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;


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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Context context = getApplicationContext();
        disconnect = findViewById(R.id.disconnect);
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
        HashMap <String, String> StationTypeKey = TrainLineKeys();
        assert station_type != null;
        final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+StationTypeKey.get(station_type.toLowerCase());
        Log.e("url", url);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                while (connect[0]){
                    InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
                    final Chicago_Transits chicago_transits = new Chicago_Transits(reader);

                    try {
                        Document content = Jsoup.connect(url).get();
                        final ArrayList<Integer> indexies = get_trains_from(train_dir, content);




                        final ArrayList<String> train_coordinates = new ArrayList<>();
                        final ArrayList<String> approaching_trains = new ArrayList<>();
                        final ArrayList<String> next_stop = new ArrayList<>();
                        final ArrayList<String> train_destination = new ArrayList<>();


                            final String[] station_destination = content.select("destNm").outerHtml().replace(" ", "").replace("<destNm>", "").replace(" </destNm>", "").split("</destNm>");
                            final String[] isApproaching = content.select("isApp").text().split(" ");
                            final String[] latitude = content.select("lat").text().split(" ");
                            final String[] longtitude = content.select("lon").text().split(" ");
                            final String[] nextStop_tags = content.select("nextStaNm").text().split(" ");


                        runOnUiThread(new Runnable() {
                            @SuppressLint({"SetTextI18n", "LongLogTag"})
                            @Override
                            public void run() {
                                int inbounds_trains = 0;
                                Log.e("Size", String.valueOf(indexies.size()));
                                if (indexies.size() <= 0 ){
                                    final Toast toast = Toast.makeText(context, "There are: "+ indexies.size()+" trains at the moment!", Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }
                                final Toast toast = Toast.makeText(context, "There are: "+ indexies.size()+" trains at the moment!", Toast.LENGTH_LONG);
                                toast.show();
                                for (Integer index : indexies) {
                                    train_coordinates.add((latitude[index] + "," + longtitude[index]));
                                    approaching_trains.add(isApproaching[index]);
                                    next_stop.add(nextStop_tags[index]);
                                    train_destination.add(station_destination[index]);



                                }



                                String main_station_name = train_destination.get(0).toLowerCase().replaceAll("\t", "").replaceAll("\n", "");
                                String[] main_station_coordinates = chicago_transits.retrieve_station_coordinates(main_station_name, station_type);
                                if (main_station_coordinates == null) {
                                    Toast.makeText(context, "MAIN Station Not Found!", Toast.LENGTH_LONG).show();
                                }
                                ArrayList<Double> train_distance_from_station = calculate_train_distance(train_coordinates, station_coordinates);

                                assert main_station_coordinates != null;
                                ArrayList<Double> train_distance_from_main_station = calculate_train_distance(train_coordinates, main_station_coordinates);


                                Double main_and_target_station_distance = calculate_coordinate_distance(main_station_coordinates , station_coordinates);
                                Log.e("Distance from target and main", String.valueOf(main_and_target_station_distance));


                                mMap.clear();
                                for (int i = 0; i < indexies.size(); i++) {
                                    Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default");

                                    Double train_to_main = train_distance_from_main_station.get(i);
                                    Double train_to_target = train_distance_from_station.get(i);
                                    String isApproaching = approaching_trains.get(i);
                                    String nextStop = next_stop.get(i);
                                    String currentLat = train_coordinates.get(i).split(",")[0];
                                    String currentLon = train_coordinates.get(i).split(",")[1];

                                    if (train_to_main >= 0 && train_to_main <= main_and_target_station_distance){
                                        inbounds_trains++;


                                    }else{

                                        if (train_to_target <= .2) {
                                            Log.e("Index", String.valueOf(i));
                                            Log.e("ARRIVED", "Train arrived at: " + station_name);
                                            Log.e("Size", train_coordinates.size() + "");
                                            train_to_target =train_to_target * -1;
                                            Log.e("Distance", String.valueOf(train_to_target));

                                        }



                                        Circle circle = mMap.addCircle(new CircleOptions()
                                                .center(new LatLng(Double.parseDouble(station_coordinates[0]), Double.parseDouble(station_coordinates[1])))
                                                .radius(300)
                                                .strokeColor(Color.RED));




                                        Marker dest_market = addMarker(main_station_coordinates [0], main_station_coordinates [1], main_station_name, "main");
                                        @SuppressLint("DefaultLocale") Marker train_marker = addMarker(currentLat, currentLon, String.format("%.2f", train_to_target) + "km", station_type);




                                    }






                                }
                                Log.e("in bounds", "There are "+inbounds_trains+" train(s) passed "+station_name+ " Out of: "+ indexies.size());
                                Log.d("Progress", "done.");

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

                        Thread.sleep(15000);

                    } catch (IOException | InterruptedException e) {
                        Log.d("Error", "Error in extracting");
                    }
                }

            }
        }).start();



    }


    private Double calculate_coordinate_distance(String[] coord1, String[] coord2){
        final int R = 6371; // Radious of the earth
        double lat1 = Double.parseDouble(coord1[0]);
        double lon1 = Double.parseDouble(coord1[1]);
        double lat2 = Double.parseDouble(coord2[0]);
        double lon2 = Double.parseDouble(coord2[1]);


        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);


        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;

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
    private ArrayList<Integer> get_trains_from(String dir, Document content){
        final ArrayList<Integer> indexies = new ArrayList<>();
        Context context = getApplicationContext();
        String[] train_direction = content.select("trDr").text().split(" ");
        if (train_direction.length <= 0){
            Toast.makeText(context, "No trains available", Toast.LENGTH_LONG).show();


        }


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
        TrainLineKeyCodes.put("blue", "blue");
        TrainLineKeyCodes.put("brown", "brn");
        TrainLineKeyCodes.put("green", "g");
        TrainLineKeyCodes.put("orange", "org");
        TrainLineKeyCodes.put("pink", "pink");
        TrainLineKeyCodes.put("purple", "p");
        TrainLineKeyCodes.put("yellow", "y");

        return TrainLineKeyCodes;
    }
    private ArrayList<Double>  calculate_train_distance(ArrayList<String> chosen_trains,String[] station_coordinates) {
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

        return  train_distance;//train_distance.get(0) * 0.62137;
    }
    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }










}
