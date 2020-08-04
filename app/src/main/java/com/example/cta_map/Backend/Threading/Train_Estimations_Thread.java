package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.UserLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class Train_Estimations_Thread implements Runnable {
    final Message msg;
    android.os.Handler handler;
    HashMap<String, String> tracking_record;
    boolean willCommunicate;
    UserLocation userLocation;
    Context context;
    public Train_Estimations_Thread(Message msg, UserLocation userLocation,   HashMap<String, String> tracking_record,android.os.Handler handler, Context context, boolean willCommunicate) {
        this.msg = msg;
        this.tracking_record = tracking_record;
        this.context = context;
        this.handler = handler;
        this.userLocation = userLocation;
        this.willCommunicate = willCommunicate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
            while (this.msg.IsSending()){
                try { Thread.sleep(120); } catch (InterruptedException e) { e.printStackTrace(); }
                synchronized (this.msg) {

                Bundle bundle = new Bundle();
                android.os.Message handler_msg = this.handler.obtainMessage();
                HashMap<String, ArrayList<HashMap>> parsed_train_data = this.msg.getParsedTrainData();
                TreeMap<Integer, String> treeMap = this.msg.getTrainMap();
                if (parsed_train_data == null) {
                    Log.e(Thread.currentThread().getName(), "Data is NULL");
                    return;
                }

                bundle.putSerializable("estimated_train_data", parsed_train_data);
                bundle.putSerializable("sorted_train_eta_map", treeMap);
                handler_msg.setData(bundle);
                handler.sendMessage(handler_msg);
                this.msg.setParsedTrainData(null);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    this.msg.notify();


            }

        }




        }
    }



//        while (this.msg.IsSending()) {
//            Bundle bundle = new Bundle();
//            android.os.Message handler_msg = this.handler.obtainMessage();
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            synchronized (this.msg) {
//                if (this.msg.isWaiting()) {
//                    if (this.msg.get_chosen_trains() == null || this.msg.getIgnored() == null) {
//                        Log.e(Thread.currentThread().getName(), "Data is NULL");
//                        return;
//                    }
//                    else{
////                        this.userLocation.getLastLocation(this.context);
//                        HashMap<String, String> target_station_record = this.msg.getTargetContent();
//                        String target_station_lat = target_station_record.get("station_lat");
//                        String target_station_lon = target_station_record.get("station_lon");
//
//
//                        Double current_user_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                                Double.parseDouble(target_station_record.get("user_lat")),
//                                Double.parseDouble(target_station_record.get("user_lon")),
//                                Double.parseDouble(target_station_lat),
//                                Double.parseDouble(target_station_lon));
//                        int current_user_eta = time.get_estimated_time_arrival(3, current_user_distance_from_target_station);
//                        target_station_record.put("user_eta", String.valueOf(current_user_eta));

//
//
//                        for (HashMap valid_trains : this.msg.get_chosen_trains()) {
//                            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                                    Double.parseDouble((String) valid_trains.get("train_lat")),
//                                    Double.parseDouble((String) valid_trains.get("train_lon")),
//                                    Double.parseDouble(target_station_lat),
//                                    Double.parseDouble(target_station_lon));
//                            int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
//                            valid_trains.put("train_eta", current_train_eta);
//                            valid_trains.put("train_distance", current_train_distance_from_target_station);
//
//                        }
//
//                        bundle.putSerializable("target_record", target_station_record);
//                        bundle.putSerializable("chosen_trains", this.msg.get_chosen_trains());
//                        bundle.putSerializable("ignored_trains", this.msg.getIgnored());
//                        handler_msg.setData(bundle);
//                        handler.sendMessage(handler_msg);
//                        this.msg.notify();
//
//                        if (!this.msg.getClicked()) {
//                            try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
//
//                        }else{
//                            this.msg.notify();
//                            this.msg.setClicked(false);
//                        }
//                    }


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
////
//                }
//
//
//                }
//            }

//        }
