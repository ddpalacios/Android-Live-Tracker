package com.example.cta_map.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.MapMarker;
import com.example.cta_map.Displayers.UserLocation;
import com.example.cta_map.R;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Backend.Threading.Notifier_Thread;
import com.example.cta_map.Backend.Threading.Train_Estimations_Thread;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback{
    private GoogleMap mMap;
    String TAG  = "MAPS ACTIVITY";
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
        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", tracking_record.get("station_type").toLowerCase().trim());

        PolylineOptions options = new PolylineOptions().width(15).color(colors.get(tracking_record.get("station_type").trim()));
        for (String each_stop : stops) {
            if (each_stop.equals("O'Hare")){
                each_stop  = each_stop.replaceAll("[^0-9a-zA-Z]", "");
            }
            String query = "SELECT station_id FROM cta_stops WHERE station_name = '"+each_stop+"' AND "+tracking_record.get("station_type").trim().toLowerCase() +" = 'true'";
            String id = sqlite.getValue(query);
            String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, id);
            double station_lat = Double.parseDouble(station_coord[0]);
            double station_lon = Double.parseDouble(station_coord[1]);
            LatLng lt = new LatLng(station_lat, station_lon);
            options.add(lt);
        }
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
        MapMarker mapMarker = new MapMarker(mMap);
        Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
        final HashMap<String, ArrayList<HashMap>> estimated_train_data = (HashMap<String, ArrayList<HashMap>>) bundle.getSerializable("estimated_train_data");
        Log.e(Thread.currentThread().getName(), estimated_train_data+" ");
addTrail();

        for (HashMap<String, String> train : estimated_train_data.get("chosen_trains")) {
            String train_lat = train.get("train_lat");
            String train_lon = train.get("train_lon");
            String eta = String.valueOf(train.get("train_eta"));
            mapMarker.addMarker(train_lat, train_lon, "Next Stop: "+train.get("next_stop")+"| "+eta+"m", train.get("station_type"), 1f);
        }

        for (HashMap<String, String> train : estimated_train_data.get("ignored")) {
            String train_lat = train.get("train_lat");
            String train_lon = train.get("train_lon");
            String eta = String.valueOf(train.get("train_eta"));
            mapMarker.addMarker(train_lat, train_lon, "Next Stop: "+train.get("next_stop")+"| "+eta+"m", train.get("station_type"), .5f);
        }

        Marker target_station_marker = mapMarker.addMarker(tracking_record.get("station_lat"), tracking_record.get("station_lon"), tracking_record.get("station_name"), "default", 1f);
        target_station_marker.showInfoWindow();

        String query = "SELECT latitude, longitude FROM cta_stops WHERE station_name = '"+tracking_record.get("main_station")+"' AND "+tracking_record.get("station_type")+" = 'true'";
        HashMap<String,String> qu = sqlite.search_query(query).get(0);
        String[] main_station_coordinates = new String[]{qu.get("latitude"), qu.get("longitude")};
        mapMarker.addMarker(main_station_coordinates[0],
                main_station_coordinates[1],
                tracking_record.get("main_station"), "cyan", 1f);


        sqlite.close();



//        HashMap<Integer, String> train_etas = new HashMap<>();
//        Database2 sqlite = new Database2(getApplicationContext());
//        final HashMap<String, String> tracking_record = sqlite.get_tracking_record();
//        Log.e("track", tracking_record+"");
//        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", tracking_record.get("station_type").trim().toLowerCase());
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        MapMarker mapMarker = new MapMarker(mMap);
//        for (HashMap train: chosen_trains){
//            Integer eta = (Integer) train.get("train_eta");
//            String train_id = (String) train.get("train_id");
//            train_etas.put(eta,  train_id);
//        }
//        Map<Integer, String> map = new TreeMap(train_etas); // nearest train
//       String query = "SELECT latitude, longitude FROM cta_stops WHERE station_name = '"+tracking_record.get("main_station")+"' AND "+tracking_record.get("station_type")+" = 'true'";
//        HashMap<String,String> qu = sqlite.search_query(query).get(0);
//        String[] main_station_coordinates = new String[]{qu.get("latitude"), qu.get("longitude")};
//        Marker target_station_marker = mapMarker.addMarker(tracking_record.get("station_lat"), tracking_record.get("station_lon"), tracking_record.get("station_name"), "default", 1f);
//        target_station_marker.showInfoWindow();
//        if (main_station_coordinates == null) {
//            Toast.makeText(getApplicationContext(), "COULD NOT FIND MAIN STATION", Toast.LENGTH_SHORT).show();
//        } else {
//            mapMarker.addMarker(main_station_coordinates[0],
//                    main_station_coordinates[1],
//                    tracking_record.get("main_station"), "cyan", 1f);
//        }
//        for (HashMap<String, String> train : ignored_trains) {
//            String train_lat = train.get("train_lat");
//            String train_lon = train.get("train_lon");
//            mapMarker.addMarker(train_lat, train_lon, train.get("next_stop"), train.get("station_type"), .5f);
//        }
//        for (HashMap<String, String> train : chosen_trains) {
//            String train_lat = train.get("train_lat");
//            String train_lon = train.get("train_lon");
//            String eta = String.valueOf(train.get("train_eta"));
//            mapMarker.addMarker(train_lat, train_lon, "Next Stop: "+train.get("next_stop")+"| "+eta+"m", train.get("station_type"), 1f);
//        }
//        Log.e("Nearest", map.entrySet()+"");
//
//
//    sqlite.close();
//

    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
       actionBar.setTitle("Live Map Viewer");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Button options_btn = (Button) findViewById(R.id.options_btn);
        options_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, PopUp.class));

            }
        });




    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            message.keepSending(false);
            Log.e(TAG, "DONE");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


































//        CardView layout = (CardView) findViewById(R.id.map_view);
//        Bundle bb = getIntent().getExtras();
//        blue = (ImageView) findViewById(R.id.blue_view);
//        red = (ImageView) findViewById(R.id.red_view) ;
//        g = (ImageView) findViewById(R.id.green_view) ;
//        y = (ImageView) findViewById(R.id.yellow_view) ;
//        p = (ImageView) findViewById(R.id.purple_view) ;
//        pink = (ImageView) findViewById(R.id.pink_view) ;
//        org = (ImageView) findViewById(R.id.orange_view) ;
//        brn= (ImageView) findViewById(R.id.brown_view) ;
//        type_title = (TextView) findViewById(R.id.type_title);
//        final String[] type = new String[1];
//        final Database2 sqlite= new Database2(getApplicationContext());
//        Integer position = bb.getInt("position");
//        if (position == 2){
//            ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", "red");
//            for (String name : line_stops) {
//                String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + "red" + " = 'true'";
//                String station_id = sqlite.getValue(query);
//                String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                mapMarker.addMarker(station_coord[0], station_coord[1], name, "red", 1f);
//            }
//            chicago_transits.ZoomIn(mMap, (float) 13.3, new String[]{"41.853839", "-87.714842"});
//            layout.setVisibility(View.VISIBLE);
//            blue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "blue";
//                    type_title.setText(type[0].toUpperCase());
//                    Log.e("CLicked", type[0].toUpperCase()+"");
//                    mMap.clear();
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });
//            red.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "red";
//                    type_title.setText(type[0].toUpperCase());
//
//                    Log.e("CLicked", type[0].toUpperCase()+"");
//                    mMap.clear();
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//
//
//
//
//                }
//            });
//            g.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "green";
//                    type_title.setText(type[0].toUpperCase());
//
//                    Log.e("CLicked", type[0].toUpperCase()+"");
//                    mMap.clear();
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });
//            p.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "purple";
//                    type_title.setText(type[0].toUpperCase());
//
//                    Log.e("CLicked", type[0].toUpperCase()+"");
//                    mMap.clear();
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });
//            pink.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "pink";
//                    type_title.setText(type[0].toUpperCase());
//
//                    Log.e("CLicked", type[0].toUpperCase()+"");
//                    mMap.clear();
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        Log.e("query", query);
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });
//            brn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "brown";
//                    mMap.clear();
//                    type_title.setText(type[0].toUpperCase());
//
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        Log.e("CLicked", name+"");
//
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "' COLLATE NOCASE" + " AND " + type[0] + " = 'true'";
//                        Log.e("query", query);
//
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });org.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "orange";
//                    Log.e("CLicked", type[0].toUpperCase()+"");
//                    mMap.clear();
//                    type_title.setText(type[0].toUpperCase());
//
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        Log.e("query", query);
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });
//           y.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type[0] = "yellow";
//                    mMap.clear();
//                    type_title.setText(type[0].toUpperCase());
//
//                    ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", type[0]);
//                    for (String name : line_stops) {
//                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + name + "'" + " AND " + type[0] + " = 'true'";
//                        String station_id = sqlite.getValue(query);
//                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                        mapMarker.addMarker(station_coord[0], station_coord[1], name, type[0], 1f);
//                    }
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + line_stops.get(0) + "'" + " AND " + type[0] + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    chicago_transits.ZoomIn(mMap, (float) 13.3, station_coord);
//
//                }
//            });
//            Log.e("CLickefddfdfd", type[0]+"");
//
//
//
//        }else {
//            layout.setVisibility(View.INVISIBLE);
//            Switch s1 = (Switch) findViewById(R.id.exit_switch);
//            FloatingActionButton switch_dir = (FloatingActionButton) findViewById(R.id.switch_dir);
//            UserLocation userLocation = new UserLocation(this);
//            final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
//            String[] target_coordinates = new String[]{tracking_record.get("station_lat"),tracking_record.get("station_lon") };
//            chicago_transits.ZoomIn(mMap, (float) 13.3, target_coordinates);
//
//
//            if (tracking_record == null || tracking_record.isEmpty()){
//                Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//
//            message.keepSending(true);
//
////            final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record,false), "API_CALL_Thread");
////            final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record,handler , sqlite, false), "Content Parser");
////            final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, tracking_record,handler,getApplicationContext(),false), "Estimation Thread");
////            final Thread t4 = new Thread(new Notifier_Thread(t1,t2,t3), "Notifier Thread");
////            t4.start();
////        sqlite.close();
//
//
//
//
//
//
//            s1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MapsActivity.this, TrainTrackingActivity.class);
//                    intent.putExtra("position", 1);
//                    message.keepSending(false);
//                    startActivity(intent);
//                }
//            });
//
//
//            switch_dir.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String query;
//                    Log.e(Thread.currentThread().getName(), tracking_record.get("station_dir")+"");
//                    if (tracking_record.get("station_dir").equals("1")){
//                        tracking_record.put("station_dir", "5");
//                        query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";
//
//                    }else{
//                        tracking_record.put("station_dir", "1");
//                        query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";
//
//                    }
//
//                    String main_station = sqlite.getValue(query);
//                    if (main_station.equals("O'Hare")){
//                        main_station  = main_station.replaceAll("[^0-9a-zA-Z]", "");
//                    }
//
//                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
//                    tracking_record.put("main_station", main_station);
//
////                    t3.interrupt();
//
//                }
//            });
//        }
//
//
//
//
//
//
//
//
//
//
//
//
////        mMap.setMyLocationEnabled(true); // Enable user location permission
////        mMap.setOnMyLocationButtonClickListener(this);
////        mMap.setOnMyLocationClickListener(this);
////        message.keepSending(true);
////        message.setClicked(false);
////
////        final Database2 sqlite = new Database2(getApplicationContext());
////        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
////
////        message.setTargetContent(tracking_record);
////        String[] target_coordinates = new String[]{tracking_record.get("station_lat"),tracking_record.get("station_lon") };
////        chicago_transits.ZoomIn(mMap, (float) 13.3, target_coordinates);
////        if (tracking_record == null || tracking_record.isEmpty()){
////            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
////            return;
////        }
////
////        Log.e("record", tracking_record+"");
////        message.setClicked(false);
////        message.keepSending(true);
////        message.setTargetContent(tracking_record);
//////        final Button switch_direction = (Button) findViewById(R.id.switch_direction);
//////
//////        final Button choose_station = (Button) findViewById(R.id.pickStation);
//////        final Button toArrival = (Button) findViewById(R.id.show);
////
////        UserLocation userLocation = new UserLocation(this);
////        userLocation.getLastLocation(getApplicationContext());
////
////        final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record, handler,true), "API_CALL_Thread");
////        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "Content Parser");
////        final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, handler,getApplicationContext(),false), "Estimation Thread");
////        final Thread t4 = new Thread(new Notifier_Thread(message, getApplicationContext(), t1,t2,t3,false), "Notifier Thread");
//
////
////
//////        t4.start();
////sqlite.close();
//
//
////        toArrival.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                t3.interrupt();
////                Intent intent = new Intent(MapsActivity.this, TrainTrackingActivity.class);
////
////
////                synchronized (message){
////                    message.keepSending(false);
////                }
////
////
////                startActivity(intent);
////            }
////        });
////
////
////
////        choose_station.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                t3.interrupt();
////                Intent intent = new Intent(MapsActivity.this, mainactivity.class);
////                Integer profile_id = Integer.parseInt(tracking_record.get("profile_id"));
////                final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+profile_id+"'");
////                intent.putExtra("profile_id", user_record.get(0));
////                synchronized (message){
////                    message.keepSending(false);
////                }
////
////                startActivity(intent);
////
////
////            }
////        });
////
////
////        switch_direction.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String target_station_direction;
////                String main_station;
////                if (message.getDir() == null) {
////                    target_station_direction = tracking_record.get("station_dir");
////                    main_station = tracking_record.get("main_station");
////
////
////                } else {
////                    target_station_direction = message.getDir();
////                    main_station = message.getMainStation();
////
////                }
////
////                t3.interrupt();
////                if (target_station_direction.equals("1")) {
////                    target_station_direction = "5";
////                    String query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+tracking_record.get("station_type").toUpperCase()+"'";
////                    main_station = sqlite.getValue(query);
////                    Log.e("MAIN", main_station+"");
////                    tracking_record.put("main_station",main_station );
////                    tracking_record.put("station_dir", target_station_direction);
////                    synchronized (message){
////                        message.setDir(target_station_direction);
////                        message.setMainStation(main_station);
////                        message.setClicked(true);
////                        message.notifyAll();
////                    }
////                } else {
////                    target_station_direction = "1";
////                    String query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase() + "'";
////                    main_station = sqlite.getValue(query);
////                    Log.e("MAIN", main_station+"");
////
////                    tracking_record.put("main_station", main_station);
////                    tracking_record.put("station_dir", target_station_direction);
////
////                    synchronized (message){
////                        message.setDir(target_station_direction);
////                        message.setMainStation(main_station);
////                        message.setClicked(true);
////                        message.notifyAll();
////                    }
////                }
////            }
////        });

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}

