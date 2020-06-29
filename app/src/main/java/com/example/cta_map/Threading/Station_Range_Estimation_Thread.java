package com.example.cta_map.Threading;

import android.util.Log;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Chicago_Transits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Station_Range_Estimation_Thread implements Runnable {
    final Message msg;
    String id;
    DatabaseHelper sqlite;
    HashMap<String, String> record;
    boolean willCommunicate;
    public Station_Range_Estimation_Thread(Message msg, String id, DatabaseHelper sqlite, HashMap<String,String> record, boolean willCommunicate){
        this.msg = msg;
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

                    }
                }
            }
        }
    }
}
