package com.example.cta_map.Threading;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.IntegerRes;
import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.UserLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Handler;

public class Train_Estimations_Thread implements Runnable {
    Message msg;
    android.os.Handler handler;

    boolean willCommunicate;
    UserLocation userLocation;
    Context context;
    public Train_Estimations_Thread(Message msg, UserLocation userLocation,  android.os.Handler handler, Context context, boolean willCommunicate) {
        this.msg = msg;
        this.context = context;
        this.handler = handler;
        this.userLocation = userLocation;
        this.willCommunicate = willCommunicate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        Time time = new Time();

        while (this.msg.IsSending()) {
            Bundle bundle = new Bundle();
            android.os.Message handler_msg = this.handler.obtainMessage();
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }

            synchronized (this.msg) {
                if (this.msg.isWaiting()) {
                    if (this.msg.get_chosen_trains() == null || this.msg.getIgnored() == null) {
                        Log.e(Thread.currentThread().getName(), "Data is NULL");
                        return;
                    }
                    else{
                        this.userLocation.getLastLocation(this.context);
                        HashMap<String, String> target_station_record = this.msg.getTargetContent();
                        String target_station_lat = target_station_record.get("station_lat");
                        String target_station_lon = target_station_record.get("station_lon");


                        Double current_user_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                Double.parseDouble(target_station_record.get("user_lat")),
                                Double.parseDouble(target_station_record.get("user_lon")),
                                Double.parseDouble(target_station_lat),
                                Double.parseDouble(target_station_lon));
                        int current_user_eta = time.get_estimated_time_arrival(3, current_user_distance_from_target_station);
                        target_station_record.put("user_eta", String.valueOf(current_user_eta));
//


                        for (HashMap valid_trains : this.msg.get_chosen_trains()) {
                            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                    Double.parseDouble((String) valid_trains.get("train_lat")),
                                    Double.parseDouble((String) valid_trains.get("train_lon")),
                                    Double.parseDouble(target_station_lat),
                                    Double.parseDouble(target_station_lon));
                            int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                            valid_trains.put("train_eta", current_train_eta);
                            valid_trains.put("train_distance", current_train_distance_from_target_station);

                        }

                        bundle.putSerializable("target_record", target_station_record);
                        bundle.putSerializable("chosen_trains", this.msg.get_chosen_trains());
                        bundle.putSerializable("ignored_trains", this.msg.getIgnored());
                        handler_msg.setData(bundle);
                        handler.sendMessage(handler_msg);
                        this.msg.notify();

                        if (!this.msg.getClicked()) {
                            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

                        }else{
                            this.msg.notify();
                            this.msg.setClicked(false);
                        }
                    }


//                    try {this.msg.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
//                    HashMap<String, String> target_station_record = this.msg.getTargetContent();
//                    String target_station_lat = target_station_record.get("station_lat");
//                    String target_station_lon = target_station_record.get("station_lon");
//                    DatabaseHelper db = new DatabaseHelper(this.context);
//                    this.userLocation.getLastLocation(this.context);
//                    HashMap<String, String> record = db.getAllRecord("userLocation_table");
//                    ArrayList<Integer> sortedTrains = new ArrayList<>();
//                    HashMap<String, String> nearest_train = null;

//                    if (this.msg.get_chosen_trains().size() == 0) {
//                        Log.e("No trains", "No Trains Available");
//                    }
//
//                    Double current_user_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                            Double.parseDouble(record.get("user_lat")),
//                            Double.parseDouble(record.get("user_lon")),
//                            Double.parseDouble(target_station_lat),
//                            Double.parseDouble(target_station_lon));
//                    int current_user_eta = time.get_estimated_time_arrival(3, current_user_distance_from_target_station);
////

//                    Log.e("valid", this.msg.get_chosen_trains() + "");
//                    if (this.willCommunicate) {
//                        Log.e(Thread.currentThread().getName(), "Is waiting...");
//                    }
//
//                    try { this.msg.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
//
//
//
//
//
                }


                }
            }


    }
}
