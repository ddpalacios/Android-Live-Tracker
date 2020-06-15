package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.R;
import com.example.cta_map.Threading.Content_Parser_Thread;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Notifier_Thread;
import com.example.cta_map.Threading.Station_Range_Estimation_Thread;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("Registered")
public class activity_arrival_times extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String station_type;
    String station_name;
    String[] train_direction = new String[1];
    Bundle bb;
    final Message message = new Message();


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
//            String train_dir = bundle.getString("train_dir");
//            String train_coordinates = bundle.getString("train_coordinates");
//            String train_next_stop = bundle.getString("train_next_stop");
//            assert train_coordinates != null;
////            Log.e("Recived", train_coordinates);
//            display_results(train_next_stop, train_dir,train_coordinates);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void display_results(String next_stop, final String train_dir, String train_coordinates){
//        int idx =0;
//        int start = 0;
//        int end = 0;
//        ArrayList<String> arrayList = new ArrayList<>();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
//        final ListView list = findViewById(R.id.train_etas);
//        list.setAdapter(adapter);
//        Time time = new Time();
//        String[] TrainlatLong = train_coordinates.split(",");
//        Log.e("COORD", TrainlatLong[0]+","+TrainlatLong[1]);
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
//        final HashMap<String, String> current_train_info = (HashMap<String, String>) getIntent().getExtras().get("current_train_info");
//        ArrayList<String> train_stops = sqlite.get_column_values("line_stops_table", current_train_info.get("station_type").toLowerCase());
//        ArrayList<String> modified_train_stops = new ArrayList<>();
//        ArrayList<Integer> all_eta = new ArrayList<>();
//        for (String noCharStops: train_stops){
//            modified_train_stops.add(noCharStops.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//
//        }
//
//        if (train_dir.equals("1")){
//            end = modified_train_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", "").replaceAll(" ", "").toLowerCase())+1;
//            if (end == -1){
//                Log.e("END 1", next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//            }
//
//
//        }if (train_dir.equals("5")) {
//            start = modified_train_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", "").replaceAll(" ", "").toLowerCase());
//            end = train_stops.size();
//
//            if (start == -1){
//
//                Log.e("START 5",next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//            }
//        }
//
//        if (start ==-1 || end == -1){
//            Toast.makeText(getApplicationContext(), "No Station Found: "+start+" or "+end, Toast.LENGTH_LONG).show();
//        }else {
//
//            List<String> all_stops_till_target = train_stops.subList(start, end);
//            for (int i = 0; i < all_stops_till_target.size(); i++) {
//                String[] stationLatLng = chicago_transits.retrieve_station_coordinates(sqlite, train_stops.get(i), current_train_info.get("station_type").toLowerCase());
//                Double distance = chicago_transits.calculate_coordinate_distance(
//                        Double.parseDouble(TrainlatLong[0]),
//                        Double.parseDouble(TrainlatLong[1]),
//                        Double.parseDouble(stationLatLng[0]),
//                        Double.parseDouble(stationLatLng[1])
//                );
//                Integer current_eta = time.get_estimated_time_arrival(25, distance);
//                all_eta.add(current_eta);
//            }
//
//            if (train_dir.equals("1")) {
//                idx = all_stops_till_target.size() - 1;
//            }
//
//
//            for (int i = 0; i < all_stops_till_target.size(); i++) {
//
//                arrayList.add("Station: " + all_stops_till_target.get(idx) + ": " + all_eta.get(idx));
//                adapter.notifyDataSetChanged();
//
//
//                Log.e("status", "Done");
//                if (train_dir.equals("1")) {
//                    idx--;
//                } else {
//                    idx++;
//                }
//
//            }
//        }

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                String main_station;
//                String[] list_item = String.valueOf(list.getItemAtPosition(position)).replaceAll("\t", "").split(":"); //.replaceAll("[^\\d.]", "");
//                String target_station_name = list_item[0];
//                Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
//                intent.putExtra("target_station_name", target_station_name);
//                intent.putExtra("target_station_type", current_train_info.get("station_type"));
//                intent.putExtra("train_direction", current_train_info.get("train_direction"));
//                ArrayList<String> main_stations = sqlite.get_table_record("main_stations_table", "WHERE train_line = '"+current_train_info.get("station_type").toUpperCase()+"'");
//                if (train_dir.equals("1")){
//                    main_station = main_stations.get(2);
//                }else{
//                    main_station = main_stations.get(3);
//                }
//                intent.putExtra("main_station", main_station);
//                String[] stationLatLng = chicago_transits.retrieve_station_coordinates(sqlite, target_station_name, current_train_info.get("station_type").toLowerCase());
//                intent.putExtra("station_lat", stationLatLng[0]);
//                intent.putExtra("station_lon", stationLatLng[1]);
//                synchronized (message){
//                    message.keepSending(false);
//                }
//
//
//
//
//
//                startActivity(intent);


//
//            }
//        });

//            sqlite.close();

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: refresh layout for train updates
        setContentView(R.layout.activity_arrival_times);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        HashMap<String, String> chosen_train = (HashMap<String, String>) getIntent().getSerializableExtra("chosen_train");
        String train_id = chosen_train.get("train_id");
        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");


        String chosen_train_next_stop = chosen_train.get("next_stop");
        String train_direction = chosen_train.get("train_direction");
        String target_station_name = tracking_record.get("station_name");




        if (tracking_record == null || tracking_record.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
            return;
        }


        message.setClicked(false);
        message.keepSending(true);
        message.setTargetContent(tracking_record);
        final Button switch_direction = (Button) findViewById(R.id.switch_direction);
        final Button backToEta = (Button) findViewById(R.id.BackToETA);
        final Button choose_station = (Button) findViewById(R.id.pickStation);
        final Button toMaps = (Button) findViewById(R.id.show);

        final Thread api_call_thread = new Thread(new API_Caller_Thread(message, tracking_record, handler,false), "API_CALL_Thread");
        api_call_thread.start();
        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "Content Parser");
        t2.start();
        final Thread t3 = new Thread(new Station_Range_Estimation_Thread(message, train_id, sqlite, tracking_record ,true), "Station_Range_Estimation_Thread");
        t3.start();


        final Thread t4 = new Thread(new Notifier_Thread(message, handler, getApplicationContext(), false), "Notifier Thread");
        t4.start();

        toMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t4.interrupt();
                Intent intent = new Intent(activity_arrival_times.this, MapsActivity.class);


                synchronized (message){
                    message.keepSending(false);
                }


                startActivity(intent);
            }
        });


        backToEta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.keepSending(false);
                Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
                startActivity(intent);
            }
        });

        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t4.interrupt();
                Intent intent = new Intent(activity_arrival_times.this, mainactivity.class);
                Integer profile_id = Integer.parseInt(tracking_record.get("profile_id"));
                final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+profile_id+"'");
                intent.putExtra("profile_id", user_record.get(0));
                synchronized (message){
                    message.keepSending(false);
                }

                startActivity(intent);


            }
        });



    }





}
