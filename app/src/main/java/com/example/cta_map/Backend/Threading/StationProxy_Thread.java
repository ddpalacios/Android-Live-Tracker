//package com.example.cta_map.Backend.Threading;
//
//import android.content.Intent;
//import android.util.Log;
//
//import com.example.cta_map.DataBase.DatabaseHelper;
//import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.Displayers.Time;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class StationProxy_Thread implements Runnable {
//
//    Message msg;
//    boolean willComunicate;
//    DatabaseHelper sqlite;
//    HashMap<String, String>record;
//    String id;
//
//    public StationProxy_Thread(Message msg, DatabaseHelper sqlite, HashMap<String, String>record, String id, boolean willCommunicate){
//        this.msg = msg;
//        this.willComunicate = willCommunicate;
//        this.sqlite = sqlite;
//        this.record = record;
//        this.id = id;
//
//    }
//
//
//    @Override
//    public void run() {
//        try {
//            Thread.sleep(250);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        synchronized (this.msg){
//            Chicago_Transits chicago_transits = new Chicago_Transits();
//            Time time = new Time();
//            while (this.msg.IsSending()){
//
//                List<String> sub_stations = this.msg.getSubStations();
//                ArrayList<HashMap> chosen_trains = this.msg.get_chosen_trains();
//                HashMap<String, String> tracking_train = null;
//                HashMap<String,Integer> AllEtas = new HashMap<>();
//                ArrayList<HashMap> all_etas = new ArrayList<>();
//
//                for (HashMap<String, String> train : chosen_trains){
//                    if (train.get("train_id").equals(this.id)){
//                        tracking_train = train;
//                        break;
//                    }
//                }
//                if (tracking_train == null){ Log.e(Thread.currentThread().getName(), "Data is Null");return; }
//                Double tracking_lat = Double.parseDouble(tracking_train.get("train_lat"));
//                Double tracking_lon = Double.parseDouble(tracking_train.get("train_lon"));
//                Log.e(Thread.currentThread().getName(), "Sub is "+ sub_stations.size());
//
//                for (String station : sub_stations){
//                    String[] stationLatLon = chicago_transits.retrieve_station_coordinates(this.sqlite, station, this.record.get("station_type"));
//                    Double station_lat = Double.parseDouble(stationLatLon[0]);
//                    Double station_lon = Double.parseDouble(stationLatLon[1]);
//
//                    Double distance = chicago_transits.calculate_coordinate_distance(tracking_lat, tracking_lon, station_lat, station_lon);
//                    Integer current_eta = time.get_estimated_time_arrival(25, distance);
//                    AllEtas.put(station, current_eta);
//                }
//                Log.e(Thread.currentThread().getName(), "Data is "+ AllEtas.size());
//                this.msg.setTrain_etas(AllEtas);
//                if (this.willComunicate) {
//                    Log.e(Thread.currentThread().getName(), sub_stations + "");
//                    Log.e(Thread.currentThread().getName(),"is Waiting...");
//
//                }
//                try {
//                    this.msg.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (this.willComunicate) {
//                    Log.e(Thread.currentThread().getName(),"is Done Waiting...");
//
//                }
//
//
//            }
//        }
//
//
//    }
//}
