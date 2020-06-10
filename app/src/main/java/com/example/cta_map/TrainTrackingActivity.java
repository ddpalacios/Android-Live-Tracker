package com.example.cta_map;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TrainTrackingActivity extends AppCompatActivity {

    Bundle bb; // Retrieve data from main screen

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
                Bundle bundle = msg.getData();
                ArrayList<Integer> etas = bundle.getIntegerArrayList("train_etas");
                ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");

            displayResults(etas, chosen_trains);
        }
    };

    public void displayResults(ArrayList<Integer> train_etas, ArrayList<HashMap> chosen_trains){
        String target_station_name = bb.getString("station_name");
        String target_station_type = bb.getString("station_type");
        String main_station = bb.getString("main_station");
        final String[] target_station_direction = new String[]{bb.getString("station_dir")};
        ArrayList<String> arrayList = new ArrayList<>();
        MapRelativeListView mapRelativeListView = new MapRelativeListView(getApplicationContext(), findViewById(R.id.train_layout_arrival_times));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        final ListView list = findViewById(R.id.train_layout_arrival_times);
        list.setAdapter(adapter);




        if (train_etas.size() == 0 ){
            if (target_station_direction.equals("1")) {
                arrayList.add(0, "No Trains Available." +" (North Bound)");
            }
            else if (target_station_direction.equals("5")){
                arrayList.add(0, "No Trains Available." +" (South Bound)");

            }
        }else {
            if (target_station_direction.equals("1")) {
                arrayList.add(0, target_station_name + " (North Bound)");
            } else if (target_station_direction.equals("5")) {
                arrayList.add(0, target_station_name + " (South Bound)");
            }

            for (Integer items : train_etas) {
                arrayList.add(main_station+" "+items + "m");
                adapter.notifyDataSetChanged();
            }

        }
}

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        bb = getIntent().getExtras();
        final String[] target_station_direction = new String[]{bb.getString("station_dir")};
        final Message message = new Message();


        final PipedReader r = new PipedReader();
        final PipedWriter w = new PipedWriter();
        try {
            r.connect(w);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        // write something
//        try {
//            Log.e("debug", "first data");
//            w.write(target_station_direction[0]);
//            w.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final Button switch_direction = (Button) findViewById(R.id.switch_direction);

        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Original ", target_station_direction[0]+"");

                if (target_station_direction[0].equals("1")){
                    target_station_direction[0] = "5";
                    synchronized (message){
                        message.setDir(target_station_direction[0]);

                    }

                }else {
                    target_station_direction[0] = "1";
                    synchronized (message){
                        message.setDir(target_station_direction[0]);
                    }

                }
                Log.e("New ", target_station_direction[0]+"");

            }
        });





        Thread t1 = new Thread(new Thread1(message, bb), "API_CALL_Thread");
        t1.start();
        Thread t2 = new Thread(new Thread2(message, bb, sqlite, r), "Content Parser");
        t2.start();
        Thread t3 = new Thread(new Thread3(message, handler), "Displayer");
        t3.start();


    }


//        mapRelativeListView.add_to_list_view(train_etas, TRAIN_RECORD, current_train_info,chosen_trains, connect, current_train_info.get("train_direction"));
}
