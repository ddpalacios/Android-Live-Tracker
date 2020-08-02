//package com.example.cta_map.Activities;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.cta_map.DataBase.DatabaseHelper;
//import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.R;
//import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
//import com.example.cta_map.Backend.Threading.Message;
//import com.example.cta_map.Backend.Threading.API_Caller_Thread;
//import com.example.cta_map.Backend.Threading.Notifier_Thread;
////import com.example.cta_map.Backend.Threading.StationProxy_Thread;
//import com.example.cta_map.Backend.Threading.Station_Range_Estimation_Thread;
//
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//@SuppressLint("Registered")
//public class activity_arrival_times extends AppCompatActivity {
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    final Message message = new Message();
//
//
//    @SuppressLint("HandlerLeak")
//    private final Handler handler = new Handler() {
//        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            Bundle bundle = msg.getData();
//            display_results(bundle);
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void display_results(Bundle bundle) {
//        if (bundle.getSerializable("station_range_eta") !=null){
//            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
//            ArrayList<Integer> list = new ArrayList<>();
//            HashMap<String, Integer> station_etas = (HashMap<String, Integer>) bundle.getSerializable("station_range_eta");
//            ArrayList<String> arrayList = new ArrayList<>();
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
//            final ListView listView = findViewById(R.id.train_etas);
//            listView.setAdapter(adapter);
//            for (Map.Entry<String, Integer> entry : station_etas.entrySet()) {
//                list.add(entry.getValue());
//            }
//            Collections.sort(list, new Comparator<Integer>() {
//                @Override
//                public int compare(Integer o1, Integer o2) {
//                    return (o1).compareTo(o2);
//                }
//            });
//            for (Integer str : list) {
//                for (Map.Entry<String, Integer> entry : station_etas.entrySet()) {
//                    if (entry.getValue().equals(str)) {
//                        sortedMap.put(entry.getKey(), str);
//                    }
//                }
//            }
//            int idx =0;
//            List sorted_etas = new ArrayList( sortedMap.values());
//            for (String station_name: sortedMap.keySet()){
//                arrayList.add("To: "+station_name+": "+sorted_etas.get(idx)+"m");
//                adapter.notifyDataSetChanged();
//                idx++;
//            }
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
//                    DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
//                    Chicago_Transits chicago_transits = new Chicago_Transits();
//                    final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");
//                    String train_item = (String) listView.getItemAtPosition(position).toString();
//                    String station_name = StringUtils.substringBetween(train_item, "To: ", ":");
//                    String station_type = tracking_record.get("station_type");
//                    String profile_id = tracking_record.get("profile_id");
////                    String[] new_tracking_coordinates =chicago_transits.retrieve_station_coordinates(sqlite, station_name, station_type);
////                    sqlite.update_tracking_record(profile_id, "tracking_table",station_name, station_type, new_tracking_coordinates[0], new_tracking_coordinates[1]);
////                    sqlite.close();
//                    message.keepSending(false);
//                    startActivity(intent);
//                }
//            });
//
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO: refresh layout for train updates
//        setContentView(R.layout.activity_arrival_times);
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        HashMap<String, String> chosen_train = (HashMap<String, String>) getIntent().getSerializableExtra("chosen_train");
//        String train_id = chosen_train.get("train_id");
//        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
//        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");
//
//        if (tracking_record == null || tracking_record.isEmpty()){
//            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
//            return;
//        }
//        message.setClicked(false);
//        message.keepSending(true);
//        message.setTargetContent(tracking_record);
//        final Button backToEta = (Button) findViewById(R.id.BackToETA);
//        final Button choose_station = (Button) findViewById(R.id.pickStation);
//        final Button toMaps = (Button) findViewById(R.id.show);
//
//
//
//
//
//
//
//        final Thread api_call_thread = new Thread(new API_Caller_Thread(message, tracking_record, handler,false), "API_CALL_Thread");
//        api_call_thread.start();
//        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite,false), "Content Parser");
//        t2.start();
//        final Thread t3 = new Thread(new Station_Range_Estimation_Thread(message, train_id, sqlite, tracking_record ,false), "Station_Range_Estimation_Thread");
//        t3.start();
//
////        final Thread t4 = new Thread(new StationProxy_Thread(message, sqlite, tracking_record, train_id,true), "Station prox Thread");
////        t4.start();
//
//        final Thread t5 = new Thread(new Notifier_Thread(message, getApplicationContext(), api_call_thread,t2,t3,false), "Notifier Thread");
//        t5.start();
//
////        toMaps.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                t5.interrupt();
////                Intent intent = new Intent(activity_arrival_times.this, MapsActivity.class);
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
////        backToEta.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                message.keepSending(false);
////                Intent intent = new Intent(activity_arrival_times.this, TrainTrackingActivity.class);
////                startActivity(intent);
////            }
////        });
////
////        choose_station.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                t5.interrupt();
////                Intent intent = new Intent(activity_arrival_times.this, mainactivity.class);
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
//
//
//
//    }
//
//}
