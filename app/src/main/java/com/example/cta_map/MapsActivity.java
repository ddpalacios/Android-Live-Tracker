package com.example.cta_map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;





public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private EditText station_name, station_type, direction;
    private Button disconnect;


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
        String [] station_coordinates = bb.getStringArray("station_coordinates");
        final String train_dir = bb.getString("train_direction");
        String station_name = bb.getString("station_name");
        String station_type = bb.getString("station_type");

        // Add a marker in Sydney and move the camera
        assert station_coordinates != null;
        LatLng sydney = new LatLng(Double.parseDouble(station_coordinates[0]), Double.parseDouble(station_coordinates[1]));
        mMap.addMarker(new MarkerOptions().position(sydney).title(station_name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        ////////////////////////////////////////////////////

        HashMap <String, String> StationTypeKey = TrainLineKeys();

        assert station_type != null;
        final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+StationTypeKey.get(station_type.toLowerCase());
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                final boolean[] connect = {true};
                while (connect[0]){
                    try {
                        Document content = Jsoup.connect(url).get();

                        final ArrayList<String> chosenTrainsCord = get_trains_from(train_dir, content);
                        Log.e("Trains", chosenTrainsCord+"");
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mMap.clear();
                                for (String train_cord : chosenTrainsCord) {
                                    String[] f = train_cord.split(",");

                                    LatLng sydney = new LatLng(Double.parseDouble(f[0]), Double.parseDouble(f[1]));
                                    mMap.addMarker(new MarkerOptions().position(sydney).title(Arrays.toString(f)));
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                        }






                            }

                        });







//                        Thread.sleep(1500);

                    } catch (IOException e) {
                        Log.d("Error", "Error in extracting");
                    }


                    disconnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Connection Status", "Connection Closed");
                            connect[0] = false;



                        }
                    });



                }









            }
        }).start();



    }

    private ArrayList<String>  get_trains_from(String dir, Document content){
        final ArrayList<Integer> indexies = new ArrayList<Integer>();
        final ArrayList<String> chosenTrains = new ArrayList<String>();
        String[] latitude = content.select("lat").text().split(" ");
        String[] longtitude = content.select("lon").text().split(" ");
        String[] train_direction = content.select("trDr").text().split(" ");


        for (int i=0; i< train_direction.length; i++){
            String elem = train_direction[i];
            if (elem.equals(dir)){
                indexies.add(i);
            }

        }

        for (Integer index : indexies){
            chosenTrains.add((latitude[index] + ","+ longtitude[index]));

        }



        return chosenTrains;
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



}
