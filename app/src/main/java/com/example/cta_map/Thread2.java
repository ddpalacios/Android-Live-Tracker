package com.example.cta_map;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

class Thread2 implements Runnable
{

    final Message msg;
    Bundle bb;
    DatabaseHelper sqlite;
    public Thread2 (Message msg, Bundle bb, DatabaseHelper sqlite){
        this.msg = msg;
        this.bb = bb;
        this.sqlite = sqlite;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        String target_station_name = bb.getString("station_name");
        String target_station_type = bb.getString("station_type");
        double target_station_lat = bb.getDouble("station_lat");
        double target_station_lon = bb.getDouble("station_lon");
        String target_station_direction = bb.getString("station_dir");

        List<String> ignored_stations;
        ArrayList<Integer> train_etas = new ArrayList<>();
        ArrayList<HashMap> chosen_trains = new ArrayList<>();

        final ArrayList<String> stops = sqlite.getValues("line_stops_table", target_station_type.toLowerCase());


        while (true) {


            Log.d("Thread", "Notifier thread active");
            try {
                synchronized (this.msg) {
                    Log.e("Sync", "Entered block");
                    Chicago_Transits chicago_transits = new Chicago_Transits();
                    Time times = new Time();

                    String[] content = msg.getMsg();
                    for (String each_train: content) {
                        HashMap<String, String> train_info = chicago_transits.get_train_info(each_train, target_station_type);
                        int start = 0;
                        int end = 0;
                        if (Objects.equals(train_info.get("train_direction"), target_station_direction)) {

                            if (target_station_direction.equals("1")) {
                                end = stops.indexOf(target_station_name.replaceAll("[^a-zA-Z0-9]", ""));

                            } else if (target_station_direction.equals("5")) {
                                start = stops.indexOf(target_station_name.replaceAll("[^a-zA-Z0-9]", "")) + 1;
                                end = stops.size();

                            }
                            if (start == -1) {
                                Log.e("ERROR", "STATION NOT FOUND");
                            } else if (end == -1) {
                                Log.e("ERROR", "STATION NOT FOUND");

                            } else {
                                ignored_stations = stops.subList(start, end);

                                String next_stop = train_info.get("next_stop");
                                if (!ignored_stations.contains(next_stop.replaceAll("[^a-zA-Z0-9]", ""))) {

                                    Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                            Double.parseDouble(Objects.requireNonNull(train_info.get("train_lat"))),
                                            Double.parseDouble(Objects.requireNonNull(train_info.get("train_lon"))),
                                            target_station_lat,
                                            target_station_lon);
                                    int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                                    train_etas.add(current_train_eta);
                                    chosen_trains.add(train_info);
                                    Collections.sort(train_etas);

                                }

                            }
                        }
                    }

                    Log.e("Thread", Thread.currentThread().getName()+" is done parsing the recieved message  and now Waiting..");
                    this.msg.setTrain_etas(train_etas);
                    this.msg.set_chosen_trains(chosen_trains);
                    this.msg.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("Thread", Thread.currentThread().getName()+" is parsing info again.." );
            train_etas.clear();
            chosen_trains.clear();
        }
    }
}
