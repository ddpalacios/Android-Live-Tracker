package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.R;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Thread4;
import com.example.cta_map.Threading.Thread5;
import com.example.cta_map.Displayers.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            String train_dir = bundle.getString("train_dir");
            String train_coordinates = bundle.getString("train_coordinates");
            String train_next_stop = bundle.getString("train_next_stop");
            assert train_coordinates != null;
//            Log.e("Recived", train_coordinates);
            display_results(train_next_stop, train_dir,train_coordinates);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void display_results(String next_stop, final String train_dir, String train_coordinates){
        int idx =0;
        int start = 0;
        int end = 0;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        final ListView list = findViewById(R.id.train_etas);
        list.setAdapter(adapter);
        Time time = new Time();
        String[] TrainlatLong = train_coordinates.split(",");
        Log.e("COORD", TrainlatLong[0]+","+TrainlatLong[1]);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> current_train_info = (HashMap<String, String>) getIntent().getExtras().get("current_train_info");
        ArrayList<String> train_stops = sqlite.get_column_values("line_stops_table", current_train_info.get("station_type").toLowerCase());
        ArrayList<String> modified_train_stops = new ArrayList<>();
        ArrayList<Integer> all_eta = new ArrayList<>();
        for (String noCharStops: train_stops){
            modified_train_stops.add(noCharStops.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());

        }

        if (train_dir.equals("1")){
            end = modified_train_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", "").replaceAll(" ", "").toLowerCase())+1;
            if (end == -1){
                Log.e("END 1", next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
            }


        }if (train_dir.equals("5")) {
            start = modified_train_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", "").replaceAll(" ", "").toLowerCase());
            end = train_stops.size();

            if (start == -1){

                Log.e("START 5",next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
            }
        }

        if (start ==-1 || end == -1){
            Toast.makeText(getApplicationContext(), "No Station Found: "+start+" or "+end, Toast.LENGTH_LONG).show();
        }else {

            List<String> all_stops_till_target = train_stops.subList(start, end);
            for (int i = 0; i < all_stops_till_target.size(); i++) {
                String[] stationLatLng = chicago_transits.retrieve_station_coordinates(sqlite, train_stops.get(i), current_train_info.get("station_type").toLowerCase());
                Double distance = chicago_transits.calculate_coordinate_distance(
                        Double.parseDouble(TrainlatLong[0]),
                        Double.parseDouble(TrainlatLong[1]),
                        Double.parseDouble(stationLatLng[0]),
                        Double.parseDouble(stationLatLng[1])
                );
                Integer current_eta = time.get_estimated_time_arrival(25, distance);
                all_eta.add(current_eta);
            }

            if (train_dir.equals("1")) {
                idx = all_stops_till_target.size() - 1;
            }


            for (int i = 0; i < all_stops_till_target.size(); i++) {

                arrayList.add("Station: " + all_stops_till_target.get(idx) + ": " + all_eta.get(idx));
                adapter.notifyDataSetChanged();


                Log.e("status", "Done");
                if (train_dir.equals("1")) {
                    idx--;
                } else {
                    idx++;
                }

            }
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chicago_Transits chicago_transits = new Chicago_Transits();
                String main_station;
                String[] list_item = String.valueOf(list.getItemAtPosition(position)).replaceAll("\t", "").split(":"); //.replaceAll("[^\\d.]", "");
                String target_station_name = list_item[0];
                Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
                intent.putExtra("target_station_name", target_station_name);
                intent.putExtra("target_station_type", current_train_info.get("station_type"));
                intent.putExtra("train_direction", current_train_info.get("train_direction"));
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



            }
        });






            sqlite.close();

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: refresh layout for train updates
        setContentView(R.layout.activity_arrival_times);
        super.onCreate(savedInstanceState);
        bb=getIntent().getExtras();
        final HashMap<String, String> current_train_info = (HashMap<String, String>) getIntent().getExtras().get("current_train_info");
        String target_station_type = current_train_info.get("station_type");
        Log.e("Picked", current_train_info+"");
        message.keepSending(true);
        final Thread t1 = new Thread(new API_Caller_Thread(message, target_station_type, true), "NEW API_CALL_Thread");
        t1.start();
        final Thread t4 = new Thread(new Thread4(message, target_station_type, current_train_info.get("train_id")), " NEW Content Parser");
        t4.start();
        final Thread t5 = new Thread(new Thread5(message, handler), "NEW Displayer");
        t5.start();






//        ArrayList<String> train_stops = sqlite.get_column_values("line_stops_table", current_train_info.get("station_type").toLowerCase());
//        final String next_stop = current_train_info.get("next_stop");
//        String specified_train_direction = current_train_info.get("train_direction");
//        station_type = current_train_info.get("station_type");
//        station_name = current_train_info.get("target_station");
//        train_direction[0] = specified_train_direction;
//
//
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
//        final ListView list = (ListView) findViewById(R.id.train_etas);
//        list.setAdapter(adapter);
//
//
//        int idx = 0;

//        Log.e("idx activity", train_stops+"");
//
//        if (specified_train_direction.equals("1")){
//            end = train_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", ""))+1;
//            Log.e("idx activity", start + " "+ end);
//
//
//        }if (specified_train_direction.equals("5")){
//            Log.e("idx activity", specified_train_direction);
//            start = train_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", ""));
//            end = train_stops.size();
//
//            Log.e("idx activity", start + " "+ end);
//
//        }
//
//

    }

//        Context context = getApplicationContext();
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        BufferedReader train_station_stops_reader = chicago_transits.setup_file_reader(context, R.raw.train_line_stops);
//        ArrayList<String> all_stops = chicago_transits.retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"), true);
//        final String next_stop = current_train_info.get("next_stop");
//        ArrayList<String> arrayList = new ArrayList<>();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
//        final ListView list = (ListView) findViewById(R.id.train_etas);
//        list.setAdapter(adapter);
//        String specified_train_direction = current_train_info.get("train_direction");
//        station_type = current_train_info.get("station_type");
//        station_name = current_train_info.get("target_station");
//        train_direction[0] = specified_train_direction;
//
//
//
//
//        int idx = 0;
//        int start = 0;
//        int end = 0;
//        Log.e("idx activity", all_stops+"");
//
//        if (specified_train_direction.equals("1")){
//            end = all_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", ""))+1;
//            Log.e("idx activity", start + " "+ end);
//
//
//        }if (specified_train_direction.equals("5")){
//            Log.e("idx activity", specified_train_direction);
//            start = all_stops.indexOf(next_stop.replaceAll("[^a-zA-Z0-9]", ""));
//            end = all_stops.size();
//
//            Log.e("idx activity", start + " "+ end);
//
//        }

////        ArrayList<Integer> range_of_eta = chicago_transits.calculate_station_range_eta(current_train_info, start, end, Integer.parseInt(specified_train_direction), context);
//        BufferedReader train_station_stops_read = chicago_transits.setup_file_reader(context, R.raw.train_line_stops);
//        ArrayList<String> all_stop = chicago_transits.retrieve_line_stations(train_station_stops_read, current_train_info.get("station_type"), false);
//        List<String> all_stops_till_target = all_stop.subList(start , end);
//
//        if (specified_train_direction.equals("1")){
//            idx = all_stops_till_target.size() -1;
//        }
//
//
//        for (int i=0; i < all_stops_till_target.size(); i++){
//                String remaining_stop = all_stops_till_target.get(idx);
//                arrayList.add(remaining_stop +":\t\t\t\t"+range_of_eta.get(i)+"m");
//                adapter.notifyDataSetChanged();
//                if (specified_train_direction.equals("1")){
//                idx--;
//            }else{
//                idx++;
//                }
//
//        }
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String[] list_item = String.valueOf(list.getItemAtPosition(position)).replaceAll("\t", "").split(":"); //.replaceAll("[^\\d.]", "");
//                String target_station_name = list_item[0];
//                Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
//                intent.putExtra("target_station_name", target_station_name);
//                intent.putExtra("target_station_type", current_train_info.get("station_type"));
//                intent.putExtra("train_direction", current_train_info.get("train_direction"));
//                startActivity(intent);
//            }
//        });



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called ");
        Intent intent = new Intent(activity_arrival_times.this,TrainTrackingActivity.class);
        Boolean[] isOn = new Boolean[]{false};
        intent.putExtra("target_station_type", station_type);
        intent.putExtra("target_station_name", station_name);
        intent.putExtra("train_direction", train_direction[0]);
        intent.putExtra("isOn", isOn[0]);

        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
