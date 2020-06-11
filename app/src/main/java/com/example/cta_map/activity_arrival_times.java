package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
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
    Message message = new Message();


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            ArrayList<Integer> etas = bundle.getIntegerArrayList("train_etas");
            String train_dir = bundle.getString("train_dir");
            ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");
            Log.e("ddd", chosen_trains+"");

//            displayResults(etas, chosen_trains, train_dir);
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: refresh layout for train updates
        setContentView(R.layout.activity_arrival_times);
        super.onCreate(savedInstanceState);
        bb=getIntent().getExtras();
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> current_train_info = (HashMap<String, String>) getIntent().getExtras().get("current_train_info");
        String target_station_type = "red";//current_train_info.get("station_type");

        synchronized (message) {
            message.keepSending(true);
        }
        final Thread t1 = new Thread(new Thread1(message, target_station_type), "API_CALL_Thread");
        t1.start();
        final Thread t4 = new Thread(new Thread4(message, target_station_type), "Content Parser");
        t4.start();
        final Thread t5 = new Thread(new Thread5(message), "Displayer");
        t5.start();

//synchronized (message){
//    message.notifyAll();
//}
//        Log.e("message", message.getDir());






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
//        int start = 0;
//        int end = 0;
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
//
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
