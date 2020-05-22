package com.example.cta_map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
import java.util.List;
import java.util.Objects;


public class MapsActivity extends FragmentActivity  implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback  {
        private Button  userLoc;
        private ListView list;
        final boolean[] connect = {true};
        private GoogleMap mMap;
        int PERMISSION_ID = 44;
        private FusedLocationProviderClient mFusedLocationClient;


    @RequiresApi(api = Build.VERSION_CODES.M)
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        final RelativeLayout train_eta_list = findViewById(R.id.background);
        final Button disconnect = initiate_button(R.id.disconnect, 133, 205,186);
        final Button hide = initiate_button(R.id.show, 133, 205,186);
        final Button switch_direction = initiate_button(R.id.switch_direction, 133, 205,186);
        final Button choose_station = initiate_button(R.id.pickStation, 133, 205,186);
        initiate_button(R.id.userLoc, 133, 205,186).setVisibility(View.GONE);

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
        final Context context = getApplicationContext();
        BufferedReader reader = setup_file_reader(R.raw.train_stations);
        final Chicago_Transits chicago_transits = new Chicago_Transits(reader);
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        final String station_type = bb.getString("target_station_type");
        final String station_name = bb.getString("target_station_name");
        final String[] specified_train_direction = {bb.getString("train_direction")};
        final String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(station_name, station_type);
        final Button switch_direction = initiate_button(R.id.switch_direction, 133, 205,186);
        chicago_transits.ZoomIn(mMap, (float) 13.3, target_station_coordinates);
        mMap.setMyLocationEnabled(true); // Enable user location permission
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        BufferedReader train_station_stops_reader = setup_file_reader(R.raw.train_line_stops);
        final ArrayList<String> stops = chicago_transits.retrieve_line_stations(train_station_stops_reader, station_type);

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


        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(station_type.toLowerCase()));
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
                    final int[] trains = {0};
                    Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    runOnUiThread(new Runnable() {
                          @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                          @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale", "WrongConstant", "ShowToast"})
                          @Override
                          public void run() {
                              mMap.clear();
                             addMarker(target_station_coordinates[0], target_station_coordinates[1], station_name, "default", 1f).showInfoWindow();
                              for (String each_train: train) {
                                  BufferedReader reader = setup_file_reader(R.raw.train_stations);
                                  final Chicago_Transits chicago_transits = new Chicago_Transits(reader);
                                  final HashMap<String, String> train_info = chicago_transits.get_train_info(each_train, station_type); // Feed in given and prepare it as a hashmap with necessary train data
                                  if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
                                      trains[0] +=1;
                                      addMarker(train_info.get("main_lat"), train_info.get("main_lon"), train_info.get("main_station"), "cyan", 1f);

                                      if (specified_train_direction[0].equals("1")){
                                          int start = 0;
                                          int end = stops.indexOf(station_name.replaceAll("[^a-zA-Z0-9]", ""));
                                          List<String> ignored_stations = stops.subList(start, end);
                                          Log.e("ignored", ignored_stations+"");
                                          String next_stop = train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "");

                                          if (ignored_stations.contains(next_stop)){
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, .5f);

                                          } else{
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, 1f);

                                          }

                                      }
                                      else if (specified_train_direction[0].equals("5")){

                                          int start= stops.indexOf(station_name.replaceAll("[^a-zA-Z0-9]", ""))+1;
                                          int end = stops.size();
                                          List<String> ignored_stations = stops.subList(start, end);
                                          Log.e("ignored", ignored_stations+"");
                                          String next_stop = train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "");

                                          if (ignored_stations.contains(next_stop)){
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, .5f);

                                          }
                                          else{
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, 1f);

                                          }
                                      }
                                  }

                              }
                              Log.d("Update", "DONE.");
                          }
                            });

                    Thread.sleep(2000);
                } catch (IOException | InterruptedException e) {
                    Toast.makeText(context, "Invalid URL", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                }
            }
        }).start();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private BufferedReader setup_file_reader(int file){
        InputStream CSVfile = getResources().openRawResource(file);
        return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

    }

    @SuppressLint("DefaultLocale")
    private Marker addMarker(String lat, String lon, String title, String color, Float alpha){
        float opacity = alpha;
        HashMap<String, Float> colors = new HashMap<>();
        colors.put("default", BitmapDescriptorFactory.HUE_ROSE);
        colors.put("main", BitmapDescriptorFactory.HUE_AZURE);
        colors.put("blue", BitmapDescriptorFactory.HUE_BLUE );
        colors.put("cyan", BitmapDescriptorFactory.HUE_CYAN );
        colors.put("rose", BitmapDescriptorFactory.HUE_ROSE );
        colors.put("purple", BitmapDescriptorFactory.HUE_CYAN );
        colors.put("pink", BitmapDescriptorFactory.HUE_MAGENTA+5 );
        colors.put("green", BitmapDescriptorFactory.HUE_GREEN );
        colors.put("brown",BitmapDescriptorFactory.HUE_GREEN );
        colors.put("orange", BitmapDescriptorFactory.HUE_ORANGE );
        colors.put("red", BitmapDescriptorFactory.HUE_RED);
        colors.put("yellow", BitmapDescriptorFactory.HUE_YELLOW);
        LatLng train_marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        Float TRAIN_COLOR = colors.get(color);
        assert TRAIN_COLOR != null;
        return mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(BitmapDescriptorFactory.defaultMarker(TRAIN_COLOR)).alpha(opacity));


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")


    private Button initiate_button(int widget, int r, int g, int b) {
            Button button = findViewById(widget);
            button.setBackgroundColor(Color.rgb(r, g,b));
        return button;
    }
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
//                                    userLoc.setText(location.getLatitude()+","+location.getLongitude());


                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
//            userLoc.setText(mLastLocation.getLatitude()+","+mLastLocation.getLongitude());

        }
    };
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}