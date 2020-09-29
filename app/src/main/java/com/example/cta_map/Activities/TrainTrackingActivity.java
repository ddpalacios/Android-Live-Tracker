package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.AllTrainsTable;
import com.example.cta_map.BottomTrackingAdapter;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.UserLocation;
import com.example.cta_map.R;

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Notifier_Thread;
import com.example.cta_map.Backend.Threading.Train_Estimations_Thread;
import com.example.cta_map.TrackingAdapter;
import com.example.cta_map.Tracking_Station;
import com.example.cta_map.Train_info;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Vector;

public class TrainTrackingActivity extends AppCompatActivity {

    Bundle bb; // Retrieve data from main screen
    final Message message = new Message();
    String TAG = "MAIN UI";
    ArrayList<Object> list1 = new ArrayList<Object>();
    ArrayList<Train_info> list2 = new ArrayList<>();
    BottomTrackingAdapter bottomTrackingAdapter = new BottomTrackingAdapter(this, list2);
    TrackingAdapter adapter = new TrackingAdapter(this, list1);

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
                Bundle bundle = msg.getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                displayResults(bundle);
            }
        }
    };
//
    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void displayResults(Bundle bundle) {
        try {
            RecyclerView line_layout = (RecyclerView) findViewById(R.id.vr_recycler_view);
            final ArrayList<Object> all_chosen_trains = (ArrayList<Object>) bundle.getSerializable("all_chosen_trains");
            if (all_chosen_trains == null) {
                Toast.makeText(getApplicationContext(), "NO TRAINS AVAIALABLE", Toast.LENGTH_SHORT).show();
            }
            else {

                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                for (Object record : all_chosen_trains) {
                    HashMap<String, String> incoming_train = (HashMap<String, String>) record;
                    ArrayList<Object> cta_record = cta_dataBase.excecuteQuery("*", "cta_stops", "MAP_ID = '"+ Objects.requireNonNull(incoming_train.get("next_stop_id")).trim()+"'", null);
                    HashMap<String, String> next_stop_station_record = (HashMap<String, String>) cta_record.get(0);
                    Log.e(TAG, next_stop_station_record.get("station_name") + " " + incoming_train.get("next_stop_eta"));
                }
                TrackingAdapter adapter = new TrackingAdapter(getApplicationContext(), all_chosen_trains);
                line_layout.setAdapter(adapter);
                line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                Log.e(TAG, "DONE.");
                cta_dataBase.close();
            }

        }catch (Exception e){e.printStackTrace();}







//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        Database2 sqlite = new Database2(getApplicationContext());
//        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
//        final TreeMap<Integer, String> map = (TreeMap<Integer, String>) bundle.getSerializable("sorted_train_eta_map");
//        ArrayList<HashMap> chosen_train = new_train_data.get("chosen_trains");
//        final TextView title = (TextView) findViewById(R.id.tracking_name);
//        Log.e(TAG, tracking_record+" ");
//
//        assert chosen_train != null;
//        if (chosen_train.size() == 0) {
//            Toast.makeText(getApplicationContext(), "No Trains Available", Toast.LENGTH_LONG).show();
//            sqlite.close();
//        } else {
//
//            if (chosen_train.get(0).get("train_direction").equals("1")) {
//                title.setText(tracking_record.get("station_name") + " (North)");
//
//            } else {
//                title.setText(tracking_record.get("station_name") + " (South)");
//
//            }
//
//
//            for (int i = 0; i < map.size(); i++) {
//                Integer train_eta = (Integer) new Vector(map.keySet()).get(i);
//                String train_id = (String) new Vector(map.values()).get(i);
//                for (HashMap<String, String> current_train : chosen_train) {
//                    if (current_train.containsValue(train_id)) {
//                        if (list1.size() == 0) {
//                            insertMultipleItems(list1, map, adapter);
//
////                        insertMultipleItems2(list2, map, chosen_train,adapter);
//
//                        } else {
//                            Log.e(TAG, adapter.getItemCount() + " " + new_train_data.get("chosen_trains").size());
//                            Tracking_Station new_obj = new Tracking_Station("#" + train_id + ". To " + tracking_record.get("main_station"), current_train.get("train_eta") + "");
//                            updateList(list1, new_obj, i, adapter);
//
//                            String query = "SELECT station_id FROM cta_stops WHERE station_name = '" + current_train.get("next_stop").replaceAll("'", "").trim() + "'" + " AND " + current_train.get("station_type").toString().trim() + " = 'true'";
//                            try {
//                                String station_id = sqlite.getValue(query);
//                                String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                                Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                                        Double.parseDouble((String) current_train.get("train_lat")),
//                                        Double.parseDouble((String) current_train.get("train_lon")),
//                                        Double.parseDouble(station_coord[0]),
//                                        Double.parseDouble(station_coord[1]));
//
//                                Train_info train_info = new Train_info("Next Stop: " + current_train.get("next_stop") + "",
//                                        "" + current_train.get("train_eta") + "m",
//                                        String.format("%.2f", current_train_distance_from_target_station) + " mi",
//                                        "To " + tracking_record.get("station_name") + " (target)",
//                                        current_train.get("train_eta") + "m",
//                                        String.format("%.2f", Double.parseDouble(String.valueOf(current_train.get("train_distance")))) + " mi",
//                                        "Train# " + train_id + "");
//                            } catch (Exception e) {
//                                Log.e(TAG, "ERROR " + current_train.get("next_stop"));
//                                e.printStackTrace();
//                            }
//
//
////                        updateBottomList(list2, train_info,i, bottomTrackingAdapter);
//                        }
//                    }
//                }
//            }
//            Log.e(TAG, "Done");
//
//
//            sqlite.close();
//        }
    }
    private void insertMultipleItems(ArrayList<Object> data, Map<Integer, String> map, RecyclerView.Adapter adapter) {
        Database2 sqlite = new Database2(getApplicationContext());
        HashMap<String, String> tracking_record = sqlite.get_tracking_record();
        ArrayList<Object> new_objects = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            Integer eta = entry.getKey();
            String train_id = entry.getValue();
            new_objects.add(new Tracking_Station("  #" + train_id + ". To " + tracking_record.get("main_station"), eta + ""));

        }

        data.addAll(new_objects);
        adapter.notifyItemRangeChanged(0, new_objects.size());
        sqlite.close();
    }
//
//
//    private void insertMultipleItems2(ArrayList<Train_info> data, Map<Integer, String> map, ArrayList<HashMap> chosen_train  , RecyclerView.Adapter adapter ) {
//        Database2 sqlite = new Database2(getApplicationContext());
//        HashMap<String, String> tracking_record = sqlite.get_tracking_record();
//        ArrayList<Train_info> new_objects = new ArrayList<>();
//        int i =0;
//        Chicago_Transits chicago_transits= new Chicago_Transits();
//
//
//        for (Map.Entry<Integer, String> entry : map.entrySet()) {
//            Integer eta = entry.getKey();
//            String train_id = entry.getValue();
//
//            String query = "SELECT station_id FROM cta_stops WHERE station_name = '" +   chosen_train.get(i).get("next_stop").toString().trim() + "'" + " AND " +   chosen_train.get(i).get("station_type").toString().trim() + " = 'true'";
//            String station_id = sqlite.getValue(query);
//
//            String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//
//
//
//            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                    Double.parseDouble((String)  chosen_train.get(i).get("train_lat")),
//                    Double.parseDouble((String)   chosen_train.get(i).get("train_lon")),
//                    Double.parseDouble(station_coord[0]),
//                    Double.parseDouble(station_coord[1]));
//
//
//
//
//
//            new_objects.add( new Train_info("Next Stop: " + chosen_train.get(i).get("next_stop") + "",
//                    "" + chosen_train.get(i).get("train_eta") + "m",
//                    String.format("%.2f", current_train_distance_from_target_station) + " mi",
//                    "To " + tracking_record.get("station_name") + " (target)",
//                    chosen_train.get(i).get("train_eta") + "m",
//                    String.format("%.2f", Double.parseDouble(String.valueOf( chosen_train.get(i).get("train_distance")))) + " mi",
//                    "Train# " + train_id + ""));
//
//        }
//
//        data.addAll(new_objects);
//        adapter.notifyItemRangeChanged(0, new_objects.size());
//        sqlite.close();
//    }
//
    private void updateList(ArrayList<Object> data, Object new_obj, Integer idx, RecyclerView.Adapter adapter) {
        data.set(idx, new_obj);
        adapter.notifyItemChanged(idx);

    }
//
//
//    private void updateBottomList(ArrayList<Train_info> data, Object new_obj, Integer idx, RecyclerView.Adapter adapter) {
//        data.set(idx, (Train_info) new_obj);
//        adapter.notifyItemChanged(idx);
//    }
//
    private void removeAllItems() {
        list1.clear();
        list2.clear();
        bottomTrackingAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bb;
        bb = getIntent().getExtras();
        String target_station_name = bb.getString("target_station_name");
        String target_type = bb.getString("target_station_type");
        String target_dir = bb.getString("target_station_dir");
        String target_station_id = bb.getString("target_station_id");

        final Thread t1 = new Thread(new API_Caller_Thread(message, target_type,false), "API_CALL_Thread");
        final Thread t2 = new Thread(new Content_Parser_Thread(getApplicationContext(), message, target_type, target_dir, target_station_name, target_station_id), "Content Parser");
        final Thread t3 = new Thread(new Train_Estimations_Thread(getApplicationContext(), message, handler), "Estimation Thread");
        final Thread t4 = new Thread(new Notifier_Thread(t1,t2,t3), "Notifier Thread");
        t4.start();


    }

//
//        ImageView imageView  = (ImageView) findViewById(R.id.tracking_image);
//        final TextView title = (TextView) findViewById(R.id.tracking_name);
//        Switch s1 = (Switch) findViewById(R.id.toMaps);
//
//        FloatingActionButton switch_dir = (FloatingActionButton) findViewById(R.id.floatingActionButton);
//        ActionBar actionBar = getSupportActionBar();
//
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//
//        assert actionBar != null;
//
//        final Database2 sqlite = new Database2(getApplicationContext());
//        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
//        actionBar.setTitle("Train Tracker ("+tracking_record.get("station_name")+")");
//
//        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", tracking_record.get("station_type").replaceAll(" ", "").toLowerCase());
//        sqlite.close();
//
//        if (tracking_record.isEmpty()){
//            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        UserLocation userLocation = new UserLocation(this);
//        HashMap<String, Integer> TrainLineKeyCodes = getTrainLineKeys();
//        try {
//            Integer screen_station_color = TrainLineKeyCodes.get(Objects.requireNonNull(tracking_record.get("station_type")).trim());
//            if (screen_station_color == null){
//                imageView.setImageResource(R.drawable.ic_launcher_background);
//                return;
//            }
//            imageView.setImageResource(screen_station_color);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        if (Objects.equals(tracking_record.get("station_dir"), "1")){
//            title.setText(tracking_record.get("station_name")+" (North)");
//
//        }else{
//            title.setText(tracking_record.get("station_name")+" (South)");
//
//        }
//
//
//        RecyclerView line_layout = findViewById(R.id.vr_recycler_view);
//        RecyclerView bottom_layout = findViewById(R.id.hr_recycler_view);
//        bottom_layout.setAdapter(bottomTrackingAdapter);
//        line_layout.setAdapter(adapter);
//        bottom_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
//
//        final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record,false), "API_CALL_Thread");
//        final Thread t2 = new Thread(new Content_Parser_Thread(message,getApplicationContext(), tracking_record,handler , stops, false), "Content Parser");
//        final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, tracking_record,handler,getApplicationContext(),false), "Estimation Thread");
//        final Thread t4 = new Thread(new Notifier_Thread(t1,t2,t3), "Notifier Thread");
//        t4.start();
//








////
////        userLocation.getLastLocation(getApplicationContext());
//
////        String query = "SELECT user_lat, user_lon FROM userLocation_table WHERE location_id = '1'";
////        ArrayList<String> user_record =  sqlite.get_table_record("userLocation_table", "WHERE location_id = '1'");
////        String user_lat = user_record.get(1);
////        String user_lon = user_record.get(2);
////        tracking_record.put("user_lat", user_lat);
////        tracking_record.put("user_lon", user_lon);
//
//        final Button switch_direction = (Button) findViewById(R.id.switch_direction);
//        final Button choose_station = (Button) findViewById(R.id.pickStation);
//        final Button toMaps = (Button) findViewById(R.id.show);
//        Log.e("tracking record", tracking_record+"");
//
//
//
//
//        RecyclerView line_layout = (RecyclerView) findViewById(R.id.vr_recycler_view);
//        RecyclerView bottom_layout = (RecyclerView) findViewById(R.id.hr_recycler_view);
//        bottom_layout.setAdapter(bottomTrackingAdapter);
//        line_layout.setAdapter(adapter);
//        bottom_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



//        message.keepSending(true);



//        final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record,false), "API_CALL_Thread");
//        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record,handler , stops, false), "Content Parser");
//        t1.start();
//        t2.start();
//        final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, tracking_record,handler,getApplicationContext(),false), "Estimation Thread");
//        final Thread t4 = new Thread(new Notifier_Thread(t1,t2,t3), "Notifier Thread");
//        t4.start();
//
//
//
//
//
//
//        s1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);
//                message.keepSending(false);
//                startActivity(intent);
//            }
//        });
////
////
//        switch_dir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String query;
//                removeAllItems();
//
//                Log.e(Thread.currentThread().getName(), tracking_record.get("station_dir")+"");
//                if (Objects.equals(tracking_record.get("station_dir"), "1")){
//                    tracking_record.put("station_dir", "5");
//                    title.setText(tracking_record.get("station_name")+" (South)");
//                    query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";
//
//                }else{
//                    title.setText(tracking_record.get("station_name")+" (North)");
//                    tracking_record.put("station_dir", "1");
//                    query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";
//
//                }
//
//                String main_station = sqlite.getValue(query);
//                if (main_station.equals("O'Hare")){
//                    main_station  = main_station.replaceAll("[^0-9a-zA-Z]", "");
//                }
//
//                sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
//                tracking_record.put("main_station", main_station);
//
//                t3.interrupt();
//
//            }
//        });
//    }
//

    public HashMap<String, Integer> getTrainLineKeys(){
        HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();

        TrainLineKeyCodes.put("red",R.drawable.red );
        TrainLineKeyCodes.put("blue", R.drawable.blue);
        TrainLineKeyCodes.put("brown", R.drawable.brown);
        TrainLineKeyCodes.put("green", R.drawable.green);
        TrainLineKeyCodes.put("orange", R.drawable.orange);
        TrainLineKeyCodes.put("pink", R.drawable.pink);
        TrainLineKeyCodes.put("purple", R.drawable.purple);
        TrainLineKeyCodes.put("yellow", R.drawable.yellow);
        return TrainLineKeyCodes;
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
}
