package com.example.cta_map.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.MapMarker;
import com.example.cta_map.Displayers.UserLocation;
import com.example.cta_map.R;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Content_Parser_Thread;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.Notifier_Thread;
import com.example.cta_map.Threading.Train_Estimations_Thread;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapsActivity extends FragmentActivity  implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback{
    private GoogleMap mMap;
    Message message = new Message();

    public HashMap GetTrainColors(){
        final HashMap<String, Integer> colors = new HashMap<>();
        colors.put("blue", Color.BLUE);
        colors.put("red", Color.RED);
        colors.put("orange", Color.rgb(255,165,0));
        colors.put("brown", Color.rgb(165,42,42));
        colors.put("pink", Color.rgb(231, 84, 128));
        colors.put("purple", Color.rgb(128,0,128));
        colors.put("green", Color.rgb(0,255,0));
        colors.put("yellow", Color.rgb(255,255,0));

        return colors;
    }

    public void addTrail(){
        Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record();

        Chicago_Transits chicago_transits = new Chicago_Transits();

        HashMap<String, Integer> colors = GetTrainColors();
        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", tracking_record.get("station_type").toLowerCase());

        PolylineOptions options = new PolylineOptions().width(15).color(colors.get(tracking_record.get("station_type").trim()));
        for (String each_stop : stops) {
            String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, tracking_record.get("station_id"));
            double station_lat = Double.parseDouble(station_coord[0]);
            double station_lon = Double.parseDouble(station_coord[1]);
            LatLng lt = new LatLng(station_lat, station_lon);
            options.add(lt); }
        mMap.addPolyline(options);
        sqlite.close();
    }


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            displayResults(bundle);
        }
    };

    public void displayResults(Bundle bundle){
        mMap.clear();
        final ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");
        ArrayList<HashMap> ignored_trains = (ArrayList<HashMap>) bundle.getSerializable("ignored_trains");
        HashMap<Integer, String> train_etas = new HashMap<>();
        Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record();
        Log.e("track", tracking_record+"");
        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", tracking_record.get("station_type").trim().toLowerCase());
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapMarker mapMarker = new MapMarker(mMap);
        for (HashMap train: chosen_trains){
            Integer eta = (Integer) train.get("train_eta");
            String train_id = (String) train.get("train_id");
            train_etas.put(eta,  train_id);
        }
        Map<Integer, String> map = new TreeMap(train_etas); // nearest train
       addTrail();
       String query = "SELECT latitude, longitude FROM cta_stops WHERE station_name = '"+tracking_record.get("main_station")+"' AND "+tracking_record.get("station_type")+" = 'true'";
        HashMap<String,String> qu = sqlite.search_query(query).get(0);
        String[] main_station_coordinates = new String[]{qu.get("latitude"), qu.get("longitude")};
        Marker target_station_marker = mapMarker.addMarker(tracking_record.get("station_lat"), tracking_record.get("station_lon"), tracking_record.get("station_name"), "default", 1f);
        target_station_marker.showInfoWindow();
        if (main_station_coordinates == null) {
            Toast.makeText(getApplicationContext(), "COULD NOT FIND MAIN STATION", Toast.LENGTH_SHORT).show();
        } else {
            mapMarker.addMarker(main_station_coordinates[0],
                    main_station_coordinates[1],
                    tracking_record.get("main_station"), "cyan", 1f);
        }
        for (HashMap<String, String> train : ignored_trains) {
            String train_lat = train.get("train_lat");
            String train_lon = train.get("train_lon");
            mapMarker.addMarker(train_lat, train_lon, train.get("next_stop"), train.get("station_type"), .5f);
        }
        for (HashMap<String, String> train : chosen_trains) {
            String train_lat = train.get("train_lat");
            String train_lon = train.get("train_lon");
            String eta = String.valueOf(train.get("train_eta"));
            mapMarker.addMarker(train_lat, train_lon, "Next Stop: "+train.get("next_stop")+"| "+eta+"m", train.get("station_type"), 1f);
        }
        Log.e("Nearest", map.entrySet()+"");


    sqlite.close();


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
        ImageView blue, g, red, y,p,brn, pink, org;
        final TextView type_title;
        final Chicago_Transits chicago_transits = new Chicago_Transits();
        final MapMarker mapMarker = new MapMarker(mMap);
        CardView layout = (CardView) findViewById(R.id.map_view);
        Bundle bb = getIntent().getExtras();
        blue = (ImageView) findViewById(R.id.blue_view);
        red = (ImageView) findViewById(R.id.red_view) ;
        g = (ImageView) findViewById(R.id.green_view) ;
        y = (ImageView) findViewById(R.id.yellow_view) ;
        p = (ImageView) findViewById(R.id.purple_view) ;
        pink = (ImageView) findViewById(R.id.pink_view) ;
        org = (ImageView) findViewById(R.id.orange_view) ;
        brn= (ImageView) findViewById(R.id.brown_view) ;
        type_title = (TextView) findViewById(R.id.type_title);
        final String[] type = new String[1];
        final Database2 sqlite= new Database2(getApplicationContext());
        Integer position = bb.getInt("position");
        if (position == 2){
            ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", "red");
            for (String name : line_stops) {
                String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + "red" + " = 'true'";
                String station_id = sqlite.getValue(query);
                String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                mapMarker.addMarker(station_coord[0], station_coord[1], name, "red", 1f);
            }
            chicago_transits.ZoomIn(mMap, (float) 13.3, new String[]{"41.853839", "-87.714842"});
            layout.setVisibility(View.VISIBLE);
            blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "blue";
                    type_title.setText(type[0].toUpperCase());
                    Log.e("CLicked", type[0].toUpperCase()+"");
                    mMap.clear();
                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });
            red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "red";
                    type_title.setText(type[0].toUpperCase());

                    Log.e("CLicked", type[0].toUpperCase()+"");
                    mMap.clear();
                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);





                }
            });
            g.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "green";
                    type_title.setText(type[0].toUpperCase());

                    Log.e("CLicked", type[0].toUpperCase()+"");
                    mMap.clear();
                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "purple";
                    type_title.setText(type[0].toUpperCase());

                    Log.e("CLicked", type[0].toUpperCase()+"");
                    mMap.clear();
                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });
            pink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "pink";
                    type_title.setText(type[0].toUpperCase());

                    Log.e("CLicked", type[0].toUpperCase()+"");
                    mMap.clear();
                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        Log.e("query", query);
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });
            brn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "brown";
                    mMap.clear();
                    type_title.setText(type[0].toUpperCase());

                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        Log.e("CLicked", name+"");

                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "' COLLATE NOCASE" + " AND " + type[0] + " = 'true'";
                        Log.e("query", query);

                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });org.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "orange";
                    Log.e("CLicked", type[0].toUpperCase()+"");
                    mMap.clear();
                    type_title.setText(type[0].toUpperCase());

                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        Log.e("query", query);
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });
           y.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type[0] = "yellow";
                    mMap.clear();
                    type_title.setText(type[0].toUpperCase());

                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
                    for (String name : line_stops) {
                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
                        String station_id = sqlite.getValue(query);
                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
                    }
                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
                    String station_id = sqlite.getValue(query);
                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);

                }
            });
            Log.e("CLickefddfdfd", type[0]+"");



        }else {
//            mMap.setMyLocationEnabled(true); // Enable user location permission
//            mMap.setOnMyLocationButtonClickListener(this);
//            mMap.setOnMyLocationClickListener(this);
            message.keepSending(true);
            message.setClicked(false);
            layout.setVisibility(View.GONE);
            FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
            UserLocation userLocation = new UserLocation(this);
            Switch exit = (Switch) findViewById(R.id.exit_switch);

            final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
            message.setTargetContent(tracking_record);
            String[] target_coordinates = new String[]{tracking_record.get("station_lat"),tracking_record.get("station_lon") };
            chicago_transits.ZoomIn(mMap, (float) 13.3, target_coordinates);

            final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record, handler,true), "API_CALL_Thread");
            final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "Content Parser");
            final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, handler,getApplicationContext(),false), "Estimation Thread");
            final Thread t4 = new Thread(new Notifier_Thread(message, getApplicationContext(), t1,t2,t3,false), "Notifier Thread");
            t4.start();


            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapsActivity.this, TrainTrackingActivity.class);
                    startActivity(intent);
                }
            });


            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String target_station_direction;
                String main_station;
                if (message.getDir() == null) {
                    target_station_direction = tracking_record.get("station_dir");
                    main_station = tracking_record.get("main_station");


                } else {
                    target_station_direction = message.getDir();
                    main_station = message.getMainStation();

                }

                t3.interrupt();
                if (target_station_direction.equals("1")) {
                    Log.e("track", tracking_record.get("tracking_id")+"");
                    target_station_direction = "5";
                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "station_dir", target_station_direction);
                    String query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+tracking_record.get("station_type").toUpperCase().trim()+"'";
                    main_station = sqlite.getValue(query);
                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
                    tracking_record.put("main_station",main_station );
                    tracking_record.put("station_dir", target_station_direction);

                        message.setDir(target_station_direction);
                        message.setMainStation(main_station);
                        message.setClicked(true);
                } else {
                    Log.e("track", tracking_record.get("tracking_id")+"");
                    target_station_direction = "1";
                    String query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";
                    main_station = sqlite.getValue(query);
                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
                    tracking_record.put("main_station", main_station);
                    tracking_record.put("station_dir", target_station_direction);
                        message.setDir(target_station_direction);
                        message.setMainStation(main_station);
                        message.setClicked(true);
                }


                }
            });






            sqlite.close();








        }












//        mMap.setMyLocationEnabled(true); // Enable user location permission
//        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);
//        message.keepSending(true);
//        message.setClicked(false);
//
//        final Database2 sqlite = new Database2(getApplicationContext());
//        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
//
//        message.setTargetContent(tracking_record);
//        String[] target_coordinates = new String[]{tracking_record.get("station_lat"),tracking_record.get("station_lon") };
//        chicago_transits.ZoomIn(mMap, (float) 13.3, target_coordinates);
//        if (tracking_record == null || tracking_record.isEmpty()){
//            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        Log.e("record", tracking_record+"");
//        message.setClicked(false);
//        message.keepSending(true);
//        message.setTargetContent(tracking_record);
////        final Button switch_direction = (Button) findViewById(R.id.switch_direction);
////
////        final Button choose_station = (Button) findViewById(R.id.pickStation);
////        final Button toArrival = (Button) findViewById(R.id.show);
//
//        UserLocation userLocation = new UserLocation(this);
//        userLocation.getLastLocation(getApplicationContext());
//
//        final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record, handler,true), "API_CALL_Thread");
//        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "Content Parser");
//        final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, handler,getApplicationContext(),false), "Estimation Thread");
//        final Thread t4 = new Thread(new Notifier_Thread(message, getApplicationContext(), t1,t2,t3,false), "Notifier Thread");

//
//
////        t4.start();
//sqlite.close();


//        toArrival.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                t3.interrupt();
//                Intent intent = new Intent(MapsActivity.this, TrainTrackingActivity.class);
//
//
//                synchronized (message){
//                    message.keepSending(false);
//                }
//
//
//                startActivity(intent);
//            }
//        });
//
//
//
//        choose_station.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                t3.interrupt();
//                Intent intent = new Intent(MapsActivity.this, mainactivity.class);
//                Integer profile_id = Integer.parseInt(tracking_record.get("profile_id"));
//                final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+profile_id+"'");
//                intent.putExtra("profile_id", user_record.get(0));
//                synchronized (message){
//                    message.keepSending(false);
//                }
//
//                startActivity(intent);
//
//
//            }
//        });
//
//
//        switch_direction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String target_station_direction;
//                String main_station;
//                if (message.getDir() == null) {
//                    target_station_direction = tracking_record.get("station_dir");
//                    main_station = tracking_record.get("main_station");
//
//
//                } else {
//                    target_station_direction = message.getDir();
//                    main_station = message.getMainStation();
//
//                }
//
//                t3.interrupt();
//                if (target_station_direction.equals("1")) {
//                    target_station_direction = "5";
//                    String query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+tracking_record.get("station_type").toUpperCase()+"'";
//                    main_station = sqlite.getValue(query);
//                    Log.e("MAIN", main_station+"");
//                    tracking_record.put("main_station",main_station );
//                    tracking_record.put("station_dir", target_station_direction);
//                    synchronized (message){
//                        message.setDir(target_station_direction);
//                        message.setMainStation(main_station);
//                        message.setClicked(true);
//                        message.notifyAll();
//                    }
//                } else {
//                    target_station_direction = "1";
//                    String query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase() + "'";
//                    main_station = sqlite.getValue(query);
//                    Log.e("MAIN", main_station+"");
//
//                    tracking_record.put("main_station", main_station);
//                    tracking_record.put("station_dir", target_station_direction);
//
//                    synchronized (message){
//                        message.setDir(target_station_direction);
//                        message.setMainStation(main_station);
//                        message.setClicked(true);
//                        message.notifyAll();
//                    }
//                }
//            }
//        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}

