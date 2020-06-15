package com.example.cta_map.Threading;

import android.util.Log;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Chicago_Transits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Station_Range_Estimation_Thread implements Runnable {
    final Message msg;
    String type;
    String id;
    DatabaseHelper sqlite;
    HashMap<String, String> record;
    boolean willCommunicate;
    public Station_Range_Estimation_Thread(Message msg, String id, DatabaseHelper sqlite, HashMap<String,String> record, boolean willCommunicate){
        this.msg = msg;
        this.type = type;
        this.id = id;
        this.sqlite = sqlite;
        this.record = record;
        this.willCommunicate = willCommunicate;
    }
    @Override
    public void run() {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (this.msg){
            final ArrayList<String> stops = this.sqlite.get_column_values("line_stops_table", this.record.get("station_type").toLowerCase());
            final ArrayList<String> modified_stops = new ArrayList<>();
            for (String each_stop: stops){ modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }
            while (this.msg.IsSending()) {
                ArrayList<HashMap> chosen_trains = this.msg.get_chosen_trains();
                for (HashMap<String, String> train : chosen_trains){
                    if (train.get("train_id").equals(this.id)){
                        String train_next_stop = train.get("next_stop");
                        String direction = train.get("train_direction");
                        List<String> sublist = null;

                        if (direction.equals("1")){
                           int next_stop_idx = modified_stops.indexOf(train_next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
                           int target_idx = modified_stops.indexOf(record.get("station_name").replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
                            sublist = stops.subList(target_idx, next_stop_idx);
                        }
                        else if (direction.equals("5")){
                            int next_stop_idx = modified_stops.indexOf(train_next_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
                            int target_idx = modified_stops.indexOf(record.get("station_name").replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
                            sublist = stops.subList(next_stop_idx, target_idx+1);
                        }
                        if (sublist == null){Log.e("Data Error","SUb list is null");return;}

                        this.msg.setSubStations(sublist);

                        if (this.willCommunicate){Log.e(Thread.currentThread().getName(), "Is waiting...");}
                        try {
                            this.msg.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (this.willCommunicate){Log.e(Thread.currentThread().getName(), "Is Done waiting...");}



//                        Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                                Double.parseDouble(train.get("train_lat")),
//                                Double.parseDouble(train.get("train_lon")),
//                                Double.parseDouble(target_station_lat),
//                                Double.parseDouble(target_station_lon));
//                        int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
//                        train.put("train_eta", String.valueOf(current_train_eta));




                    }

                }









            }
        }


//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                String[] content = this.msg.getMsg();
//                if (content == null){
//                    Log.e("Content", "is null.");
//                    continue;
//                }
//
//
//                for (String each_train : content) {
//                    HashMap<String, String> train_info = chicago_transits.get_train_info(each_train, this.type);
//                    if (train_info.get("train_id").equals(id)) {
//                        String train_lat = train_info.get("train_lat");
//                        String train_lon = train_info.get("train_lon");
//                        String train_next_stop = train_info.get("next_stop");
//                        String train_direction = train_info.get("train_direction");
////                        Log.e("NEXT", train_next_stop);
//
//
//
//
//
//                        this.msg.setDir(train_direction);
//                        this.msg.setCoord(train_lat, train_lon);
//                        this.msg.setNextStop(train_next_stop);
//
//
////                        Log.e("FOUND", train_info + "");
//
//                    }
//                }
//                    try {
//                    this.msg.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                Log.e(Thread.currentThread().getName(), "Done waiting");
//





    }
}
