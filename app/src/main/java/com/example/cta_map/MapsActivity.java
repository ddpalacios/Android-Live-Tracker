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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (connect[0]){
                try {
                    Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    final ArrayList<HashMap> chosen_trains = new ArrayList<>();
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
                                      addMarker(train_info.get("main_lat"), train_info.get("main_lon"), train_info.get("main_station"), "cyan", 1f);

                                      if (specified_train_direction[0].equals("1")){
                                          int start = 0;
                                          int end = stops.indexOf(station_name);
                                          List<String> ignored_stations = stops.subList(start, end);
                                          if (ignored_stations.contains(train_info.get("next_stop"))){
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, .5f);

                                          }
                                          else{
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, 1f);

                                          }




                                      }
                                      else {

                                          int start= stops.indexOf(station_name)+1;
                                          int end = stops.size();
                                          List<String> ignored_stations = stops.subList(start, end);
                                          if (ignored_stations.contains(train_info.get("next_stop"))){
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, .5f);

                                          }
                                          else{
                                              Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, 1f);

                                          }





                                      }







                                  }
                                  Log.d("Update", "DONE.");

                              }


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

//    private void connect_and_run_main_thread(final String url){
//        final Context context = getApplicationContext();
//          /*
//
//          Everything is being ran within its own thread.
//         This allows us to run our continuous web extraction
//         while also performing other user interactions
//
//          */
//        new Thread(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @SuppressLint({"DefaultLocale", "WrongConstant"})
//            @Override
//            public void run() {
//                while (connect[0]){
//                    int out_of_bounds = 0;
//                    getLastLocation(); // Continuously extract users last location
//
//                    final ArrayList<HashMap> chosen_trains = new ArrayList<>();
//
//                    try {
//                        Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
//                        String[] train = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
//
//                        for (String each_train: train){
//
//                            BufferedReader reader = read_station_coordinates(R.raw.train_stations);
//                            Chicago_Transits chicago_transits = new Chicago_Transits(reader);
//
//
//
//                            HashMap<String, String> train_info = chicago_transits.get_train_info(each_train); // Feed in given and prepare it as a hashmap with necessary train data
//
//                            if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])){ // Only retrieve the trains that going to users specified direction
//
//                                String main_station_name = train_info.get("main_station");
//                                String[] main_station_coordinates = chicago_transits.retrieve_station_coordinates(main_station_name, station_type);
//
//                                train_info.put("main_lan", main_station_coordinates[0]);
//                                train_info.put("main_lon", main_station_coordinates[1]);
//
//
//
//                                Double distance_from_train_to_main = chicago_transits.calculate_coordinate_distance(
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lat"))),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lon"))),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("main_lan"))),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("main_lon"))));
//
//
//                                Double distance_from_train_to_target = chicago_transits.calculate_coordinate_distance(
//                                        Double.parseDouble(target_station_coordinates[0]),
//                                        Double.parseDouble(target_station_coordinates[1]),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lat"))),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lon"))));
//
//
//                                Double distance_from_target_to_main = chicago_transits.calculate_coordinate_distance(
//                                        Double.parseDouble(target_station_coordinates[0]),
//                                        Double.parseDouble(target_station_coordinates[1]),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("main_lan"))),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("main_lon"))));
//
//
//                                chosen_trains.add(train_info);
//
//
//                                if (withinBounds(distance_from_train_to_main, distance_from_target_to_main)){
//                                    // TODO: Debug threshold of train lines. e.g. BLUE line does not pass.
//                                    out_of_bounds++;
//                                    continue;
//                                }else {
//                                    train_info.put("train_to_target", String.format("%.2f", distance_from_train_to_target));
//                                    chosen_trains.add(train_info); // Extracted trains going specified direction and still heading towards target station
//                                }
//                            }
//                        }
//                        display_on_user_interface(chosen_trains, station_coordinates, station_name, station_type);
//                        switchDir.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Thread.currentThread().interrupt();
//                                Toast.makeText(context, "Switching Directions. Please Wait...", Toast.LENGTH_SHORT).show();
//
//                                if (specified_train_direction[0].equals("1")){
//                                    specified_train_direction[0] = "5";
//
//                                }else {
//                                    specified_train_direction[0] = "1";
//                                }
//                            }
//                        });
//
//
//                        Thread.sleep(2000);
//                    }catch (IOException | InterruptedException e){
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//
//
//
//    }
//
//    private void display_on_user_interface(final ArrayList<HashMap> chosen_trains,
//                                           final String[] station_coordinates,
//                                           final String station_name,
//                                           final String station_type
//                                          ){
//        runOnUiThread(new Runnable() {
//                          @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                          @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale", "WrongConstant", "ShowToast"})
//                          @Override
//                          public void run() {
//                              mMap.clear();
//
//                              if (chosen_trains.size() ==0){
//                                  status.setBackgroundColor(Color.WHITE);
//                                  status.setText("No Trains Available.");
//                                  Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default",1f);
//                              }
//                              BufferedReader reader = read_station_coordinates(R.raw.train_stations);
//                              Chicago_Transits chicago_transits = new Chicago_Transits(reader);
//
//                              boolean green_indicator = false; // get ready to leave
//                              boolean yellow_indicator = false; // start to leave
//                              boolean blue_indicator = false; // train is approaching station
//                              boolean orange_indicator = false; // train has arrived
//                              boolean ButtonIsOn = false; // status button
//                              int TRAIN_SPEED = 25;
//                              float WALK_SPEED = (float) 3.1;
//                              int num_of_vibrants = 0;
//                              int minutes_to_spare = 0;
//                              int late_amount = 0;
//                              float marked_opacity = 1f;
//                              float unmarked_opacity = .5f;
//                              String[] userLocation = ((String) userLoc.getText()).split(",");
//                              double userLatitude = Double.parseDouble(userLocation[0]);
//                              double userLongitude = Double.parseDouble(userLocation[1]);
//                              double targetLatitude = Double.parseDouble(station_coordinates[0]);
//                              double targetLongitude = Double.parseDouble(station_coordinates[1]);
//                              final ArrayList<String> arrayList  = new ArrayList<>();
//                              final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
//                              double user_distance_from_station = chicago_transits.calculate_coordinate_distance(userLatitude, userLongitude, targetLatitude,targetLongitude)  * 0.621371;
//                              int user_to_target_ETA = (int) ((user_distance_from_station/ WALK_SPEED)*100);
//                              list.setAdapter(adapter);
//                              ArrayList<Integer> train_eta = new ArrayList<>();
//                              arrayList.add("You are "+user_to_target_ETA+" minute(s) away from "+station_name );
//                              adapter.notifyDataSetChanged();
//
//                              for (HashMap current_train: chosen_trains){
//                                  double current_distance_from_target = Double.parseDouble((String) Objects.requireNonNull(current_train.get("train_to_target"))) * 0.621371;
//                                  int train_min_ETA = (int) ((current_distance_from_target / TRAIN_SPEED)*100); // TODO: debug time approximations. Faulty calculation result < 30 mph Mistrack markers
//                                  train_eta.add(train_min_ETA);
//
//
//                              }
//                              Collections.sort(train_eta);
//                              for (Integer i: train_eta){
//                                  String main_name = (String) chosen_trains.get(0).get("main_station");
//
//                                  if (i < 1){
//                                      adapter.add("Train to " + main_name + ". Less Than 1 Minute away");
//                                      adapter.notifyDataSetChanged();
//
//                                  }else {
//                                      arrayList.add("Train to " + main_name + ". ETA: " + i + " Minute(s)");
//                                      adapter.notifyDataSetChanged();
//                                  }
//
//                              }
//
//
//
//                              for (HashMap current_train: chosen_trains){
//                                  double current_distance_from_target = Double.parseDouble((String) Objects.requireNonNull(current_train.get("train_to_target"))) * 0.621371;
//                                  int train_min_ETA = (int) ((current_distance_from_target / TRAIN_SPEED)*100) ;
//                                  String[] train_coord = (current_train.get("train_lat") +","+current_train.get("train_lon")).split(",");
//                                  String main_station_lat = (String) current_train.get("main_lan");
//                                  String main_station_lon = (String) current_train.get("main_lon");
//                                  String train_lat = (String) current_train.get("train_lat");
//                                  String train_lon = (String) current_train.get("train_lon");
//
//                                  Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default",marked_opacity);
//                                  addMarker(main_station_lat, main_station_lon, (String) current_train.get("main_station"), "main",marked_opacity);
//                                  int train_hour_ETA = (int) ((current_distance_from_target / TRAIN_SPEED)*100) /60;
//
//
//
//                                  if (train_min_ETA >= 0 && train_min_ETA <=20){
//
//                                      if (user_to_target_ETA <= train_min_ETA) {
//                                          green_indicator = true;
//                                          addMarker(train_lat, train_lon, "NEXT STOP:  "+ current_train.get("next_stop"), "green", marked_opacity);
//                                          minutes_to_spare = train_min_ETA - user_to_target_ETA;
//
//                                      }else if (user_to_target_ETA > train_min_ETA){
//                                          late_amount = user_to_target_ETA - train_min_ETA;
//                                          if (late_amount >=0 && late_amount <4){
//                                              yellow_indicator = true;
//                                              addMarker(train_lat, train_lon, "NEXT STOP:  "+ current_train.get("next_stop"), "yellow", marked_opacity);
//
//                                          }else if (late_amount >=4){
//                                              blue_indicator = true;
//                                              addMarker(train_lat, train_lon, "NEXT STOP:  "+ current_train.get("next_stop"), "blue",marked_opacity).showInfoWindow();
//
//
//                                          }
//                                      }
//                                  } else{
//                                      Marker regular_marker = addMarker(train_lat, train_lon, "NEXT STOP:  "+ current_train.get("next_stop"), station_type,unmarked_opacity);
//                                  }
//
//
//
//
//                                  if (!yellow_indicator && !green_indicator && !blue_indicator){ // if no train near, show station name
//                                      station_marker.showInfoWindow();
//                                      status.setBackgroundColor(Color.WHITE);
//                                      status.setText("No Nearby Trains.");
//                                      status.setTextColor(Color.BLACK);
//                                  }
//                                  else if (!blue_indicator && green_indicator&& !yellow_indicator){
//                                      status.setBackgroundColor(Color.GREEN);
//                                      status.setText(minutes_to_spare+" Minute(s) to spare.");
//                                      status.setTextColor(Color.BLACK);
//
//                                  }
//                                  else if (!blue_indicator && !green_indicator&& yellow_indicator){
//                                      status.setBackgroundColor(Color.YELLOW);
//                                      status.setText(late_amount+" Minute(s) Late.");
//                                      status.setTextColor(Color.BLACK);
//                                  }
//                                  else if (!blue_indicator && green_indicator && yellow_indicator){
//                                      status.setBackgroundColor(Color.YELLOW);
//                                      status.setText(late_amount+" Minute(s) Late.");
//                                      status.setTextColor(Color.BLACK);
//                                  }
//                                  else if (current_train.get("isApproaching").equals("1") && current_train.get("next_stop").equals(station_name)){
//                                      Log.e("status", "Approaching");
//                                      arrayList.set(1, "Train to " + current_train.get("main_station") + " is Approaching");
//                                      adapter.notifyDataSetChanged();
//                                  }
//
//
//                                  else{
//                                      status.setBackgroundColor(Color.BLUE);
//                                      status.setText(late_amount+" Minute(s) Late.");
//                                      status.setTextColor(Color.WHITE);
//
//                                  }
//
//
//
//
//
//
//                              }
//                              Log.d("DONE", "DONE");
//            }
//
//        });
//
//    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private BufferedReader setup_file_reader(int file){
        InputStream CSVfile = getResources().openRawResource(file);
        return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

    }

    @SuppressLint("DefaultLocale")
    private Marker addMarker(String lat, String lon, String title, String color, Float alpha){
        float opacity = alpha;
        HashMap<String, Float> colors = new HashMap<>();
        colors.put("default", BitmapDescriptorFactory.HUE_MAGENTA);
        colors.put("main", BitmapDescriptorFactory.HUE_AZURE);
        colors.put("blue", BitmapDescriptorFactory.HUE_BLUE );
        colors.put("cyan", BitmapDescriptorFactory.HUE_CYAN );
        colors.put("rose", BitmapDescriptorFactory.HUE_ROSE );
        colors.put("purple", BitmapDescriptorFactory.HUE_CYAN );
        colors.put("pink", BitmapDescriptorFactory.HUE_BLUE );
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

//    private boolean withinBounds(double dist1, double dist2){
//        if (dist1 >= 0 && dist1 <= dist2){   // Train threshold to determine if train has passed target station
//            return true;
//
//        }else {
//            return false;
//}
//
//
//    }
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//    }
//    @Override
//    public boolean onMyLocationButtonClick() {
//        return false;
//    }

//
//        disconnect.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View v) {
//
//                if (connect[0]) {
//                    disconnect.setText("Connect");
//                    connect[0] = false;
//                    Log.d("Connection Status", "Connection Closed");
//                    Toast.makeText(context, "DISCONNECTED", Toast.LENGTH_SHORT).show();
//
//                }else {
//                    disconnect.setText("Disconnect");
//                    connect[0] = true;
//                    Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show();
//                    Log.d("Connection Status", "Connection Opened");
//                    onMapReady(mMap);
//
//                }
//
//            }
//        });
//
//
//
//
//    }
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