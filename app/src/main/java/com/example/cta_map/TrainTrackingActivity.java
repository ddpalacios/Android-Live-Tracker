package com.example.cta_map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
                String train_dir = bundle.getString("train_dir");
                ArrayList<HashMap> chosen_trains = (ArrayList<HashMap>) bundle.getSerializable("chosen_trains");


            displayResults(etas, chosen_trains, train_dir);
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
                            synchronized (message){
                                message.keepSending(false);
                            }
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
        bb = getIntent().getExtras();
        final String[] target_station_direction = new String[]{bb.getString("station_dir")};
        message.setClicked(false);
        message.keepSending(true);
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        final Button switch_direction = (Button) findViewById(R.id.switch_direction);
        final Button choose_station = (Button) findViewById(R.id.pickStation);
        final Button toMaps = (Button) findViewById(R.id.show);
        String target_station_type = bb.getString("station_type");







        final Thread t1 = new Thread(new Thread1(message, target_station_type), "API_CALL_Thread");
        t1.start();
        final Thread t2 = new Thread(new Thread2(message, bb, sqlite), "Content Parser");
        t2.start();
        final Thread t3 = new Thread(new Thread3(message, handler, getApplicationContext()), "Displayer");
        t3.start();



        toMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });



        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.widget.Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TrainTrackingActivity.this, mainactivity.class);
                synchronized (message){
                    message.keepSending(false);
                }

                startActivity(intent);


            }
        });


        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Original ", target_station_direction[0]+"");

                if (target_station_direction[0].equals("1")){
                    target_station_direction[0] = "5";
                    synchronized (message){
                        message.setDir(target_station_direction[0]);
                        message.setClicked(true);
                        message.notifyAll();
                        try{
                            t3.interrupt();
                        }catch(Exception e){Log.e("fff","Exception handled "+e);}
                    }

                }else {
                    target_station_direction[0] = "1";
                    synchronized (message){
                        message.setDir(target_station_direction[0]);
                        message.setClicked(true);
                        message.notifyAll();
                        try{
                            t3.interrupt();

                        }catch(Exception e){Log.e("fff","Exception handled "+e);}
                    }

                }

            }
        });

    }


}
