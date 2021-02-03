package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.Button;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.AllTrainsTable;
import com.example.cta_map.BottomTrackingAdapter;
import com.example.cta_map.DataBase.CTA_DataBase;
//import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.DataBase.CTA_Stops;
import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.Displayers.UserLocation;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.FavStationAdapter;
import com.example.cta_map.R;

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Notifier_Thread;
//import com.example.cta_map.Backend.Threading.Train_Estimations_Thread;
import com.example.cta_map.TrackingAdapter;
import com.example.cta_map.Tracking_Station;
import com.example.cta_map.Train_info;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Vector;

public class TrainTrackingActivity extends AppCompatActivity {
    final Message message = new Message();
    String TAG = "MAIN UI";

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void displayResults(Bundle bundle) {
        final ArrayList<Object> all_chosen_trains = (ArrayList<Object>) bundle.getSerializable("new_incoming_trains");
        ArrayList<Object> list = new ArrayList<>(all_chosen_trains);
        RecyclerView live_trains_rv = findViewById(R.id.live_trains_rv);
        TrackingAdapter adapter = new TrackingAdapter(getApplicationContext(), list);
        live_trains_rv.setAdapter(adapter);
        live_trains_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Object main_record = all_chosen_trains.get(0);
        Train main_train  = (Train) main_record;

//        all_chosen_trains.remove(0);
        Log.e("UI", "ALL OTHER TRAINS STATUS:\n");
        for (Object record : all_chosen_trains) {
                   Train incoming_train = (Train) record;
                    Log.e("UI", incoming_train.getRn()+" | "+incoming_train.getNextStaNm()+ " |  "+ incoming_train.getTarget_eta()+"m | STATUS: "+ incoming_train.getStatus() + " | USER STATUS: "+ incoming_train.getUserStatus());
                }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bb;
        bb = getIntent().getExtras();
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        Chicago_Transits chicago_transits = new Chicago_Transits();
        final boolean[] change_text;
        final  boolean[] change_thread_text = new boolean[]{false};
        HashMap<String, Integer> TrainLineKeyCodes = getTrainLineKeys();
        // Non Persistable Variables
        final HashMap<String, String> tracking_station = (HashMap<String, String>) bb.getSerializable("tracking_station");
        final Button add_to_favorites = findViewById(R.id.add_as_favorite);
        TextView main_title = findViewById(R.id.main_title);
        ImageView main_tracking_image = findViewById(R.id.main_tracking_image);
        main_tracking_image.setImageResource(TrainLineKeyCodes.get(tracking_station.get("station_type").toLowerCase()));
        main_title.setText(tracking_station.get("target_station_name")+"");

        final Button backToHome = findViewById(R.id.HomeButton);
        final Button test_threads = findViewById(R.id.test_threads);
        ArrayList<Object> record, cta_record;
        record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "STOP_ID = '"+tracking_station.get("station_id")+"'", null,null);
        cta_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STOP_ID = '"+tracking_station.get("station_id")+"'", null,null);
        if (tracking_station.get("station_type").toLowerCase().equals("purple" )){
            record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND "+chicago_transits.TrainLineKeys(tracking_station.get("station_type").toLowerCase()).toUpperCase() +" = '1'", null,null);
            if (record == null){
                record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND PEXP = '1'", null,null);

            }
        }

        if (record!=null) {
            change_text = new boolean[]{true};
            add_to_favorites.setText("Saved");
        }else{
            change_text = new boolean[]{false};
            add_to_favorites.setText("Add To Favorites");
        }

        message.setSwitchDir(false);
        message.keepSending(true);
        message.setTarget_name(tracking_station.get("target_station_name"));
        message.setTarget_type(tracking_station.get("station_type"));

        final Thread api_caller =  new  Thread(new API_Caller_Thread(message, tracking_station.get("station_type")));
        final Thread content_parser = new Thread(new Content_Parser_Thread(getApplicationContext(), message, handler));

        test_threads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!change_thread_text[0]){
                    change_thread_text[0] = true;
                    test_threads.setText("Running");
                    message.setDir(tracking_station.get("station_dir"));
                    message.setSwitchDir(false);
                    message.keepSending(true);
                    message.setTarget_name(tracking_station.get("target_station_name"));
                    message.setTarget_type(tracking_station.get("station_type"));

                    Log.e(TAG, api_caller.getState().toString()+"");
                    if (api_caller.getState() == Thread.State.NEW){
                        api_caller.start();
                    }
                    if (content_parser.getState() == Thread.State.NEW){
                        content_parser.start();
                    }

                }else{
                    test_threads.setText("Run");
                    change_thread_text[0] = false;
                    message.keepSending(false);
                    api_caller.interrupt();
                    content_parser.interrupt();

                }





            }
        });

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainTrackingActivity.this, mainactivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });


        add_to_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!change_text[0]) {
                    add_to_favorites.setText("Saved");
                    change_text[0] = true;
                    Chicago_Transits chicago_transits = new Chicago_Transits();
                    CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                    ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND "+chicago_transits.TrainLineKeys(tracking_station.get("station_type").toLowerCase()).toUpperCase() +" = '1'", null,null);
                    if (tracking_station.get("station_type").toLowerCase().equals("purple" )){
                        record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND "+chicago_transits.TrainLineKeys(tracking_station.get("station_type").toLowerCase()).toUpperCase() +" = '1'", null,null);
                        if (record == null){
                            record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND PEXP = '1'", null,null);

                        }
                    }


                    HashMap<String,String> target_station_record  = (HashMap<String, String>) record.get(0);
                    FavoriteStation favoriteStation = new FavoriteStation(tracking_station.get("station_id"),target_station_record.get("STATION_NAME"), tracking_station.get("station_type"));
                    cta_dataBase.commit(favoriteStation, "favorite_station");
                    Toast.makeText(getApplicationContext(), tracking_station.get("target_station_name")+ " station added to favorites", Toast.LENGTH_LONG).show();

                }else{
                    Chicago_Transits chicago_transits = new Chicago_Transits();

                    add_to_favorites.setText("Add To Favorites");
                    change_text[0] = false;
                    CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                    ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND "+chicago_transits.TrainLineKeys(tracking_station.get("station_type").toLowerCase()).toUpperCase() +" = '1'", null,null);
                    if (tracking_station.get("station_type").toLowerCase().equals("purple" )){
                        record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND "+chicago_transits.TrainLineKeys(tracking_station.get("station_type").toLowerCase()).toUpperCase() +" = '1'", null,null);
                        if (record == null){
                            record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("target_station_name")+"' AND PEXP = '1'", null,null);

                        }
                    }

//                    ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+tracking_station.get("station_name")+"' AND "+chicago_transits.TrainLineKeys(tracking_station.get("station_type").toLowerCase()).toUpperCase() +" = '1'", null);
                    HashMap<String,String> target_station_record  = (HashMap<String, String>) record.get(0);

                    if (cta_dataBase.delete_record("USER_FAVORITES", "STOP_ID = ?", new String[]{target_station_record.get("STOP_ID")})){
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(), tracking_station.get("target_station_name")+ " was removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
