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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.BottomTrackingAdapter;
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

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void displayResults(Bundle bundle) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");
        final HashMap<String, ArrayList<HashMap>> new_train_data = (HashMap<String, ArrayList<HashMap>>) bundle.getSerializable("estimated_train_data");
        final TreeMap<Integer, String> map = (TreeMap<Integer, String>) bundle.getSerializable("sorted_train_eta_map");
        ArrayList<HashMap> chosen_train = new_train_data.get("chosen_trains");


        for(int i=0;i<map.size();i++) {
            Integer train_eta = (Integer) new Vector(map.keySet()).get(i);
            String train_id = (String) new Vector(map.values()).get(i);
            for (HashMap<String, String> current_train: chosen_train){
                if (current_train.containsValue(train_id)){
                    if (list1.size() ==0){
                        insertMultipleItems(list1, map, adapter);
                        insertMultipleItems2(list2, map, chosen_train,adapter);

                    }else{
                        Log.e(TAG, adapter.getItemCount()+" "+new_train_data.get("chosen_trains").size());
                            Tracking_Station new_obj = new Tracking_Station("#" + train_id + ". To " + tracking_record.get("main_station"), current_train.get("train_eta") +"");
                            updateList(list1, new_obj,i, adapter);



                        String query = "SELECT station_id FROM cta_stops WHERE station_name = '" +  current_train.get("next_stop").toString().trim() + "'" + " AND " +  current_train.get("station_type").toString().trim() + " = 'true'";
                        String station_id = sqlite.getValue(query);

                        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);



                        Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                Double.parseDouble((String) current_train.get("train_lat")),
                                Double.parseDouble((String)  current_train.get("train_lon")),
                                Double.parseDouble(station_coord[0]),
                                Double.parseDouble(station_coord[1]));



                        Train_info train_info = new Train_info("Next Stop: " + current_train.get("next_stop") + "",
                                "" + current_train.get("train_eta") + "m",
                                String.format("%.2f", current_train_distance_from_target_station) + " mi",
                                "To " + tracking_record.get("station_name") + " (target)",
                                current_train.get("train_eta") + "m",
                                String.format("%.2f", Double.parseDouble(String.valueOf(current_train.get("train_distance")))) + " mi",
                                "Train# " + train_id + "");


                        updateBottomList(list2, train_info,i, bottomTrackingAdapter);


                    }
                        }
            }









        }
        Log.e(TAG, "Done");



//        HashMap<Integer, String> train_etas = new HashMap<>();
//        for (HashMap train : new_train_data.get("chosen_trains")) {
//
//            Integer eta = Integer.parseInt((String.valueOf(train.get("train_eta"))));
//            String train_id = (String) train.get("train_id");
//            train_etas.put(eta, train_id);
//        }
//        TreeMap<Integer, String> map = new TreeMap(train_etas);
//        if (list1.size() ==0){
//            insertMultipleItems(list1, map, adapter);
//        }else{
//            Log.e(TAG, adapter.getItemCount()+" "+new_train_data.get("chosen_trains").size());
//            if (adapter.getItemCount() == new_train_data.get("chosen_trains").size()) {
////                Log.e(TAG, new_train_data.get("chosen_trains")+" "+ list1.size());
//                ArrayList<HashMap> trains = new_train_data.get("chosen_trains");


//                int i=0;
//                for (Map.Entry<Integer, String> entry : map.entrySet()) {
//                    Integer eta = entry.getKey();
//                    String train_id = entry.getValue();
//
//
//
//
//
//                    Tracking_Station new_obj = new Tracking_Station("#" + train_id + ". To " + tracking_record.get("main_station"), eta +"");
//                    updateList(list1, new_obj,i, adapter);
//                    i++;
//                }



//                for (Map.Entry<Integer, String> entry : map.entrySet()) {
//                    Integer eta = entry.getKey();
//                    String train_id = entry.getValue();
//
//
//
//
//
//
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" +  t.get("next_stop").toString().trim() + "'" + " AND " +  t.get("station_type").toString().trim() + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                            Double.parseDouble((String) t.get("train_lat")),
//                            Double.parseDouble((String) t.get("train_lon")),
//                            Double.parseDouble(station_coord[0]),
//                            Double.parseDouble(station_coord[1]));

//
//
//
//
//
//
//                    list2.add(new Train_info("Next Stop: " + t.get("next_stop") + "",
//                            "" + eta + "m",
//                            String.format("%.2f", current_train_distance_from_target_station) + " mi",
//                            "To " + tracking_record.get("station_name") + " (target)",
//                            t.get("train_eta") + "m",
//                            String.format("%.2f", Double.parseDouble(String.valueOf(t.get("train_distance")))) + " mi",
//                            "Train# " + train_id + ""));
//
//
//
//
//                    Tracking_Station new_obj = new Tracking_Station("#" + train_id + ". To " + tracking_record.get("main_station"), eta +"");
//                    updateList(list1, new_obj,i, adapter);
//                    i++;
//                }





//            }else {
//                Toast.makeText(getApplicationContext(), "MORE TRAINS "+ new_train_data.get("chosen_trains").size(), Toast.LENGTH_LONG).show();
//            }

//        }


//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        Time time = new Time();
//        Database2 sqlite = new Database2(getApplicationContext());
//        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");

//        HashMap<Integer, String> train_etas = new HashMap<>();
//
//        for (HashMap train : estimated_train_data.get("chosen_trains")) {
//
//            Integer eta = Integer.parseInt((String.valueOf(train.get("train_eta"))));
//            String train_id = (String) train.get("train_id");
//            train_etas.put(eta, train_id);
//        }
//        Map<Integer, String> map = new TreeMap(train_etas);

//        for (Map.Entry<Integer, String> entry : map.entrySet()) {
//            Integer eta = entry.getKey();
//            String train_id = entry.getValue();
//            list.add(new Tracking_Station("  #" + train_id + ". To " + tracking_record.get("main_station"), eta + ""));
//            for (HashMap t : estimated_train_data.get("chosen_trains")) {
//                if (t.containsValue(train_id)) {
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '" +  t.get("next_stop").toString().trim() + "'" + " AND " +  t.get("station_type").toString().trim() + " = 'true'";
//                    String station_id = sqlite.getValue(query);
//                    String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                    Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                            Double.parseDouble((String) t.get("train_lat")),
//                            Double.parseDouble((String) t.get("train_lon")),
//                            Double.parseDouble(station_coord[0]),
//                            Double.parseDouble(station_coord[1]));
//                    int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
//                    list2.add(new Train_info("Next Stop: " + t.get("next_stop") + "",
//                            "" + current_train_eta + "m",
//                            String.format("%.2f", current_train_distance_from_target_station) + " mi",
//                            "To " + tracking_record.get("station_name") + " (target)",
//                            t.get("train_eta") + "m",
//                            String.format("%.2f", Double.parseDouble(String.valueOf(t.get("train_distance")))) + " mi",
//                            "Train# " + train_id + ""));
//                }
//            }
//        }
//


sqlite.close();
    }

    private void insertMultipleItems(ArrayList<Object> data, Map<Integer, String> map, RecyclerView.Adapter adapter ) {
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
    }



    private void insertMultipleItems2(ArrayList<Train_info> data, Map<Integer, String> map, ArrayList<HashMap> chosen_train  , RecyclerView.Adapter adapter ) {
        Database2 sqlite = new Database2(getApplicationContext());
        HashMap<String, String> tracking_record = sqlite.get_tracking_record();
        ArrayList<Train_info> new_objects = new ArrayList<>();
        int i =0;
        Chicago_Transits chicago_transits= new Chicago_Transits();


        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            Integer eta = entry.getKey();
            String train_id = entry.getValue();

            String query = "SELECT station_id FROM cta_stops WHERE station_name = '" +   chosen_train.get(i).get("next_stop").toString().trim() + "'" + " AND " +   chosen_train.get(i).get("station_type").toString().trim() + " = 'true'";
            String station_id = sqlite.getValue(query);

            String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);



            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                    Double.parseDouble((String)  chosen_train.get(i).get("train_lat")),
                    Double.parseDouble((String)   chosen_train.get(i).get("train_lon")),
                    Double.parseDouble(station_coord[0]),
                    Double.parseDouble(station_coord[1]));





            new_objects.add( new Train_info("Next Stop: " + chosen_train.get(i).get("next_stop") + "",
                    "" + chosen_train.get(i).get("train_eta") + "m",
                    String.format("%.2f", current_train_distance_from_target_station) + " mi",
                    "To " + tracking_record.get("station_name") + " (target)",
                    chosen_train.get(i).get("train_eta") + "m",
                    String.format("%.2f", Double.parseDouble(String.valueOf( chosen_train.get(i).get("train_distance")))) + " mi",
                    "Train# " + train_id + ""));

        }

        data.addAll(new_objects);
        adapter.notifyItemRangeChanged(0, new_objects.size());
    }

    private void updateList(ArrayList<Object> data, Object new_obj, Integer idx, RecyclerView.Adapter adapter) {
        data.set(idx, new_obj);
        adapter.notifyItemChanged(idx);
    }


    private void updateBottomList(ArrayList<Train_info> data, Object new_obj, Integer idx, RecyclerView.Adapter adapter) {
        data.set(idx, (Train_info) new_obj);
        adapter.notifyItemChanged(idx);
    }

    private void removeAllItems() {
        list1.clear();
        list2.clear();
        bottomTrackingAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
       HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();
        setContentView(R.layout.train_tracking_activity);
        ImageView imageView  = (ImageView) findViewById(R.id.tracking_image);
        final TextView title = (TextView) findViewById(R.id.tracking_name);
        Switch s1 = (Switch) findViewById(R.id.toMaps);
        FloatingActionButton switch_dir = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        String[]t = new String[]{"1", "2", null};

        Log.e(TAG, t.length+" ");

        for (String s: t) {
            Log.e(TAG, s+" ");
        }









        final Database2 sqlite = new Database2(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.get_tracking_record(); //("tracking_record", "WHERE TRACKING_ID ='"+0+"'");  //.getAllRecord("tracking_table");

        if (tracking_record == null || tracking_record.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
            return;
        }

        TrainLineKeyCodes.put("red",R.drawable.red );
        TrainLineKeyCodes.put("blue", R.drawable.blue);
        TrainLineKeyCodes.put("brown", R.drawable.brown);
        TrainLineKeyCodes.put("green", R.drawable.green);
        TrainLineKeyCodes.put("orange", R.drawable.orange);
        TrainLineKeyCodes.put("pink", R.drawable.pink);
        TrainLineKeyCodes.put("purple", R.drawable.purple);
        TrainLineKeyCodes.put("yellow", R.drawable.yellow);
        UserLocation userLocation = new UserLocation(this);

        imageView.setImageResource(TrainLineKeyCodes.get(tracking_record.get("station_type").toString().trim()));
        title.setText(tracking_record.get("station_name")+" ("+tracking_record.get("main_station")+")");

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
//        Log.e("tracking record", tracking_record+"");




        RecyclerView line_layout = (RecyclerView) findViewById(R.id.vr_recycler_view);
        RecyclerView bottom_layout = (RecyclerView) findViewById(R.id.hr_recycler_view);
        bottom_layout.setAdapter(bottomTrackingAdapter);
        line_layout.setAdapter(adapter);
        bottom_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        message.keepSending(true);
        final Thread t1 = new Thread(new API_Caller_Thread(message, tracking_record,false), "API_CALL_Thread");
        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record,handler , sqlite, false), "Content Parser");
        final Thread t3 = new Thread(new Train_Estimations_Thread(message, userLocation, tracking_record,handler,getApplicationContext(),false), "Estimation Thread");
        final Thread t4 = new Thread(new Notifier_Thread(t1,t2,t3), "Notifier Thread");
        t4.start();
        sqlite.close();






        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);
                intent.putExtra("position", 1);
                message.keepSending(false);
                startActivity(intent);
            }
        });


        switch_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query;
                removeAllItems();
                Log.e(Thread.currentThread().getName(), tracking_record.get("station_dir")+"");
                if (tracking_record.get("station_dir").equals("1")){
                    tracking_record.put("station_dir", "5");
                    query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";

                }else{
                    tracking_record.put("station_dir", "1");
                    query = "SELECT northbound FROM main_stations WHERE main_station_type = '" + tracking_record.get("station_type").toUpperCase().trim() + "'";

                }

                String main_station = sqlite.getValue(query);
                if (main_station.equals("O'Hare")){
                    main_station  = main_station.replaceAll("[^0-9a-zA-Z]", "");
                }

                sqlite.update_value(tracking_record.get("tracking_id"), "tracking_table", "main_station_name", main_station);
                tracking_record.put("main_station", main_station);

                t3.interrupt();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
                message.keepSending(false);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
