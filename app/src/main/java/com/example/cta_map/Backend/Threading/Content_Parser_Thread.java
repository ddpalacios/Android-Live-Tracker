package com.example.cta_map.Backend.Threading;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Content_Parser_Thread implements Runnable
{

    final Message msg;
    HashMap<String, String> record;
    Database2 sqlite;
    android.os.Handler handler;
    boolean willCommunicate;
    String TAG = Thread.currentThread().getName();
    public Content_Parser_Thread(Message msg, HashMap<String, String> record,android.os.Handler handler ,Database2 sqlite, boolean willCommunicate){
        this.msg = msg;
        this.record = record;
        this.handler = handler;
        this.sqlite = sqlite;
        this.willCommunicate = willCommunicate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        android.os.Message handler_msg = this.handler.obtainMessage();


        Time time = new Time();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String target_station = this.record.get("station_name").replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        final ArrayList<String> stops = this.sqlite.get_column_values("line_stops_table", this.record.get("station_type").replaceAll(" ", "").toLowerCase());
        final ArrayList<String> modified_stops = new ArrayList<>();
        HashMap<String, ArrayList<HashMap>> parsed_train_data = new HashMap();
        List<String> modified_valid_stations;
        for (String each_stop : stops) { modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }

        while (this.msg.IsSending()) {
            synchronized (this.msg) {
                if (this.msg.getRawTrainContent() == null){
                        try { this.msg.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                        Log.e(TAG, "NULL object. Waiting. ");
                    }
                    ArrayList<HashMap> chosen_trains = new ArrayList<>();
                    ArrayList<HashMap> ignored_trains = new ArrayList<>();

                    if (this.record.get("station_dir").equals("1")) {
                        modified_valid_stations = modified_stops.subList(modified_stops.indexOf(target_station), modified_stops.size());
                    } else {
                        modified_valid_stations = modified_stops.subList(0, modified_stops.indexOf(target_station) + 1);
                    }
                    for (String raw_content: this.msg.getRawTrainContent()){
                        HashMap<String, String> current_train_info = chicago_transits.get_train_info(raw_content, record.get("station_type").replaceAll(" ", ""));
                        String modified_next_stop = current_train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                        if (current_train_info.get("train_direction").equals(this.record.get("station_dir")) && modified_valid_stations.contains(modified_next_stop)) {
                            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                    Double.parseDouble((String) Objects.requireNonNull(current_train_info.get("train_lat"))),
                                    Double.parseDouble((String) Objects.requireNonNull(current_train_info.get("train_lon"))),
                                    Double.parseDouble(Objects.requireNonNull(this.record.get("station_lat"))),
                                    Double.parseDouble(Objects.requireNonNull(this.record.get("station_lon"))));
                            int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                            current_train_info.put("train_eta", current_train_eta+"");
                            current_train_info.put("train_distance", current_train_distance_from_target_station+"");
                            chosen_trains.add(current_train_info);
                        }
                        if (!modified_valid_stations.contains(modified_next_stop) && current_train_info.get("train_direction").equals(this.record.get("station_dir"))) {
                            ignored_trains.add(current_train_info);
                        }
                    }

                    if (this.willCommunicate) {
                        Log.e(Thread.currentThread().getName(), "Chosen Trains: " + chosen_trains.size());
                        Log.e(Thread.currentThread().getName(), "Ignored Trains: " + ignored_trains.size());
                    }

                    parsed_train_data.put("chosen_trains", chosen_trains);
                    parsed_train_data.put("ignored", ignored_trains);
                    this.msg.setParsedTrainData(parsed_train_data);


                    this.sqlite.close();

                    this.msg.notify();
                    try {
                        this.msg.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
//        }
    }
}