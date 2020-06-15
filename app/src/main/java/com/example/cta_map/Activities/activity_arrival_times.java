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
import com.example.cta_map.Threading.StationProxy_Thread;
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
            display_results(bundle);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void display_results(Bundle bundle) {
        if (bundle.getSerializable("station_range_eta") !=null){
            Log.e(Thread.currentThread().getName(), "Recieved "+bundle.getSerializable("station_range_eta")  );
        }

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
        final Thread t3 = new Thread(new Station_Range_Estimation_Thread(message, train_id, sqlite, tracking_record ,false), "Station_Range_Estimation_Thread");
        t3.start();

        final Thread t4 = new Thread(new StationProxy_Thread(message, sqlite, tracking_record, train_id,true), "Station prox Thread");
        t4.start();

        final Thread t5 = new Thread(new Notifier_Thread(message, handler, getApplicationContext(), false), "Notifier Thread");
        t5.start();

        toMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t5.interrupt();
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
                t5.interrupt();
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
