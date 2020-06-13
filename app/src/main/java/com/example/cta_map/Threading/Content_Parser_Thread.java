package com.example.cta_map.Threading;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

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
    DatabaseHelper sqlite;
    boolean willCommunicate;
    public Content_Parser_Thread(Message msg, HashMap<String, String> record, DatabaseHelper sqlite, boolean willCommunicate){
        this.msg = msg;
        this.record = record;
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
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String target_station = this.record.get("station_name").replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String station_direction = this.record.get("station_dir");
        final ArrayList<String> stops = this.sqlite.get_column_values("line_stops_table", this.record.get("station_type").toLowerCase());
        final ArrayList<String> modified_stops = new ArrayList<>();
        List<String> modified_valid_stations;
        for (String each_stop: stops){ modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }


        synchronized (this.msg){

            if (this.msg.getDir() != null) {
                    station_direction = this.msg.getDir();
                }
            if (station_direction.equals("1")){
                modified_valid_stations = modified_stops.subList(modified_stops.indexOf(target_station), modified_stops.size());
            }else{
                modified_valid_stations =modified_stops.subList(0, modified_stops.indexOf(target_station)+1);
            }
            while (this.msg.IsSending()){
                ArrayList<HashMap> chosen_trains = new ArrayList<>();
                ArrayList<HashMap> ignored_trains = new ArrayList<>();

                String[] content = this.msg.getMsg();
                if (content == null){
                    Log.e("TRAIN CONTENT ERROR", null+"");
                    return;
                }

                for (String raw_content : content){
                    HashMap<String, String> current_train_info = chicago_transits.get_train_info(raw_content, record.get("station_type"));
                    String modified_next_stop = current_train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    if (current_train_info.get("train_direction").equals(station_direction) && modified_valid_stations.contains(modified_next_stop)){
                        chosen_trains.add(current_train_info);


                    }else{
                        ignored_trains.add(current_train_info);
                    }
                }


                this.msg.set_chosen_trains(chosen_trains);
                this.msg.setIgnored(ignored_trains);
                if (this.willCommunicate){
                    Log.e(Thread.currentThread().getName(), " Is waiting...");
                }


                try {
                    this.msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (this.willCommunicate){
                    Log.e(Thread.currentThread().getName(), " Is done waiting...");
                }
            }
        }


    }
}

//        String target_station_name = record.get("station_name");
//        String target_station_type = record.get("station_type");
//        double target_station_lat = Double.parseDouble(record.get("station_lat"));
//        double target_station_lon = Double.parseDouble(record.get("station_lon"));
//        String target_station_direction = record.get("station_dir");
//        ArrayList<Integer> train_etas = new ArrayList<>();
//        ArrayList<HashMap> chosen_trains = new ArrayList<>();
//
//
//
//        final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", target_station_type.toLowerCase());
//        final ArrayList<String> modified_stops = new ArrayList<>();
//        for (String each_stop: stops){
//            modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//
//        }
//
//
//
//        synchronized (this.msg) {
//        while (this.msg.IsSending()) {
//            ArrayList<String> modified_ignored_stations = new ArrayList<>();
//            List<String> ignored_stations = null;
//
//            ArrayList<HashMap> ignored_station = new ArrayList<>();
//            if (this.msg.getDir() != null) {
//                    target_station_direction = this.msg.getDir();
//                }
//
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                Time times = new Time();
//
//                String[] content = this.msg.getMsg();
//                for (String each_train : content) {
//                    HashMap<String, String> train_info = chicago_transits.get_train_info(each_train, target_station_type);
//                    int start = 0;
//                    int end = 0;
//                    this.msg.setDir(target_station_direction);
//                    if (Objects.equals(train_info.get("train_direction"), target_station_direction)) {
//
//                        if (target_station_direction.equals("1")) {
//                            end = modified_stops.indexOf(target_station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//
//                        } else if (target_station_direction.equals("5")) {
//                            start = modified_stops.indexOf(target_station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()) + 1;
//                            end = stops.size();
//
//                        }
//                        if (start == -1 || end ==-1) {
//                            Log.e("ERROR", "STATION NOT FOUND");
//                            Log.e("stops", stops+"");
//                            Log.e("target", target_station_name.replaceAll("[^a-zA-Z0-9]", "")+"");
//                            return;
//                        }
//                            ignored_stations = stops.subList(start, end);
//                            for (String ignored: ignored_stations){
//                                modified_ignored_stations.add(ignored.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//                            }
//
//
//
//
//
//
//                            String next_stop = train_info.get("next_stop");
//
//                            Log.e("ignored", next_stop +" ig "+ modified_ignored_stations);
//                            if (!modified_ignored_stations.contains(next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
//
//                                Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lat"))),
//                                        Double.parseDouble(Objects.requireNonNull(train_info.get("train_lon"))),
//                                        target_station_lat,
//                                        target_station_lon);
//                                int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
//                                train_info.put("train_eta", String.valueOf(current_train_eta));
//                                train_etas.add(current_train_eta);
//                                chosen_trains.add(train_info);
//                                Collections.sort(train_etas);
//
//                            }else{
//                                ignored_station.add(train_info);
//
//                            }
//                    }
//                    modified_ignored_stations.clear();
//
//                }
//
//
//                this.msg.setTrain_etas(train_etas);
//                this.msg.setIgnored(ignored_station);
//            train_etas.clear();
//            chosen_trains.clear();
//            ignored_station.clear();
//            ignored_stations.clear();
//                sqlite.close();
//                this.msg.set_chosen_trains(chosen_trains);
//                try {
//                    if (this.willCommunicate) {
//                        Log.e("update", Thread.currentThread().getName() + " is waiting");
//                    }
//                    this.msg.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//            if (this.willCommunicate) {
//                Log.e("update", Thread.currentThread().getName() + " is done waiting");
//            }
//            }
//
//        }

