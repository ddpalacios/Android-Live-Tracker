package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
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
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;
import com.example.cta_map.Threading.Message;
import com.example.cta_map.Threading.API_Caller_Thread;
import com.example.cta_map.Threading.Content_Parser_Thread;
import com.example.cta_map.Threading.Notifier_Thread;
import com.example.cta_map.Threading.Train_Estimations_Thread;

import java.util.ArrayList;
import java.util.HashMap;

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
        if (bundle.getBoolean("No_Trains")){ Log.e("No trains", bundle.getBoolean("No_Trains")+"");return; }

        final ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");
        String main_station = bundle.getString("main_station");
        if (main_station == null) { main_station = tracking_record.get("main_station_name"); }
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        final ListView list = findViewById(R.id.train_layout_arrival_times);
        list.setAdapter(adapter);
        arrayList.add(0, tracking_record.get("station_name")+" ("+tracking_record.get("station_type")+")");
        for (HashMap<String, String> train: chosen_trains){
            arrayList.add("Train #"+train.get("train_id")+". "+main_station+": "+String.valueOf(train.get("train_eta"))+"m");
            adapter.notifyDataSetChanged();
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TrainTrackingActivity.this, activity_arrival_times.class);
                String train_item = (String) list.getItemAtPosition(position).toString().substring(7,10);
                for (HashMap<String, String> train: chosen_trains){
                    if (train.get("train_id").equals(train_item)){
                        Toast.makeText(getApplicationContext(), "Found: "+train, 1).show();
                            message.keepSending(false);
                            intent.putExtra("chosen_train", train);
                        startActivity(intent);
                    }
                }



            }
        });

        sqlite.close();




}

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final HashMap<String, String> tracking_record = sqlite.getAllRecord("tracking_table");

        if (tracking_record == null || tracking_record.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tracking Station Found in DB!", Toast.LENGTH_LONG).show();
            return;
        }


        Log.e("record", tracking_record+"");
        message.setClicked(false);
        message.keepSending(true);
        message.setTargetContent(tracking_record);
        final Button switch_direction = (Button) findViewById(R.id.switch_direction);

        final Button choose_station = (Button) findViewById(R.id.pickStation);
        final Button toMaps = (Button) findViewById(R.id.show);

        final Thread api_call_thread = new Thread(new API_Caller_Thread(message, tracking_record, handler,false), "API_CALL_Thread");
        api_call_thread.start();
        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "Content Parser");
        t2.start();
        final Thread t3 = new Thread(new Train_Estimations_Thread(message, false), "Estimation Thread");
        t3.start();
        final Thread t4 = new Thread(new Notifier_Thread(message, handler, getApplicationContext(), false), "Notifier Thread");
        t4.start();

        toMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t4.interrupt();
                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);


                synchronized (message){
                    message.keepSending(false);
                }


                startActivity(intent);
            }
        });


        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t4.interrupt();
                Intent intent = new Intent(TrainTrackingActivity.this, mainactivity.class);
                Integer profile_id = Integer.parseInt(tracking_record.get("profile_id"));
                final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+profile_id+"'");
                intent.putExtra("profile_id", user_record.get(0));
                synchronized (message){
                    message.keepSending(false);
                }

                startActivity(intent);


            }
        });


        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target_station_direction;
                String main_station;
                if (message.getDir() ==null) {
                    target_station_direction = tracking_record.get("station_dir");
                    main_station = tracking_record.get("main_station_name");


                }else{
                    target_station_direction = message.getDir();
                    main_station = message.getMainStation();

                }

                t4.interrupt();
                if (target_station_direction.equals("1")){
                    target_station_direction = "5";
                    main_station = sqlite.get_table_record("main_stations_table", "WHERE train_line = '"+ tracking_record.get("station_type") + "'").get(3);

                    sqlite.update_value(tracking_record.get("profile_id"), "tracking_table", "station_dir", target_station_direction);
                    sqlite.update_value(tracking_record.get("profile_id"), "tracking_table", "main_station_name", main_station);

                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setMainStation(main_station);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }else {
                    target_station_direction = "1";
                    sqlite.update_value(tracking_record.get("profile_id"), "tracking_table", "station_dir", target_station_direction);
                    main_station = sqlite.get_table_record("main_stations_table",
                            "WHERE train_line = '"+
                                    tracking_record.get("station_type") + "'").get(2);
                    sqlite.update_value(tracking_record.get("profile_id"), "tracking_table", "main_station_name", main_station);

                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setMainStation(main_station);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }

            }
        });
    }


}
