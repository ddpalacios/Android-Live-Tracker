package com.example.cta_map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TrainTrackingActivity extends AppCompatActivity implements TrainDirection{
    final boolean[] connect = {true};
    List<String> ignored_stations;
    ArrayList<Integer> train_etas = new ArrayList<>();
    ArrayList<HashMap> chosen_trains = new ArrayList<>();
    Bundle bb; // Retrieve data from main screen
    Boolean[] green = new Boolean[] {false};
    Boolean[] yellow = new Boolean[] {false};
    Boolean[] pink = new Boolean[] {false};


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Chicago_Transits chicago_transits = new Chicago_Transits();

        super.onCreate(savedInstanceState);
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
        bb=getIntent().getExtras();
        assert bb != null;
        final SharedPreferences TRAIN_RECORD = getSharedPreferences("Train_Record", MODE_PRIVATE);
        final String[]  specified_train_direction = new String[]{String.valueOf(TRAIN_RECORD.getInt("station_dir", 5))};
        final String target_station_name = TRAIN_RECORD.getString("station_name", null);
        final String target_station_type =  TRAIN_RECORD.getString("station_type", null);
        Log.e("record", TRAIN_RECORD.getFloat("station_lat", 0)+ "");
        final Button hide = initiate_button(R.id.show);
        final Button switch_direction = initiate_button(R.id.switch_direction);
        final Button choose_station = initiate_button(R.id.pickStation);
        final Switch notify_switch = (Switch) findViewById(R.id.switch1);


        choose_station.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainTrackingActivity.this, mainactivity.class);
                connect[0] = false;
                Log.d("Connection Status", "Connection Closed");
                startActivity(intent);
            }
        });



        hide.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                connect[0]= false;
                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());

        final ArrayList<String> stops = sqlite.getValues("line_stops_table", target_station_type.toLowerCase());
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(TRAIN_RECORD.getString("station_type", null).toLowerCase()));
        final String[] target_station_coordinates = new String[]{String.valueOf(TRAIN_RECORD.getFloat("train_lat", 0)), String.valueOf(TRAIN_RECORD.getFloat("train_lon", 0)) };

        Log.e("url", url);
        /*

          Everything is being ran within its own thread.
         This allows us to run our continuous web extraction
         while also performing other user interactions

          */

        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (connect[0]) {
                    try {
                        final Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale", "WrongConstant", "ShowToast", "NewApi"})
                            @Override
                            public void run() {
                                final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                                if (train_list.length > 1) {
                                    for (String each_train : train_list) {
                                        HashMap<String, String> train_info = chicago_transits.get_train_info(each_train, target_station_type);
                                        int start = 0;
                                        int end = 0;
                                        if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
                                            train_info.put("target_station_lat", target_station_coordinates[0]);
                                            train_info.put("target_station_lon", target_station_coordinates[1]);
                                            if (specified_train_direction[0].equals("1")) {
                                                end = stops.indexOf(target_station_name.replaceAll("[^a-zA-Z0-9]", ""));
                                            } else if (specified_train_direction[0].equals("5")) {
                                                start = stops.indexOf(target_station_name.replaceAll("[^a-zA-Z0-9]", "")) + 1;
                                                end = stops.size();

                                            }
                                            setup_train_direction(train_info, stops, start, end, Integer.parseInt(specified_train_direction[0]), getApplicationContext());
                                        }
                                    }
                                }
                                Log.d("Update", "DONE HERE.");
                            }
                        });

                        switch_direction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                green[0] = false;
                                yellow[0] = false;
                                pink[0] = false;
                                notify_switch.setChecked(false);
                                Thread.currentThread().interrupt();
                                Toast.makeText(getApplicationContext(), "Switching Directions. Please Wait...", Toast.LENGTH_SHORT).show();

                                if (specified_train_direction[0].equals("1")){
                                    specified_train_direction[0] = "5";

                                }else {
                                    specified_train_direction[0] = "1";
                                }
                            }
                        });


                        train_etas.clear();
                        chosen_trains.clear();
                        Thread.sleep(500);
                    }catch (IOException | InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();



    }
    private Button initiate_button(int widget) {
        Button button = findViewById(widget);
        return button;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context) {
        SharedPreferences TRAIN_RECORD = getSharedPreferences("Train_Record", MODE_PRIVATE);

        final String[] target_station_coordinates = new String[]{String.valueOf(TRAIN_RECORD.getFloat("station_lat", 0)), String.valueOf(TRAIN_RECORD.getFloat("station_lon", 0))};
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapRelativeListView mapRelativeListView = new MapRelativeListView(context, findViewById(R.id.train_layout_arrival_times));
        Time times = new Time();
        ignored_stations = stops.subList(start, end);
        String next_stop = current_train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "");

        if (!ignored_stations.contains(next_stop)) {


            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(current_train_info.get("train_lat")),
                    Double.parseDouble(current_train_info.get("train_lon")),
                    Double.parseDouble(target_station_coordinates[0]),
                    Double.parseDouble(target_station_coordinates[1]));




            int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
            train_etas.add(current_train_eta);
            Collections.sort(train_etas);
            chosen_trains.add(current_train_info);
            current_train_info.put(String.valueOf(current_train_eta), next_stop);
        }
        mapRelativeListView.add_to_list_view(train_etas, TRAIN_RECORD, current_train_info,chosen_trains, connect, current_train_info.get("train_direction"));
    }
        }

