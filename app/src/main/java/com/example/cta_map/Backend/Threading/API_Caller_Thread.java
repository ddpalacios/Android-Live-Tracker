package com.example.cta_map.Backend.Threading;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class API_Caller_Thread implements Runnable {
    public static Message msg = null;
    String TAG = "API CALLER";
    Context context;
    Handler handler;
    private boolean fromAlarm;
    public  static Boolean AlarmTriggered = false;
    private volatile boolean cancelled = false;
    public API_Caller_Thread(Message msg, Context context, Handler handler, boolean fromAlarm){
        API_Caller_Thread.msg = msg;
        this.context = context;
        this.handler = handler;
        this.fromAlarm = fromAlarm;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        Log.e("API CALLER", "NEW THREADS ARE STARTING");
        synchronized (msg){
            while (msg.IsSending()) {
                    if (cancelled) {
                    break;
                }
                CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
                ArrayList<Train> new_incoming_trains = null;
                    try {
//                        if (MainActivity.message.getT1()==null) {
//                            MainActivity.message = msg;
//                        }
                        new_incoming_trains = call_cta_rest();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                ArrayList<Object> tracking_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null, null, null);



                    cta_dataBase.close();
                    if (tracking_record !=null && new_incoming_trains!= null){ // from service intent
                        HashMap<String,String> current_tracking_train = (HashMap<String, String>) tracking_record.get(0);
                        for (Train train : new_incoming_trains){
                            if (train.getRn().equals(current_tracking_train.get("TRAIN_ID"))){
                                train.setNotified(true);
                                train.setSelected(true);
                                msg.setOld_trains(new_incoming_trains);
                                break;
                            }
                        }
                    }
                    if (new_incoming_trains.size() > 0) {
                        msg.setFinalDest(new_incoming_trains.get(0).getDestNm());
                        msg.setNearestTrain(new_incoming_trains.get(0));
                    }else{
                        msg.setNearestTrain(null);
                    }


                if (tracking_record == null && new_incoming_trains !=null&&fromAlarm ){
                    // we need to set the nearest train if we are calling from an alarm
                    cta_dataBase.commit(new_incoming_trains.get(0), CTA_DataBase.TRAIN_TRACKER);
                    fromAlarm = false;
                    Log.e(TAG, "THREAD IS GOING TO INTERRUTT");

                    msg.getT1().interrupt();
                    Log.e(TAG, "THREAD  INTERRUTTED");

                }



                msg.setIncoming_trains(new_incoming_trains);
                    setStatus(new_incoming_trains, msg);
                    send_to_UI("new_incoming_trains",new_incoming_trains);
                try {
                    Log.e(TAG, "THREAD IS WAITING");
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (cancelled){
                        Log.e(TAG, "THREAD IS STOPPING...");
                        break;

                    }
                }
            }

        }
        Log.e(TAG, "Thread is Killed.");
    }

    private ArrayList<Train> proceess_new_trains(ArrayList<Train> new_incoming_trains) {
        if (new_incoming_trains.size() == 0){
            send_to_UI("new_incoming_trains",new_incoming_trains);
            return new_incoming_trains;
        }

        if (msg.getOld_trains() !=null && new_incoming_trains !=null) {
            for (Train train : msg.getOld_trains()) {
                if (train.getSelected() && train.getIsNotified()){ // Setting new incoming trains for notification train
                    for (Train train1 : new_incoming_trains){
                        if (train.getRn().equals(train1.getRn())){
                            train1.setSelected(true);
                            train1.setNotified(true);
                            break;
                        }
                    }
                }
                if (train.getSelected()){
                    for (Train train1 : new_incoming_trains){  // Setting new incoming trains for selection trains
                        if (train.getRn().equals(train1.getRn())){
                            train1.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
        return new_incoming_trains;
    }

    public void cancel() {
        cancelled = true;
    }

    private String[] callApi(String url) throws IOException {
        final Document TRAIN_RESPONSE = Jsoup.connect(url).get(); // JSOUP to webscrape XML
        return TRAIN_RESPONSE.select("eta").outerHtml().split("</eta>");
    }



    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Train> call_cta_rest() throws ParseException {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ArrayList<Train> all_incoming_trains = null;
        String train_rn;
        SimpleDateFormat dateFormat;
        String[] train_list = null;
        ArrayList<String> remaining_stops_url_list = new ArrayList<>();
        String train_eta_url = null;

        try {
            train_eta_url =  "https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&mapid="+ msg.getTARGET_MAP_ID();
            String trains_heading_to_station_url = "https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&mapid="+ msg.getTARGET_MAP_ID();
            train_list = callApi(trains_heading_to_station_url);
        }catch (Exception e){
            e.printStackTrace();
        }
            all_incoming_trains = new ArrayList<>();
        if (train_list == null || train_list.length == 0){
            msg.setStop_id(null);
        }

            if (train_list!=null) {
                for (String raw_train : train_list) {
                    Train train = chicago_transits.get_train_info(raw_train);
                    if (train != null) {
                        train.setTarget_id(msg.getTARGET_MAP_ID());
                        if (train.getTrDr().equals(msg.getDir())) { // Filter list based on target direction
                            //get train eta to target
                            msg.setStop_id(train.getStpId());
                            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date parsedArrivalTime = dateFormat.parse(train.getArrT());
                            Date parsePredictedTime = dateFormat.parse(train.getPrdt());
                            long diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                            if (diff < 0){
                                diff = parsePredictedTime.getTime() - parsedArrivalTime.getTime();
                            }
                            long eta_in_minutes = diff / (60 * 1000) % 60;
                            train.setTarget_eta((int) eta_in_minutes);
                            String[] remaining_stops = null;
                            try {
                                train_rn = train.getRn();
                                String remaining_stations_url = "https://lapi.transitchicago.com/api/1.0/ttfollow.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&runnumber=" + train_rn;
                                remaining_stops_url_list.add(remaining_stations_url);
                                remaining_stops = callApi(remaining_stations_url);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            ArrayList<TrainStops> remaining_stations = new ArrayList<>();
                            if (remaining_stops!=null) {
                                for (String raw_stop : remaining_stops) {
                                    TrainStops remaining_trainStop = chicago_transits.get_remaining_train_stop_info(raw_stop);
                                    if (remaining_trainStop != null) {
                                        parsedArrivalTime = dateFormat.parse(remaining_trainStop.getArrT());
                                        parsePredictedTime = dateFormat.parse(remaining_trainStop.getPrdt());
                                        diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                                        eta_in_minutes = diff / (60 * 1000) % 60;
                                        remaining_trainStop.setNextStopEtA((int) eta_in_minutes);
                                        remaining_stations.add(remaining_trainStop);
                                    }
                                }
                            }
                            train.setSelected(false);
                            train.setNotified(false);
                            train.setRemaining_stops(remaining_stations);
                            all_incoming_trains.add(train);
                        }
                    }
                }
            }
        Log.e("API CALLER", "\nMain URL: \n- . "+ train_eta_url+ "\n");
            for (String url : remaining_stops_url_list){
                Log.e("API CALLER", "\n- "+url+"\n");

            }
        return proceess_new_trains(all_incoming_trains);
    }

    public void send_to_UI( String key, ArrayList<Train> trainArrayList){

        Bundle bundle = new Bundle();
        android.os.Message handler_msg = msg.getHandler().obtainMessage();
        bundle.putSerializable(key, trainArrayList);
        handler_msg.setData(bundle);
        msg.getHandler().sendMessage(handler_msg);
        Log.e("API CALLER", "Sent to UI");
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setStatus(ArrayList<Train> incoming_trains, Message message){
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+this.msg.getTARGET_MAP_ID()+"'", null,null);
        cta_dataBase.close();
        if (record!=null){
            Station target_station_record = (Station) record.get(0);
            message.setTarget_name(target_station_record.getStation_name());


            for (Train main_train : incoming_trains) {
                    main_train.setTarget_station_name(target_station_record.getStation_name());
                    ArrayList<TrainStops> remaining_stops = main_train.getRemaining_stops();
                    ArrayList<TrainStops> new_list = new ArrayList<>();
                    for (TrainStops remainining_train_stop : remaining_stops) {
                        if (remainining_train_stop.getStaId().equals(target_station_record.getMap_id())) {
                            new_list.add(remainining_train_stop);
                            break;
                        } else {
                            new_list.add(remainining_train_stop);
                        }
                    }

                if (new_list.size() == 1 || new_list.size() == 2 || new_list.size() < 1) {
                    main_train.setStatus("RED");
                }
                if (new_list.size() == 3 || new_list.size() == 4) {
                    main_train.setStatus("YELLOW");

                } else if (new_list.size() >= 5) {
                    main_train.setStatus("GREEN");
                }


                if (main_train.getIsSch()){
                  main_train.setStatus(null);
                }


            }
            Collections.sort(incoming_trains);
        }
    }


}