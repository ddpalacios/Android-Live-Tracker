package com.example.cta_map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
    Boolean[] t = new Boolean[] {false};
    Boolean[] yel = new Boolean[] {false};
    Boolean[] pink = new Boolean[] {false};
    GoogleMap mMap;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.train_tracking_activity);
        final Chicago_Transits chicago_transits = new Chicago_Transits();

        super.onCreate(savedInstanceState);
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
        bb=getIntent().getExtras();
        assert bb != null;
        final String target_station_type = bb.getString("target_station_type");
        final String target_station_name = bb.getString("target_station_name");
        final String[] specified_train_direction = {bb.getString("train_direction")};
        final Button hide = initiate_button(R.id.show);
        final Button switch_direction = initiate_button(R.id.switch_direction);
        final Button choose_station = initiate_button(R.id.pickStation);

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

        switch_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t[0] = false;
                yel[0] = false;
                pink[0] = false;
                Thread.currentThread().interrupt();
                Toast.makeText(getApplicationContext(), "Switching Directions. Please Wait...", Toast.LENGTH_SHORT).show();

                if (specified_train_direction[0].equals("1")){
                    specified_train_direction[0] = "5";

                }else {
                    specified_train_direction[0] = "1";
                }
            }
        });



        BufferedReader train_station_csv_reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
        final String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(train_station_csv_reader, target_station_name, target_station_type);


        final ArrayList<String> stops = chicago_transits.retrieve_line_stations(chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops), target_station_type);
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(target_station_type.toLowerCase()));
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
                        final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint({"SetTextI18n", "LongLogTag", "DefaultLocale", "WrongConstant", "ShowToast", "NewApi"})
                            @Override
                            public void run() {
                                final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                                for (String each_train : train_list) {
                                    HashMap<String, String> train_info = chicago_transits.get_train_info(chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations), each_train,target_station_name ,target_station_type);
                                    int start = 0;
                                    int end =0;
                                    if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
                                        train_info.put("target_station_lat", target_station_coordinates[0]);
                                        train_info.put("target_station_lon", target_station_coordinates[1]);
                                        if (specified_train_direction[0].equals("1")) {
                                            end = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", ""));
//
                                        } else if (specified_train_direction[0].equals("5")) {
                                            start = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", "")) + 1;
                                            end = stops.size();

                                        }
                                        setup_train_direction(train_info, stops, start, end, Integer.parseInt(specified_train_direction[0]), getApplicationContext());


                                        }
                                }
                                Log.d("Update", "DONE HERE.");
                            }
                        });
                        if (train_etas.size() != 0){
                            Context context = getApplicationContext();
                            UserLocation userLocation = new UserLocation(context);
                            Intent intent = new Intent(TrainTrackingActivity.this, mainactivity.class);
                            int closest_train_eta = train_etas.get(0);
                            for (HashMap<String, String>current_train : chosen_trains){
                                if (current_train.containsKey(String.valueOf(closest_train_eta))){
                                    userLocation.getLastLocation(intent, getApplicationContext(), current_train, closest_train_eta, t, yel, pink, mMap, false);


                                }
                            }
                            if (closest_train_eta == 0){
                                t[0] = false;
                                yel[0] =false;
                                pink[0] = false;
                            }

                        }


                        train_etas.clear();
                        chosen_trains.clear();
                        Thread.sleep(1000);
                    }catch (IOException | InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        hide.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                connect[0]= false;
                Intent intent = new Intent(TrainTrackingActivity.this, MapsActivity.class);
                intent.putExtra("target_station_type", target_station_type);
                intent.putExtra("target_station_name", target_station_name);
                intent.putExtra("train_direction", specified_train_direction[0]);
                Log.e("ddd", specified_train_direction[0] + " "+ target_station_name + " "+ target_station_type);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }
    private Button initiate_button(int widget) {
        Button button = findViewById(widget);
        return button;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapRelativeListView mapRelativeListView = new MapRelativeListView(context,findViewById(R.id.train_layout_arrival_times));
        BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
        String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(reader, current_train_info.get("target_station"), current_train_info.get("station_type"));
        Time times = new Time();

        ignored_stations = stops.subList(start, end);
        String next_stop = Objects.requireNonNull(current_train_info.get("next_stop")).replaceAll("[^a-zA-Z0-9]", "");

        if (ignored_stations.contains(next_stop)) {

        }else {
            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lon"))));
            int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
            train_etas.add(current_train_eta);
            Collections.sort(train_etas);
            chosen_trains.add(current_train_info);
            current_train_info.put(String.valueOf(current_train_eta), next_stop);
        }
        mapRelativeListView.add_to_list_view(train_etas, current_train_info, chosen_trains, connect);
    }
}
