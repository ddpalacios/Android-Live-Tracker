package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressLint("Registered")
public class activity_arrival_times extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_arrival_times);
        super.onCreate(savedInstanceState);
        Time time = new Time();
        Context context = getApplicationContext();
        HashMap<String, String> current_train_info = (HashMap<String, String>) getIntent().getExtras().get("current_train_info");
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader train_station_stops_reader = chicago_transits.setup_file_reader(context, R.raw.train_line_stops);
        ArrayList<String> all_stops = chicago_transits.retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"));
        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        final String next_stop = bb.getString("next_stop");

        String specified_train_direction = current_train_info.get("train_direction");
        String target_station =  current_train_info.get("target_station");
        if (specified_train_direction.equals("1")){

            int start = all_stops.indexOf(target_station.replaceAll("[^a-zA-Z0-9]", ""));
            int end = all_stops.indexOf(next_stop)+1;
            List<String> all_stops_till_target = all_stops.subList(start , end);
            int idx = all_stops_till_target.size() -1;
            for (int i=0; i < all_stops_till_target.size(); i++){
                BufferedReader train_station_coordinates_reader = chicago_transits.setup_file_reader(context, R.raw.train_stations);
                String remaining_stop = all_stops_till_target.get(idx);
                String[] remaining_station_coordinates = chicago_transits.retrieve_station_coordinates(train_station_coordinates_reader, remaining_stop, current_train_info.get("station_type"));
                String[] current_train_loc = (current_train_info.get("train_lat") + ","+current_train_info.get("train_lon")).split(",");
                double train_distance_to_next_stop = chicago_transits.calculate_coordinate_distance(
                        Double.parseDouble(current_train_loc[0]),
                        Double.parseDouble(current_train_loc[1]),
                        Double.parseDouble(remaining_station_coordinates[0]),
                        Double.parseDouble(remaining_station_coordinates[1]));

                int next_stop_eta = time.get_estimated_time_arrival(25, train_distance_to_next_stop);



                Log.e("remaining", "ETA To "+remaining_stop +": "+ next_stop_eta+" Minutes");



                idx--;
            }





        }
        else if (specified_train_direction.equals("5")){


        }






    }


}
