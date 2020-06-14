package com.example.cta_map.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.MapMarker;
import com.example.cta_map.R;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Content_Parser_Thread;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.Notifier_Thread;
import com.example.cta_map.Threading.Train_Estimations_Thread;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity  implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback{
    final boolean[] connect = {true};
    private GoogleMap mMap;
    List<String> ignored_stations;
    Message message = new Message();
    ArrayList<Integer> train_etas = new ArrayList<>();
    ArrayList<HashMap> chosen_trains = new ArrayList<>();
    Bundle bb;


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");
            ArrayList<HashMap> ignored_trains = (ArrayList<HashMap>) bundle.getSerializable("ignored_trains");



            displayResults(chosen_trains, ignored_trains);
        }
    };


    public void displayResults(ArrayList<HashMap> chosen_trains, ArrayList<HashMap> ignored_stations){
                MapMarker mapMarker = new MapMarker(mMap);
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");



                mMap.clear();

                Marker target_station = mapMarker.addMarker(tracking_record.get("station_lat"), tracking_record.get("station_lon"), tracking_record.get("station_name"), "default", 1f);
                target_station.showInfoWindow();

//                for (HashMap<String, String> train: ignored_stations){
//                    String train_lat = train.get("train_lat");
//                    String train_lon = train.get("train_lon");
//                    mapMarker.addMarker(train_lat, train_lon, train.get("next_stop"), train.get("station_type"), 1f);
//
//                }


                for (HashMap<String, String> train: chosen_trains){
                    String train_lat = train.get("train_lat");
                    String train_lon = train.get("train_lon");
                    mapMarker.addMarker(train_lat, train_lon, train.get("next_stop"), train.get("station_type"), .4f);

                }


        sqlite.close();




//        MapMarker mapMarker = new MapMarker(mMap);
//        DatabaseHelper sqlite= new DatabaseHelper(getApplicationContext());
//        String target_station_name = bb.getString("station_name");
//        String target_station_type = bb.getString("station_type");
//        String main_station = null;
//        mMap.clear();
//
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        String[] target_station_coordinates = bb.getStringArray("target_station_coordinates");
//        String station_dir = bb.getString("station_dir");
//        ArrayList<String> main_station_record = sqlite.get_table_record("main_stations_table", "WHERE train_line ='"+target_station_type+"'");
//
//
//        if (station_dir.equals("1")){
//            main_station = main_station_record.get(2);
//
//        }else if (station_dir.equals("5")){
//            main_station = main_station_record.get(3);
//        }
//
//        String[] main_station_coordinates = chicago_transits.retrieve_station_coordinates(sqlite, main_station, target_station_type);
//
//
//        Marker target_marker = mapMarker.addMarker(target_station_coordinates[0], target_station_coordinates[1], target_station_name, "default", (float) 1.0);
//        Marker main_marker = mapMarker.addMarker(main_station_coordinates[0], main_station_coordinates[1], main_station, "cyan", (float) 1.0);
//        for (int i=0; i < chosen_trains.size(); i++){
//
//            HashMap<String, String> current_train = chosen_trains.get(i);
//
//            String station_lat = current_train.get("train_lat");
//            String station_lon = current_train.get("train_lon");
//
//
//            Log.e("recived", "Chosen: "+ station_lat+" "+station_lon);
//            mapMarker.addMarker(station_lat, station_lon, current_train.get("next_stop"), target_station_type, (float) 1f);
//
//
//
//        }
//
//        for (int i=0; i < ignored_stations.size(); i++){
//
//            HashMap<String, String> current_train = ignored_stations.get(i);
//
//            String station_lat = current_train.get("train_lat");
//            String station_lon = current_train.get("train_lon");
//
//
//            mapMarker.addMarker(station_lat, station_lon, current_train.get("next_stop"), target_station_type, .5f);
//
//
//
//        }
//
//
//


    }



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
        mMap = googleMap;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapMarker mapMarker = new MapMarker(mMap);
        mMap.setMyLocationEnabled(true); // Enable user location permission
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        message.keepSending(true);
        message.setClicked(false);

        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");
        message.setTargetContent(tracking_record);
        String[] target_coordinates = new String[]{tracking_record.get("station_lat"),tracking_record.get("station_lon") };
        chicago_transits.ZoomIn(mMap, (float) 13.3, target_coordinates);
        if (tracking_record == null || tracking_record.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("record", tracking_record+"");
        message.setClicked(false);
        message.keepSending(true);
        message.setTargetContent(tracking_record);
        final Button switch_direction = (Button) findViewById(R.id.switch_direction);

        final Button choose_station = (Button) findViewById(R.id.pickStation);
        final Button toArrival = (Button) findViewById(R.id.show);




        Thread api_call = new Thread(new API_Caller_Thread(message, tracking_record, false), "api caller");
        api_call.start();

        Thread content_parser = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "parser");
        content_parser.start();


        Thread train_estimations = new Thread(new Train_Estimations_Thread(message, false), "estimations");
        train_estimations.start();

        final Thread notifier = new Thread(new Notifier_Thread(message, handler, getApplicationContext(), true), "notifier");
        notifier.start();



        toArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifier.interrupt();
                Intent intent = new Intent(MapsActivity.this, TrainTrackingActivity.class);


                synchronized (message){
                    message.keepSending(false);
                }


                startActivity(intent);
            }
        });



        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifier.interrupt();
                Intent intent = new Intent(MapsActivity.this, mainactivity.class);
                Integer profile_id = Integer.parseInt(tracking_record.get("profile_id"));
                final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+profile_id+"'");
                intent.putExtra("profile_id", user_record.get(0));
                synchronized (message){
                    message.keepSending(false);
                }

                startActivity(intent);


            }
        });


        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target_station_direction;
                if (message.getDir() ==null) {
                    target_station_direction = tracking_record.get("station_dir");
                }else{
                    target_station_direction = message.getDir();

                }

                notifier.interrupt();
                if (target_station_direction.equals("1")){
                    target_station_direction = "5";
                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }else {
                    target_station_direction = "1";
                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }

            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
//        Button hide = (Button) findViewById(R.id.show);
//        Button choose_station = (Button) findViewById(R.id.pickStation);
//        mMap = googleMap;
//        mMap.setMyLocationEnabled(true); // Enable user location permission
//        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);
//        final MapMarker mapMarker = new MapMarker(mMap);
//        final Chicago_Transits chicago_transits = new Chicago_Transits();
//        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
//        SharedPreferences TRAIN_RECORD = getSharedPreferences("Train_Record", MODE_PRIVATE);
//
//        final String target_station_type = TRAIN_RECORD.getString("station_type", null);//bb.getString("target_station_type");
//        final String target_station_name =TRAIN_RECORD.getString("station_name", null);
//        final String[] specified_train_direction = new String[]{String.valueOf(TRAIN_RECORD.getInt("station_dir", 1)) };//{bb.getString("train_direction")};
//        BufferedReader train_station_csv_reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
//        final String[] target_station_coordinates = new String[]{String.valueOf(TRAIN_RECORD.getFloat("station_lat", 0)), String.valueOf(TRAIN_RECORD.getFloat("station_lon", 0)) };
//        final ArrayList<String> stops = chicago_transits.retrieve_line_stations(chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops), target_station_type, false);
//        chicago_transits.ZoomIn(mMap, (float) 13.3, target_station_coordinates);
//        Log.e("stops", stops+"");
//        final Context context = getApplicationContext();
//        final Intent intent = new Intent(MapsActivity.this, mainactivity.class);



//        hide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                connect[0]= false;
//                Intent intent = new Intent(MapsActivity.this,TrainTrackingActivity.class);
//                intent.putExtra("target_station_type", target_station_type);
//                intent.putExtra("target_station_name", target_station_name);
//                intent.putExtra("train_direction", specified_train_direction[0]);
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                startActivity(intent);
//            }
//        });
//
//        choose_station.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                connect[0] = false;
//                Intent intent = new Intent(MapsActivity.this,mainactivity.class);
//                intent.putExtra("target_station_type", target_station_type);
//                intent.putExtra("target_station_name", target_station_name);
//                intent.putExtra("train_direction", specified_train_direction[0]);
//
//
//                startActivity(intent);
//
//            }
//        });
//
//        final HashMap<String, Integer> colors = new HashMap<>();
//        colors.put("blue", Color.BLUE);
//        colors.put("red", Color.RED);
//        colors.put("orange", Color.rgb(255,165,0));
//        colors.put("brown", Color.rgb(165,42,42));
//        colors.put("pink", Color.rgb(231, 84, 128));
//        colors.put("purple", Color.rgb(128,0,128));
//        colors.put("green", Color.rgb(0,255,0));
//        colors.put("yellow", Color.rgb(255,255,0));
//
//
//        assert target_station_type != null;
//        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(target_station_type.toLowerCase()));
//        Log.e("url", url);
//        /*
//          Everything is being ran within its own thread.
//         This allows us to run our continuous web extraction
//         while also performing other user interactions
//          */
//        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (connect[0]){
//
//                    try {
//                        Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
//                        final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
//                        runOnUiThread(new Runnable() {
//                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                            @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale", "WrongConstant", "ShowToast", "NewApi"})
//                            @Override
//                            public void run() {
//                                mMap.clear();
//                                PolylineOptions options = new PolylineOptions().width(15).color(colors.get(target_station_type));
//                                for (String each_stop : stops) {
//                                    BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_stations);
//                                    String[] station_coord = chicago_transits.retrieve_station_coordinates(reader, each_stop, target_station_type);
//                                        double station_lat = Double.parseDouble(station_coord[0]);
//                                        double station_lon = Double.parseDouble(station_coord[1]);
//                                        LatLng lt = new LatLng(station_lat, station_lon);
//                                        options.add(lt);
//
//                                }
//                                mMap.addPolyline(options);
//
//                                mapMarker.addMarker(target_station_coordinates[0], target_station_coordinates[1], target_station_name, "default", 1f).showInfoWindow();
//                                for (String each_train : train_list) {
//                                    // prepare each train as a map
//                                    HashMap<String, String> train_info = chicago_transits.get_train_info(chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations), each_train,target_station_name ,target_station_type);
//
//                                    int start = 0;
//                                   int end =0;
//                                    if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
//                                        train_info.put("target_station_lat", target_station_coordinates[0]);
//                                        train_info.put("target_station_lon", target_station_coordinates[1]);
//                                        mapMarker.addMarker(train_info.get("main_lat"), train_info.get("main_lon"),train_info.get("main_station"), "cyan", 1f);
//                                        if (specified_train_direction[0].equals("1")){
//                                            end = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", ""));
//
//                                        }else if (specified_train_direction[0].equals("5")){
//                                            start = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", "")) + 1;
//                                            end = stops.size();
//
//                                        }
//                                        setup_train_direction(train_info, stops, start, end, Integer.parseInt(specified_train_direction[0]), getApplicationContext());
//                                    }
//                                }
//                                Log.d("Update", "DONE.");
//                            }
//
//                        });
//                        train_etas.clear();
//                        chosen_trains.clear();
//                        Thread.sleep(10000);
//
//                    } catch (IOException | InterruptedException e) {
//                        Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @SuppressLint("MissingPermission")
//    private Button initiate_button(int widget) {
//        Button button = findViewById(widget);
//        button.setBackgroundColor(Color.rgb(133, 205, 186));
//        return button;
//    }
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onResume() {
//        super.onResume();
//        Context context = getApplicationContext();
//        final UserLocation userLocation = new UserLocation(context);
//        if (userLocation.checkPermissions()) {
//            Intent intent = new Intent(MapsActivity.this,ChooseDirectionActivity.class);
////            userLocation.getLastLocation(intent, mMap, null, null, null, null, context);
//        }
//    }
//    @Override
//    public boolean onMyLocationButtonClick() {
//        return false;
//    }
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//
//    }
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context) {
//        MapMarker mapMarker = new MapMarker(mMap);
//        Intent intent = new Intent(MapsActivity.this,mainactivity.class);
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        UserLocation userLocation = new UserLocation(context);
//        BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
//        String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(reader, current_train_info.get("target_station"), current_train_info.get("station_type"));
//        Time times = new Time();
//
//        ignored_stations = stops.subList(start, end);
//        String next_stop = Objects.requireNonNull(current_train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", ""));
//
//        if (ignored_stations.contains(next_stop)) {
//            Log.e("ignored", ignored_stations+" " +next_stop+"");
//            mapMarker.addMarker(current_train_info.get("train_lat"), current_train_info.get("train_lon"), current_train_info.get("next_stop"), current_train_info.get("station_type"), .5f);
//
//        }else {
//            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lon"))));
//            int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
//            train_etas.add(current_train_eta);
//            Collections.sort(train_etas);
//            chosen_trains.add(current_train_info);
//            current_train_info.put(String.valueOf(current_train_eta), next_stop);
//            Boolean[] t = {false};
//            Boolean[] y = {false};
//            Boolean[] p = {false};
//            userLocation.getLastLocation(intent, context, current_train_info, current_train_eta, null,null,null, mMap, true);
//        }
//    }
}

