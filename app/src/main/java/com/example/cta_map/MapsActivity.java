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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MapsActivity extends FragmentActivity  implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback  {
        private Button disconnect, switchDir, chooseStation, status, show, userLoc, minutes;
        private ListView list;
        private  RelativeLayout test;
        final boolean[] connect = {true};
        private GoogleMap mMap;
        int PERMISSION_ID = 44;
        private FusedLocationProviderClient mFusedLocationClient;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        display_map_interface_buttons();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        HashMap <String, String> StationTypeKey = TrainLineKeys(); // Train line key codes


        mMap = googleMap;
        switchDir = findViewById(R.id.switch_direction);
        switchDir.setBackgroundColor(Color.rgb(133, 205,186));
        mMap.setMyLocationEnabled(true); // Enable user location permission
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        final String station_type = bb.getString("target_station_type");
        final String [] station_coordinates = bb.getStringArray("target_station_coordinates");
        assert station_coordinates != null;
        ZoomIn((float) 13.1, station_coordinates);
        assert station_type != null;
        String type  = StationTypeKey.get(station_type.toLowerCase()); // Retrieve train line code and extract from given url
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s", type);
        Log.e("url", url);
        connect_and_run_main_thread(url);

    }




    private void connect_and_run_main_thread(final String url){
        final Context context = getApplicationContext();
        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        final String station_name = bb.getString("target_station_name");
        final String station_type = bb.getString("target_station_type");
        final String [] station_coordinates = bb.getStringArray("target_station_coordinates");
        assert station_coordinates != null;
        final String[] specified_train_direction = {bb.getString("train_direction")};
        final double target_station_latitude = Double.parseDouble(station_coordinates[0]);
        final double target_station_longitude = Double.parseDouble(station_coordinates[1]);
        ZoomIn((float) 13.1, station_coordinates);


          /*
          Everything is being ran within its own thread.
         This allows us to run our continuous web extraction
         while also performing other user interactions
          */
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"DefaultLocale", "WrongConstant"})
            @Override
            public void run() {
                while (connect[0]){
                    int out_of_bounds = 0;
                    getLastLocation(); // Continuously extract users last location

                    final ArrayList<HashMap> chosen_trains = new ArrayList<>();

                    try {
                        Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                        String[] train = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>

                        for (String each_train: train){

                            BufferedReader reader = read_station_coordinates();
                            Chicago_Transits chicago_transits = new Chicago_Transits(reader);
                            HashMap<String, String> train_info = get_train_info(each_train); // Feed in given and prepare it as a hashmap with necessary train data

                            if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])){ // Only retrieve the trains that going to users specified direction

                                String main_station_name = train_info.get("main_station");
                                String[] main_station_coordinates = chicago_transits.retrieve_station_coordinates(main_station_name, station_type);

                                train_info.put("main_lan", main_station_coordinates[0]);
                                train_info.put("main_lon", main_station_coordinates[1]);



                                Double distance_from_train_to_main = calculate_coordinate_distance(
                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lat"))),
                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lon"))),
                                        Double.parseDouble(main_station_coordinates[0]),
                                        Double.parseDouble(main_station_coordinates[1]));


                                Double distance_from_train_to_target = calculate_coordinate_distance(target_station_latitude,
                                        target_station_longitude,
                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lat"))),
                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lon"))));


                                Double distance_from_target_to_main = calculate_coordinate_distance(target_station_latitude,
                                        target_station_longitude,
                                        Double.parseDouble(main_station_coordinates[0]),
                                        Double.parseDouble(main_station_coordinates[1]));


                                if (withinBounds(distance_from_train_to_main, distance_from_target_to_main)){
                                    // TODO: Debug threshold of train lines. e.g. BLUE line does not pass.
                                    out_of_bounds++;


                                }else {
                                    train_info.put("train_to_target", String.format("%.2f", distance_from_train_to_target));
                                    chosen_trains.add(train_info); // Extracted trains going specified direction and still heading towards target station
                                }

                            }

                        }

                        switchDir.setOnClickListener(new View.OnClickListener() {
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


                        display_on_user_interface(chosen_trains, station_coordinates, station_name, station_type);
                        sleep();
                    }catch (IOException | InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        }).start();



    }

    private void display_on_user_interface(final ArrayList<HashMap> chosen_trains,
                                           final String[] station_coordinates,
                                           final String station_name,
                                           final String station_type
                                          ){
        runOnUiThread(new Runnable() {
                          @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                          @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale"})
                          @Override
                          public void run() {

                              String[] userLocation = ((String) userLoc.getText()).split(",");
                              double userLatitude = Double.parseDouble(userLocation[0]);
                              double userLongitude = Double.parseDouble(userLocation[1]);
                              double targetLatitude = Double.parseDouble(station_coordinates[0]);
                              double targetLongitude = Double.parseDouble(station_coordinates[1]);
                              double user_distance_from_station = calculate_coordinate_distance(userLatitude, userLongitude, targetLatitude,targetLongitude)  * 0.621371;
                              final ArrayList<String> arrayList  = new ArrayList<>();
                              final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
                              list.setAdapter(adapter);

                            mMap.clear();
                            if (chosen_trains.size() == 0) {
                                Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default");
                                station_marker.showInfoWindow();
                                status.setBackgroundColor(Color.WHITE);

                            } else {
                                boolean green_indicator = false; // get ready to leave
                                boolean yellow_indicator = false; // start to leave
                                boolean blue_indicator = false; // train is approaching station
                                boolean orange_indicator = false; // train has arrived
                                boolean ButtonIsOn = false; // status button
                                int TRAIN_SPEED = 35;
                                float WALK_SPEED = (float) 3.1;
                                int minutes_to_spare;
                                int late_amount;

                                for (HashMap current_train : chosen_trains) {

                                    double current_distance_from_target = Double.parseDouble((String) Objects.requireNonNull(current_train.get("train_to_target"))) * 0.621371;


                                    String main_station_lat = (String) current_train.get("main_lan");
                                    String main_station_lon = (String) current_train.get("main_lon");
                                    String[] train_coord = (current_train.get("train_lat") +","+current_train.get("train_lon")).split(",");
                                    String train_lat = (String) current_train.get("train_lat");
                                    String train_lon = (String) current_train.get("train_lon");
                                    String isApproaching = (String) current_train.get("isApproaching");
                                    String isDelayed = (String) current_train.get("isDelayed");
                                    String next_stop = (String) current_train.get("next_stop");

                                    Marker station_marker = addMarker(station_coordinates[0], station_coordinates[1], station_name, "default");
                                    addMarker(main_station_lat, main_station_lon, (String) current_train.get("main_station"), "main");



                                    int train_min_ETA = (int) ((current_distance_from_target / TRAIN_SPEED)*100) %60;
                                    int train_hour_ETA = (int) ((current_distance_from_target / TRAIN_SPEED)*100) /60;


                                    int user_to_target_ETA =  5;//(int) ((user_distance_from_station/ WALK_SPEED)*100);




                                    int hours = user_to_target_ETA / 60;
                                    int min = user_to_target_ETA % 60;
                                    minutes.setText(hours+ " Hour(s) "+ min+" Minute(s)");
                                    if (!yellow_indicator && !green_indicator && !blue_indicator && !orange_indicator){ // if no train near, show station name
                                        station_marker.showInfoWindow();
                                    }



                                    assert isDelayed != null;
                                    if (isDelayed.equals("1")) { // if current train is delayed
                                        Marker delayed_train = addMarker(train_lat, train_lon, "DELAYED", "rose");
                                        if (train_min_ETA <=0){
                                            double seconds =  ((current_distance_from_target / TRAIN_SPEED)*100);
                                            arrayList.add("TRAIN DELAYED. ETA: "+seconds+" Second(s)");
                                        }else {
                                            arrayList.add("TRAIN DELAYED. ETA: "+train_min_ETA+" Minute(s)");
                                        }
                                        adapter.notifyDataSetChanged();
                                        delayed_train.showInfoWindow();
                                        continue;
                                    }

                                    if (train_min_ETA >= 0 && train_min_ETA <=10){
                                        if (user_to_target_ETA <= train_min_ETA) {
                                            addMarker(train_lat, train_lon, "ETA: " + train_min_ETA, "green");
                                            minutes_to_spare = train_min_ETA - user_to_target_ETA;
                                            Log.e("minutes to spare", "You have "+ minutes_to_spare+" minutes to spare "+ train_min_ETA);
//
                                        }else if (user_to_target_ETA > train_min_ETA){
                                            late_amount = user_to_target_ETA - train_min_ETA;
                                            Log.e("minutes to spare", "You are "+ late_amount+" MINUTES LATE. ");
                                            if (late_amount >=0 && late_amount <=4){
                                                yellow_indicator = true;
                                                addMarker(train_lat, train_lon, "ETA: " + train_min_ETA, "yellow");

                                            }else if (late_amount >=5){
                                                blue_indicator = true;
                                                addMarker(train_lat, train_lon, "ETA: " + train_min_ETA, "blue");

                                            }
                                        }
                                    } else{
                                        Marker regular_marker = addMarker(train_lat, train_lon, "ETA: " + train_min_ETA, station_type);


                                    }





//                                     else if (current_distance_from_target >= 1.9 && current_distance_from_target <= 3.0) {  // get ready to leave
//
//                                        Marker t = addMarker(train_lat, train_lon, "Next Stop: " + next_stop, "green");
//                                        green_indicator = true;
//                                        arrayList.add("Get ready to Leave! ETA: "+ETA+" Minute(s)");
//                                        if (!ButtonIsOn){
//                                            status.setBackgroundColor(Color.GREEN);
//                                        }
//                                            if (!yellow_indicator) {
//                                                t.showInfoWindow();
//                                            }
//                                            continue;
//                                    }
//                                    else {
//                                        assert next_stop != null;
//                                        assert isApproaching != null;
//                                        if (next_stop.equals(station_name) && isApproaching.equals("1")) {
//                                            Marker t = addMarker(train_lat, train_lon, "APPROACHING " + station_name.toUpperCase(), "blue");
//                                            blue_indicator = true;
//
//                                            if (ETA <=0){
//                                                double seconds =  ((current_distance_from_target / TRAIN_SPEED)*100);
//                                                arrayList.add("Is Approaching! ETA: "+String.format("%.1f", seconds*100)+" second(s)");
//                                            }else {
//                                                arrayList.add("Is Approaching! ETA: " + ETA + " Minute(s)");
//                                            }
//                                            adapter.notifyDataSetChanged();
//
//                                            if (!ButtonIsOn) {
//                                                ButtonIsOn=true;
//                                                status.setBackgroundColor(Color.BLUE);
//                                            }
//
//                                            t.showInfoWindow();
//                                            ZoomIn((float) 17, train_coord);
//                                            t.showInfoWindow();
//                                            continue;
//                                        }
//                                        else if (current_distance_from_target >= .1 && current_distance_from_target <= 1.89) {
//                                            Marker t = addMarker(train_lat, train_lon,  String.format("%.2f",current_distance_from_target) + " MILES AWAY FROM " + station_name.toUpperCase(), "yellow");
//                                            yellow_indicator = true;
//
//                                            if (ETA <=0){
//                                                double seconds =  ((current_distance_from_target / TRAIN_SPEED)*100);
//                                                arrayList.add("Start walking! ETA: "+String.format("%.1f", seconds*100)+" second(s)");
//                                            }else {
//                                                arrayList.add("Start walking! ETA: " + ETA + " Minute(s)");
//                                            }
//                                            adapter.notifyDataSetChanged();
//
//                                            if (!ButtonIsOn) {
//                                                ButtonIsOn=true;
//                                                status.setBackgroundColor(Color.YELLOW);
//                                            }
//
//                                            if (!orange_indicator) {
//                                                t.showInfoWindow();
//                                            }
//                                            continue;
//                                    }
//                                    }
//                                    if (current_distance_from_target <=.05){
//                                        station_marker.remove();
//                                        orange_indicator=true;
//                                        Marker ts = addMarker(train_lat, train_lon, "ARRIVED AT "+station_name.toUpperCase(), "orange");
//                                        arrayList.add("ARRIVED.");
//                                       ZoomIn((float) 15, train_coord);
//                                        adapter.notifyDataSetChanged();
//
//                                        if (!ButtonIsOn) {
//                                            ButtonIsOn=true;
//                                            status.setBackgroundColor(Color.rgb(255, 127, 0));
//                                            status.setText("ARRIVED");
//
//                                        }
//                                        ts.showInfoWindow();
//                                        continue;
//                                    }
//                                    else {
//
//                                        addMarker(train_lat, train_lon, "ETA: " + ETA, station_type);
//                                        if (isApproaching.equals("1")){
//                                            arrayList.add("ETA: "+ETA+" Minute(s)");
//                                        }
//                                        else{
//                                            arrayList.add("ETA: "+ETA+" Minute(s)");
//
//
//                                        }
//                                        adapter.notifyDataSetChanged();
//
//
//                                        if (!green_indicator && !yellow_indicator && !blue_indicator && !orange_indicator){
//                                            status.setBackgroundColor(Color.WHITE);
////                                            status.setText("No Train Near.");
//
//                                        }
//                                    }
//                                    adapter.notifyDataSetChanged();
                                }
                    Log.d("DONE", "DONE");

                }
            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  BufferedReader read_station_coordinates(){
        InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
        return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

    }
    private void sleep() throws InterruptedException {
        Thread.sleep(2000);

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
//    Log.e("time", predicted_arrival_time);
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
        return mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(BitmapDescriptorFactory.defaultMarker(TRAIN_COLOR)));


    }
    private void ZoomIn(Float zoomLevel, String[] coord){
        assert coord != null;
        LatLng target = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(zoomLevel)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(90)                  // Sets the tilt of the camera to 40 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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

        // Train threshold to determine if train has passed target station
        return dist1 >= 0 && dist1 <= dist2;
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        return false;
    }
    private void display_map_interface_buttons(){
        final Context context = getApplicationContext();
        disconnect = findViewById(R.id.disconnect);  // Initialize buttons
        list = findViewById(R.id.list);
        test = findViewById(R.id.background);
        chooseStation = findViewById(R.id.pickStation);
        status = findViewById(R.id.status);
        minutes = findViewById(R.id.minutes);
        show = findViewById(R.id.show);
        userLoc = findViewById(R.id.userLoc);
        userLoc.setVisibility(View.GONE);




        show.setBackgroundColor(Color.rgb(133, 205,186)); // set button background color
        disconnect.setBackgroundColor(Color.rgb(133, 205,186));
        chooseStation.setBackgroundColor(Color.rgb(133, 205,186));


        show.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (test.getVisibility() == View.VISIBLE) {
                    show.setText("SHOW");
                    test.setVisibility(View.GONE);
                    chooseStation.setVisibility(View.GONE);
                    switchDir.setVisibility(View.GONE);

                }
                else if (test.getVisibility() != View.VISIBLE) {
                    show.setText("HIDE");
                    test.setVisibility(View.VISIBLE);
                    chooseStation.setVisibility(View.VISIBLE);
                    switchDir.setVisibility(View.VISIBLE);

                }
            }
        });

        chooseStation.setOnClickListener(new View.OnClickListener() {
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




    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
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
                                    userLoc.setText(location.getLatitude()+","+location.getLongitude());


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
            userLoc.setText(mLastLocation.getLatitude()+","+mLastLocation.getLongitude());

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

}