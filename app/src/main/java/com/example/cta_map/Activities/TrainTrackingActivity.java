package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.BottomTrackingAdapter;
import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.UserLocation;
import com.example.cta_map.LineAdapter;
import com.example.cta_map.R;
import com.example.cta_map.StationLines;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Content_Parser_Thread;
import com.example.cta_map.Threading.Notifier_Thread;
import com.example.cta_map.Threading.Train_Estimations_Thread;
import com.example.cta_map.TrackingAdapter;
import com.example.cta_map.Tracking_Station;
import com.example.cta_map.Train_info;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TrainTrackingActivity extends AppCompatActivity {

    Bundle bb; // Retrieve data from main screen
    final Message message = new Message();


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
//        final ListView list = findViewById(R.id.train_layout_arrival_times);
//        ArrayList<String> arrayList = new ArrayList<>();
//        Database2 sqlite = new Database2(getApplicationContext());
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
//        list.setAdapter(adapter);
//        HashMap<Integer, String> train_etas = new HashMap<>();

        final ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");
        Log.e("chosen", chosen_trains+"");
        ArrayList<Tracking_Station> list = new ArrayList<>();
        ArrayList<Train_info> list2 = new ArrayList<>();
        Time time = new Time();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");



        RecyclerView line_layout = (RecyclerView) findViewById(R.id.vr_recycler_view);
        RecyclerView bottom_layout = (RecyclerView) findViewById(R.id.hr_recycler_view);

        for (HashMap t: chosen_trains){

            list.add(new Tracking_Station("  #"+t.get("train_id")+". To "+t.get("main_station"), t.get("train_eta")+""));

            String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + chosen_trains.get(0).get("next_stop").toString().trim() + "'" + " AND " + chosen_trains.get(0).get("station_type").toString().trim() + " = 'true'";
            String station_id = sqlite.getValue(query);
            Log.e("ID", station_id+"dd");
            String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);

            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                    Double.parseDouble((String) t.get("train_lat")),
                    Double.parseDouble((String) t.get("train_lon")),
                    Double.parseDouble(station_coord[0]),
                    Double.parseDouble(station_coord[1]));
            int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);



            list2.add(new Train_info("Next Stop: "+t.get("next_stop")+"",
                            ""+current_train_eta+"m",
                        String.format("%.2f",current_train_distance_from_target_station)+" mi",
                    "To "+tracking_record.get("station_name")+" (target)",
                    t.get("train_eta")+"m",
                    String.format("%.2f",t.get("train_distance"))+" mi", "#"+t.get("train_id")+""));
        }


        BottomTrackingAdapter bottomTrackingAdapter = new BottomTrackingAdapter(getApplicationContext(), list2);
        bottom_layout.setAdapter(bottomTrackingAdapter);
        bottom_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        TrackingAdapter adapter = new TrackingAdapter(getApplicationContext(), list);
        line_layout.setAdapter(adapter);
        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));





//        final HashMap<String, String> tracking_record = (HashMap<String, String>) bundle.getSerializable("target_record");
////        Log.e("chosen", chosen_trains+"");
//        for (HashMap train: chosen_trains){
//            Integer eta = (Integer) train.get("train_eta");
//            String train_id = (String) train.get("train_id");
//            train_etas.put(eta,  train_id);
//        }
//        int idx = 0;
//        Map<Integer, String> map = new TreeMap(train_etas);
//        String nearest_train_id = null;
//        arrayList.add(0,tracking_record.get("station_name") +" to "+ sqlite.get_tracking_record().get("main_station"));
//        for (Map.Entry<Integer, String> entry : map.entrySet()) {
//            Integer key = entry.getKey();
//            String value = entry.getValue();
//            idx+=1;
//            if (idx ==1){
//                nearest_train_id = value;
//            }
//            arrayList.add("Train #"+value+": "+ key+"m");
//        }

//        for (HashMap train: chosen_trains){
//           if (train.get("train_id").equals(nearest_train_id)){
//               Integer user_eta =  3;//Integer.parseInt(tracking_record.get("user_eta"));
//               Integer train_eta = (Integer) train.get("train_eta");
//
//               if (user_eta <=train_eta ){
//                   int time_to_spare =train_eta - user_eta;
//                   Log.e("fff", "You have "+time_to_spare+"m to spare");
//
//               }else{
//                   int time_to_spare =train_eta - user_eta;
//                   if (time_to_spare < 0){
//                       int late = time_to_spare *-1;
//                       String next_stop = train.get("next_stop").toString().replaceAll("[^0-9a-zA-Z]+", "").toLowerCase();
//                       String target_station = tracking_record.get("station_name").replaceAll("[^0-9a-zA-Z]+", "").toLowerCase();
//                       if (train.get("isApproaching").equals("1") && next_stop.equals(target_station)){
//                           Log.e("late", "You are "+late+"m late. Train is approaching. Try to make this train or choose next train");
//
//                       }else{
//                           Log.e("late", "You are "+late+"m late");
//                       }
//
//                   }
//
//               }
//
//               }


//               }
//        }




//        if (Integer.parseInt(tracking_record.get("user_eta"))  <= 15){
//            int time_to_spare = 10- Integer.parseInt(tracking_record.get("user_eta"));
//            if (time_to_spare == 0){
//                Log.e("Leave now", "Leave now before you are late");
//            }else{
//                Log.e("time to spare", "You have "+ time_to_spare+" time to spare");
//            }
//        }else{
//            int late = (Integer.parseInt(tracking_record.get("user_eta")) - 10) *-1;
//            Log.e("LATE","You are "+late+" m Late, Check out next trains or try to make this one.");
//        }
//
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String train_item = (String) list.getItemAtPosition(position).toString();
//                String train_id = StringUtils.substringBetween(train_item, "#", ":");
//                for (HashMap t: chosen_trains){
//                    if (t.containsValue(train_id)){
////                        Intent intent = new Intent(TrainTrackingActivity.this, activity_arrival_times.class);
//                        Log.e("train record", t+"");
//                        message.keepSending(false);
////                        intent.putExtra("chosen_train", t);
////                        startActivity(intent);
//                    }
//                }
//            }
//        });
}




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {


//        RecyclerView recList = (RecyclerView) findViewById(R.id.data_view);
//        recList.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recList.setLayoutManager(llm);


        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        final Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");

        if (tracking_record == null || tracking_record.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
            return;
        }
        UserLocation userLocation = new UserLocation(this);
//        userLocation.getLastLocation(getApplicationContext());
//
//

//        String query = "SELECT user_lat, user_lon FROM userLocation_table WHERE location_id = '1'";
//        ArrayList<String> user_record =  sqlite.get_table_record("userLocation_table", "WHERE location_id = '1'");
//        String user_lat = user_record.get(1);
//        String user_lon = user_record.get(2);
//        tracking_record.put("user_lat", user_lat);
//        tracking_record.put("user_lon", user_lon);

//        final Button switch_direction = (Button) findViewById(R.id.switch_direction);
//        final Button choose_station = (Button) findViewById(R.id.pickStation);
//        final Button toMaps = (Button) findViewById(R.id.show);
        Log.e("tracking record", tracking_record+"");

        message.setClicked(false);
        message.keepSending(true);
        message.setTargetContent(tracking_record);

        final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record, handler,true), "API_CALL_Thread");
        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, true), "Content Parser");
        final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, handler,getApplicationContext(),false), "Estimation Thread");
        final Thread t4 = new Thread(new Notifier_Thread(message, getApplicationContext(), t1,t2,t3,false), "Notifier Thread");
//t1.start();
//
//t2.start();
//t3.start();

        t4.start();
        sqlite.close();

//        toMaps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                t3.interrupt();
//                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);
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
//        choose_station.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                t4.interrupt();
//
//                Intent intent = new Intent(TrainTrackingActivity.this, mainactivity.class);
//                Integer profile_id = Integer.parseInt(tracking_record.get("station_id"));
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
//                    Log.e("track", tracking_record.get("tracking_id")+"");
//                    target_station_direction = "5";
//                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "station_dir", target_station_direction);
//                    String query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+tracking_record.get("station_type").toUpperCase()+"'";
//                    main_station = sqlite.getValue(query);
//                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
//                    tracking_record.put("main_station",main_station );
//                    tracking_record.put("station_dir", target_station_direction);
//                    synchronized (message){
//                        message.setDir(target_station_direction);
//                        message.setMainStation(main_station);
//                        message.setClicked(true);
//                        message.notifyAll();
//                    }
//                } else {
//                    Log.e("track", tracking_record.get("tracking_id")+"");
//                    target_station_direction = "1";
//                    String query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase() + "'";
//                    main_station = sqlite.getValue(query);
//                    sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
//                    tracking_record.put("main_station", main_station);
//                    tracking_record.put("station_dir", target_station_direction);
//
//                    synchronized (message){
//                        message.setDir(target_station_direction);
//                        message.setMainStation(main_station);
//                        message.setClicked(true);
//                        message.notifyAll();
//                }
//                }
//            }
//        });
    }
}
