package com.example.cta_map.Threading;

import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class Train_Estimations_Thread implements Runnable {
    Message msg;
    boolean willCommunicate;

    public Train_Estimations_Thread(Message msg, boolean willCommunicate) {
        this.msg = msg;
        this.willCommunicate = willCommunicate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        Time time = new Time();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            synchronized (this.msg) {
                HashMap<String, String> target_station_record = this.msg.getTargetContent();
                String target_station_lat = target_station_record.get("station_lat");
                String target_station_lon = target_station_record.get("station_lon");


                while (this.msg.IsSending()) {
                    if (this.msg.get_chosen_trains() == null || this.msg.getIgnored() == null || this.msg.getTargetContent() == null) {
                        Log.e(Thread.currentThread().getName(), "Data is NULL");
                        return;
                    }
                    if (this.msg.get_chosen_trains().size() == 0) {
                        Log.e("No trains", "No Trains Available");
                    }


                    for (HashMap valid_trains : this.msg.get_chosen_trains()) {
                        Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                Double.parseDouble((String) valid_trains.get("train_lat")),
                                Double.parseDouble((String) valid_trains.get("train_lon")),
                                Double.parseDouble(target_station_lat),
                                Double.parseDouble(target_station_lon));
                        int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                        valid_trains.put("train_eta", current_train_eta);
                    }

                    if (this.willCommunicate) {
                        Log.e(Thread.currentThread().getName(), "Is waiting...");
                    }
                    try {
                        this.msg.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.willCommunicate) {
                        Log.e(Thread.currentThread().getName(), "Is done waiting...");
                    }


                }
            }


        } catch (Exception e) {
//            Log.e(",essage", this.msg.getTargetContent() + "");
        }
    }
}
