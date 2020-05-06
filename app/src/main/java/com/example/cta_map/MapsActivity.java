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
import android.widget.EditText;

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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private EditText station_name, station_type, direction;
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
        disconnect = (Button) findViewById(R.id.disconnect);

        mMap = googleMap;
        Bundle bb;
        bb=getIntent().getExtras();
        assert bb != null;
        final HashMap<String, Marker> hashMapMarker = new HashMap<>();

        final String [] station_coordinates = bb.getStringArray("station_coordinates");
        final String train_dir = bb.getString("train_direction");
        final String station_name = bb.getString("station_name");
        final String station_type = bb.getString("station_type");

        // Add a marker in Sydney and move the camera
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
                    final int[] idx = {0};
                    try {
                        Document content = Jsoup.connect(url).get();

                        final ArrayList<Integer> indexies = get_trains_from(train_dir, content);
                        final ArrayList<String> train_coordinates = new ArrayList<String>();
                        final ArrayList<String> approaching_trains = new ArrayList<>();
                        final ArrayList<String> next_stop = new ArrayList<>();

                        final String[] isApproaching = content.select("isApp").text().split(" ");
                        String[] latitude = content.select("lat").text().split(" ");
                        String[] longtitude = content.select("lon").text().split(" ");
                        String[] nextStop_tags = content.select("nextStaNm").text().split(" ");

                                for (Integer index : indexies){
                                    train_coordinates.add((latitude[index] + ","+ longtitude[index]));
                                    approaching_trains.add(isApproaching[index]);
                                    next_stop.add(nextStop_tags[index]);

                         }



                        final ArrayList<Double> train_distance_from_station = calculate_nearest_train_from(train_coordinates, station_coordinates);



                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mMap.clear();

                                for (int i=0; i<train_coordinates.size();i++){
                                    String[] current_coordinates = train_coordinates.get(i).split(",");
                                    String train_approaching = approaching_trains.get(i);
                                    String train_next_stop = next_stop.get(i);
                                    Double current_distance_from_station = train_distance_from_station.get(i);

                                    Log.e("coord", "Size: "+ train_coordinates.size()+"| "+"Approaching: "+train_approaching+" | Current Coordinates: "+ Arrays.toString(current_coordinates)+" | Next Stop: "+train_next_stop+" | Distance: "+train_distance_from_station.get(i));
                                    LatLng train_marker = new LatLng(Double.parseDouble(current_coordinates[0]), Double.parseDouble(current_coordinates[1]));
                                    Marker marker = mMap.addMarker(new MarkerOptions().position(train_marker).title(Arrays.toString(current_coordinates)));
                                    LatLng train_station_marker = new LatLng(Double.parseDouble(station_coordinates[0]), Double.parseDouble(station_coordinates[1]));
                                    Marker station_marker = mMap.addMarker(new MarkerOptions().position(train_station_marker).title(station_name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


                                    if (current_distance_from_station <= .2){
                                        Log.e("ARRIVED", "Train arrived at: "+station_name);
                                        train_coordinates.remove(i);
                                        approaching_trains.remove(i);
                                        next_stop.remove(i);
                                        train_distance_from_station.remove(i);
                                        indexies.remove(i);
                                        Log.e("Size", train_coordinates.size()+"");
                                        marker.remove();



                                    }










                                }
                                Log.d("progress", "done.");




                            }

                        });
                        Thread.sleep(1500);

                    } catch (IOException | ParseException | InterruptedException e) {
                        Log.d("Error", "Error in extracting");
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





    private ArrayList<Integer> get_trains_from(String dir, Document content){
        final ArrayList<Integer> indexies = new ArrayList<Integer>();
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



    private ArrayList<Double>  calculate_nearest_train_from(ArrayList<String> chosen_trains,String[] station_coordinates) throws ParseException {
        ArrayList<Double> train_distance = new ArrayList<Double>();
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
