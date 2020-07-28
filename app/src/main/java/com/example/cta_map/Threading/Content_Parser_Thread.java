package com.example.cta_map.Threading;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Content_Parser_Thread implements Runnable
{

    final Message msg;
    HashMap<String, String> record;
    Database2 sqlite;
    boolean willCommunicate;
    public Content_Parser_Thread(Message msg, HashMap<String, String> record, Database2 sqlite, boolean willCommunicate){
        this.msg = msg;
        this.record = record;
        this.sqlite = sqlite;
        this.willCommunicate = willCommunicate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String target_station = this.record.get("station_name").replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String station_direction = this.record.get("station_dir");
        final ArrayList<String> stops = this.sqlite.get_column_values("line_stops_table", this.record.get("station_type").replaceAll(" ", "").toLowerCase());
        final ArrayList<String> modified_stops = new ArrayList<>();
        List<String> modified_valid_stations;
        for (String each_stop : stops) {
            modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        }
        this.sqlite.close();
        while (this.msg.IsSending()) {

            synchronized (this.msg) {
                if (this.msg.isWaiting()) {

                    String[] content = this.msg.getMsg();
                    ArrayList<HashMap> chosen_trains = new ArrayList<>();
                    ArrayList<HashMap> ignored_trains = new ArrayList<>();
                    if (this.msg.getDir() != null) {
                        station_direction = this.msg.getDir();
                    }
                    if (station_direction.equals("1")) {
                        modified_valid_stations = modified_stops.subList(modified_stops.indexOf(target_station), modified_stops.size());
                    } else {
                        modified_valid_stations = modified_stops.subList(0, modified_stops.indexOf(target_station) + 1);
                    }

                    for (String raw_content : content) {
                        HashMap<String, String> current_train_info = chicago_transits.get_train_info(raw_content, record.get("station_type").replaceAll(" ", ""));
                        String modified_next_stop = current_train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                        if (current_train_info.get("train_direction").equals(station_direction) && modified_valid_stations.contains(modified_next_stop)) {
                            chosen_trains.add(current_train_info);
                        }
                        if (!modified_valid_stations.contains(modified_next_stop) && current_train_info.get("train_direction").equals(station_direction)) {
                            ignored_trains.add(current_train_info);
                        }
                    }

                    this.msg.set_chosen_trains(chosen_trains);
                    this.msg.setIgnored(ignored_trains);

                    if (this.willCommunicate) {
                        Log.e(Thread.currentThread().getName(), "Chosen Trains: " + chosen_trains.size());
                        Log.e(Thread.currentThread().getName(), "Ignored Trains: " + ignored_trains.size());
                        Log.e(Thread.currentThread().getName(), " Is waiting...");
                    }
                    this.msg.notify(); // Sets up another API call and we waiting until its ready to be parsed

                    try {
                        this.sqlite.close();
                        this.msg.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}