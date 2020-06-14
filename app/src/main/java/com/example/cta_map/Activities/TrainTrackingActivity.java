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
                ArrayList<Integer> etas = bundle.getIntegerArrayList("train_etas");
//                String train_dir = bundle.getString("train_dir");
                ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");


//            displayResults(etas, chosen_trains, train_dir);
        }
    };

    public void displayResults(ArrayList<Integer> train_etas, final ArrayList<HashMap> chosen_trains, String train_dir){
        String target_station_name = bb.getString("station_name");
        String target_station_type = bb.getString("station_type");
        String main_station;
//        Log.e("New ", train_dir+"");

        final String[] target_station_direction = new String[]{bb.getString("station_dir")};
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        final ListView list = findViewById(R.id.train_layout_arrival_times);
        list.setAdapter(adapter);
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());

        ArrayList<String> table_record = sqlite.get_table_record("main_stations_table",
                "WHERE train_line = '"+target_station_type.replaceAll("\\)", "'")+"'");

        if (train_etas.size() == 0 ){
            if (train_dir.equals("1")) {
                arrayList.add(0, "No Trains Available." +" (North Bound)");
            }
            else if (train_dir.equals("5")){
                arrayList.add(0, "No Trains Available." +" (South Bound)");

            }
        }else {

            if (train_dir.equals("1")) {
//                Log.e("station", "upadting title: ");
                arrayList.add(target_station_name + " (North Bound)");
            } else if (train_dir.equals("5")) {
                arrayList.add(target_station_name + " (South Bound)");
            }

            for (Integer items : train_etas) {

                if (train_dir.equals("1")){
                    main_station = table_record.get(2);
                }

                else  {
                    main_station = table_record.get(3);
                }


                arrayList.add(main_station+": "+items + "m");
                adapter.notifyDataSetChanged();
            }


        }
        sqlite.close();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                Intent intent = new Intent(TrainTrackingActivity.this, activity_arrival_times.class);
                if (position == 0 ){
                    Toast.makeText(context, list.getItemAtPosition(position)+"", Toast.LENGTH_SHORT).show();
                }
                else {

                    String[] list_item = String.valueOf(list.getItemAtPosition(position)).split(":");
                    String key = list_item[1].replaceAll("[^\\d.]", "");
                    for (HashMap<String, String> each_train : chosen_trains) {
                        String train_eta = each_train.get("train_eta");
                        if (train_eta.equals(key)){
                            Log.e("next ", "TRAIN ID: " +each_train.get("train_id") + "");
                            intent.putExtra("current_train_info", each_train);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                message.keepSending(false);
                            startActivity(intent);
                        }
                    }
                }


            }
        });





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

        final Thread api_call_thread = new Thread(new API_Caller_Thread(message, tracking_record, false), "API_CALL_Thread");
        api_call_thread.start();
        final Thread t2 = new Thread(new Content_Parser_Thread(message, tracking_record, sqlite, false), "Content Parser");
        t2.start();
        final Thread t3 = new Thread(new Train_Estimations_Thread(message, false), "Estimation Thread");
        t3.start();
//        final Thread t4 = new Thread(new Notifier_Thread(message, handler, getApplicationContext(), true), "Notifier Thread");
//        t4.start();

        toMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                t4.interrupt();
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
//                t4.interrupt();
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
                if (message.getDir() ==null) {
                    target_station_direction = tracking_record.get("station_dir");
                }else{
                        target_station_direction = message.getDir();

                }

//                t4.interrupt();
                if (target_station_direction.equals("1")){
                    target_station_direction = "5";
                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }else {
                    target_station_direction = "1";
                    synchronized (message){
                        message.setDir(target_station_direction);
                        message.setClicked(true);
                        message.notifyAll();
                    }

                }

            }
        });
//
    }


}
