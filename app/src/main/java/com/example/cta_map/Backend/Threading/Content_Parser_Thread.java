
package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Content_Parser_Thread implements Runnable {
    private final Context context;
    private final Message msg;

    android.os.Handler handler;
    HashMap<String, String> target_station;
    ArrayList<Double> all_speeds = new ArrayList<>();

    String TAG = "Content Parser";

    public Content_Parser_Thread(Context context, Message msg,android.os.Handler handler, HashMap<String, String> target_station){
        this.msg = msg;
        this.context = context;
        this.handler = handler;
        this.target_station = target_station;


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (this.msg){
            while (this.msg.IsSending()){
                ArrayList<Train> incoming_trains = this.msg.getIncoming_trains(); // From API Call
                ArrayList<Train> chosen_trains = null;
                try {
                    chosen_trains = choose_trains(this.context, this.target_station, incoming_trains, this.msg);

                    if (this.msg.getOld_trains()!=null){
                        for (Train new_train: chosen_trains) {
                            updateTrains(new_train, this.msg.getOld_trains());
                        }
                    }
                } catch (ParseException e) {
                    Log.e("ERROR", "ERROR WITHIN 'CHOOSE_TRAINS'");
                    e.printStackTrace();
                }

                if (this.msg.getOld_trains() != null){
                    if (!this.msg.getDirectionChanged()) {
                        send_to_UI("new_incoming_trains", this.msg.getOld_trains());
                    }else {
                        this.msg.setDirectionChanged(false);
                        send_to_UI("new_incoming_trains", chosen_trains);
                    }
                }else{
                    send_to_UI("new_incoming_trains", chosen_trains);
                }
                this.msg.notify();
                try {
                    this.msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        Log.e(TAG, "is Killed.");
    }
    public void updateTrains(Train new_train, ArrayList<Train> old_trains){
        for (Train old_train : old_trains) {
            if (old_train.getRn().equals(new_train.getRn())) {
                old_train.setRn(new_train.getRn());
                old_train.setStatus(new_train.getStatus());
                old_train.setDestNm(new_train.getDestNm());
                old_train.setTrDr(new_train.getTrDr());
                old_train.setNextStpID(new_train.getNextStpID());
                old_train.setNextStaNm(new_train.getNextStaNm());
                old_train.setPrdt(new_train.getPrdt());
                old_train.setArrT(new_train.getArrT());
                old_train.setIsApp(new_train.getIsApp());
                old_train.setHeading(new_train.getHeading());
                old_train.setIsDly(new_train.getIsDly());
                old_train.setDestSt(new_train.getDestSt());
                old_train.setNextStaId(new_train.getNextStaId());
                old_train.setLat(new_train.getLat());
                old_train.setLon(new_train.getLon());
                old_train.setTarget_id(new_train.getTarget_id());
                old_train.setTarget_eta(new_train.getTarget_eta());
                old_train.setNext_stop_distance(new_train.getNext_stop_distance());
                old_train.setNextStopEtA(new_train.getNextStopEtA());
                old_train.setTarget_distance(new_train.getTarget_distance());

            }

        }






    }


    public void send_to_UI(String key, ArrayList<Train> message){
        Bundle bundle = new Bundle();
        android.os.Message handler_msg = this.handler.obtainMessage();
        bundle.putSerializable(key, message);
        handler_msg.setData(bundle);
        handler.sendMessage(handler_msg);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Train> choose_trains(Context context,HashMap<String, String> target_station ,ArrayList<Train> incoming_trains, Message message) throws ParseException {
        boolean left = message.getDir().equals("1");
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        Time time = new Time();
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+target_station.get("MAP_ID")+"'", null,null);
        if (record == null){return null;}

        HashMap<String, String> target_record = (HashMap<String, String>) record.get(0);
        double target_lat = Double.parseDouble(target_record.get("LAT"));
        double target_lon = Double.parseDouble(target_record.get("LON"));
        BufferedReader file3Buffer = chicago_transits.setup_file_reader(context, R.raw.line_stops);
        ArrayList<Train> chosen_trains = new ArrayList<>();
        ArrayList<String> ordered_stops = chicago_transits.create_line_stops_table(file3Buffer, context, message.getTarget_type());
        int target_idx = ordered_stops.indexOf(target_station.get("STATION_NAME"));
        for (Train train: incoming_trains){

            if (train.getTrDr().equals(message.getDir())){
//                Log.e("Content_Parser","Current Train Direction: "+ train.getTrDr());

                if (left){
                    List<String> left_sublist = ordered_stops.subList(target_idx, ordered_stops.size()-1);
                    if (left_sublist.contains(train.getNextStaNm())){
                        ArrayList<Object> record_1 = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STOP_ID = '"+train.getNextStpID()+"'", null,null);
                        HashMap<String, String> next_stop_record = (HashMap<String, String>) record_1.get(0);
                        Double train_lat = train.getLat();
                        Double train_lon = train.getLon();
                        double n_lat = Double.parseDouble(next_stop_record.get("LAT"));
                        double n_lon = Double.parseDouble(next_stop_record.get("LON"));
                        Double next_stop_distance = chicago_transits.calculate_coordinate_distance(n_lat, n_lon, train_lat, train_lon);
                        Double train_to_target_distance = chicago_transits.calculate_coordinate_distance(target_lat, target_lon, train_lat, train_lon);
                        double avg_train_speed = 55;
                        int next_stop_eta = time.get_estimated_time_arrival((int) avg_train_speed, next_stop_distance);
                        int target_eta = time.get_estimated_time_arrival((int) avg_train_speed, train_to_target_distance);
                        DecimalFormat df = new DecimalFormat("###.##");
//                        Log.e("Next Stop speed", "Train#"+ train.getRn()+" | Distance: "+ df.format(next_stop_distance) +"mi | Speed: "+avg_train_speed+" miles per hour. Next Stop: "+ train.getNextStaNm()+ "|  ETA: "+next_stop_eta +" | Target ETA: "+ target_eta);
                        HashMap<String,String> user_loc = getUserLocation(this.context);
                        Double user_lat = Double.parseDouble(user_loc.get("LAT"));
                        Double user_lon = Double.parseDouble(user_loc.get("LON"));
                        Double user_to_target_distance = chicago_transits.calculate_coordinate_distance(target_lat, target_lon, user_lat, user_lon);
                        train.setUser_to_target_distance(user_to_target_distance);
                        train.setUser_lat(user_lat);
                        train.setUser_lon(user_lon);
                        train.setTarget_id(target_record.get("MAP_ID"));
                        train.setTarget_eta(target_eta);
                        train.setNext_stop_distance(next_stop_distance);
                        train.setNextStopEtA(next_stop_eta);
                        train.setTarget_distance(train_to_target_distance);
                        chosen_trains.add(train);

                    }
                }else{
                    List<String> right_sublist = ordered_stops.subList(0, target_idx+1);
                    if (right_sublist.contains(train.getNextStaNm())){
                        HashMap<String, String> next_stop_record =(HashMap<String, String>) cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STOP_ID = '"+train.getNextStpID()+"'", null,null).get(0);
                        Double train_lat = train.getLat();
                        Double train_lon = train.getLon();
                        double n_lat = Double.parseDouble(next_stop_record.get("LAT"));
                        double n_lon = Double.parseDouble(next_stop_record.get("LON"));
                        Double next_stop_distance = chicago_transits.calculate_coordinate_distance(n_lat, n_lon, train_lat, train_lon);
                        Double target_stop_distance = chicago_transits.calculate_coordinate_distance(target_lat, target_lon, train_lat, train_lon);
                        int eta = time.get_estimated_time_arrival(55, target_stop_distance);
                        HashMap<String,String> user_loc = getUserLocation(this.context);
                        Double user_lat = Double.parseDouble(user_loc.get("LAT"));
                        Double user_lon = Double.parseDouble(user_loc.get("LON"));
                        Double user_to_target_distance = chicago_transits.calculate_coordinate_distance(target_lat, target_lon, user_lat, user_lon);
                        train.setUser_to_target_distance(user_to_target_distance);
                        train.setTarget_eta(eta);
                        train.setUser_lat(user_lat);
                        train.setUser_lon(user_lon);
                        train.setTarget_id(target_record.get("MAP_ID"));
                        BigDecimal bd = new BigDecimal(next_stop_distance).setScale(2, RoundingMode.HALF_UP);
                        train.setNext_stop_distance(bd.doubleValue());
                        train.setTarget_distance(target_stop_distance);
                        chosen_trains.add(train);
                    }
                }
            }
        }
        cta_dataBase.close();
        Collections.sort(chosen_trains);
        return setStatus(this.context, chosen_trains, target_station.get("STATION_NAME"));

    }


    private HashMap<String, String> getUserLocation(Context context){
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS","MAP_ID = '41450'", null,null);
        cta_dataBase.close();
        return (HashMap<String, String>) record.get(0);
    }

    private Double getTimeDifference(Date time1, Date time2){
        if (time1 == null || time2 == null){
            return null;
        }
        return (time1.getTime() - time2.getTime())/ 1e6;
    }




    // Returns live train speed. (if Needed.)
    private void getLiveSpeed(){
        //                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
//                        Date nextStopPredictedArrivalTime = simpleDateFormat.parse(train.getArrT());
//                        String current_time = dtf.format(LocalDateTime.now());
//                        Date currentTime = simpleDateFormat.parse(current_time);
//                        Log.e("TIME", "Current Time: "+currentTime+" || next stop Time: "+nextStopPredictedArrivalTime+"");
//                        Double time_diff = getTimeDifference(nextStopPredictedArrivalTime,currentTime);
//                        Log.e("TimeDifference", time_diff+"");

//                        Double speed = next_stop_distance / time_diff;
//                        all_speeds.add(speed);
//                        double sum = 0;
//                        for (int i=0; i < all_speeds.size(); i++){
//                            sum += all_speeds.get(i);
//                        }
//                        Log.e("SPEEDS", all_speeds+"");
//                      double avg_train_speed = sum / all_speeds.size();


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<Train> setStatus(Context context, ArrayList<Train> incoming_trains, String target_name){
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader file3Buffer = chicago_transits.setup_file_reader(context, R.raw.line_stops);

        if (incoming_trains.size() >0) {
            ArrayList<String> ordered_stops = chicago_transits.create_line_stops_table(file3Buffer, context, incoming_trains.get(0).getTrain_type());
            for (Train main_train : incoming_trains) {
                try {
                    int target_idx = ordered_stops.indexOf(target_name);
                    if (main_train.getIsApp().equals("1") && main_train.getNextStaId().equals(main_train.getTarget_id())) {
//                        Log.e("NORTH STATUS", "RED");
//                        Log.e("NORTH", " IS APP. " + main_train.getNextStaNm());
                        main_train.setStatus("RED");
                    }
                    int next_stop_idx = ordered_stops.indexOf(main_train.getNextStaNm());
                    List<String> remaining_stops;
                    if (main_train.getTrDr().equals("1")) {
                        remaining_stops = ordered_stops.subList(target_idx, next_stop_idx + 1);
                    } else {
                        remaining_stops = ordered_stops.subList(next_stop_idx, target_idx + 1);
                    }

                    if (remaining_stops.size() <= 1) {
//                        Log.e("NORTH STATUS", "RED");
                        main_train.setStatus("RED");
                    }
                    if (remaining_stops.size() == 2) {
//                        Log.e("NORTH STATUS", "YELLOW");
                        main_train.setStatus("YELLOW");

                    } else if (remaining_stops.size() >= 3) {
//                        Log.e("NORTH STATUS", "GREEN");
                        main_train.setStatus("GREEN");
                    }

                    Double train_to_target_distance = main_train.getTarget_distance();
                    Double user_to_target_distance = main_train.getUser_to_target_distance();

                    if (user_to_target_distance <= train_to_target_distance) {
                        main_train.setUserStatus("GREEN");
                    } else if (train_to_target_distance <= user_to_target_distance) {
                        main_train.setUserStatus("RED");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return incoming_trains;
    }


}