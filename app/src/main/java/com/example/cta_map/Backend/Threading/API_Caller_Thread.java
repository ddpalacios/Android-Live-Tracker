package com.example.cta_map.Backend.Threading;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class API_Caller_Thread implements Runnable {
    String station_type;
    final Message msg;
    String TAG = "API CALLER";
    Context context;
    Handler handler;
    private volatile boolean cancelled = false;
    public API_Caller_Thread(Message msg, Context context, Handler handler){
        this.msg = msg;
        this.context = context;
        this.handler = handler;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        this.msg.keepSending(true);
        this.msg.setDirectionChanged(false);
        this.msg.setSendingNotifications(false);
        synchronized (this.msg){
            while (this.msg.IsSending()) {
                if (cancelled) {
                    break;
                }
                    ArrayList<Train> new_incoming_trains = call_cta_rest();
                if (new_incoming_trains!= null) {
                    this.msg.setIncoming_trains(new_incoming_trains);
                    setStatus(new_incoming_trains);
                    send_to_UI("new_incoming_trains",new_incoming_trains);
                }
                try {
                    Log.e(TAG, "THREAD IS WAITING");
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (cancelled){
                        break;
                    }
                }
            }
        }
        Log.e(TAG, "is Killed.");

    }

    public void cancel() {
        cancelled = true;
    }

    private String[] callApi(String url) throws IOException {
        final Document TRAIN_RESPONSE = Jsoup.connect(url).get(); // JSOUP to webscrape XML
        return TRAIN_RESPONSE.select("eta").outerHtml().split("</eta>");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Train> call_cta_rest()  {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ArrayList<Train> all_incoming_trains = null;
        String train_rn;
        SimpleDateFormat dateFormat;

        try {
            String trains_heading_to_station_url = "https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&mapid="+this.msg.getTARGET_MAP_ID();
            final String[] train_list = callApi(trains_heading_to_station_url);
            all_incoming_trains = new ArrayList<>();
            if (train_list!=null) {
                for (String raw_train : train_list) {
                    Train train = chicago_transits.get_train_info(raw_train);
                    if (train != null) {
                        train.setTarget_id(this.msg.getTARGET_MAP_ID());
                        if (train.getTrDr().equals(this.msg.getDir())) { // Filter list based on target direction
                            //get train eta to target
                            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date parsedArrivalTime = dateFormat.parse(train.getArrT());
                            Date parsePredictedTime = dateFormat.parse(train.getPrdt());
                            long diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                            long eta_in_minutes = diff / (60 * 1000) % 60;
                            train.setTarget_eta((int) eta_in_minutes);
                            train_rn = train.getRn();
                            String[] remaining_stops = callApi("https://lapi.transitchicago.com/api/1.0/ttfollow.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&runnumber=" + train_rn);
                            ArrayList<TrainStops> remaining_stations = new ArrayList<>();
                            for (String raw_stop : remaining_stops) {
                                TrainStops remaining_trainStop = chicago_transits.get_remaining_train_stop_info(raw_stop);
                                parsedArrivalTime = dateFormat.parse(remaining_trainStop.getArrT());
                                parsePredictedTime = dateFormat.parse(remaining_trainStop.getPrdt());
                                diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                                eta_in_minutes = diff / (60 * 1000) % 60;
                                remaining_trainStop.setNextStopEtA((int) eta_in_minutes);
                                remaining_stations.add(remaining_trainStop);
                            }
                            train.setSelected(false);
                            train.setRemaining_stops(remaining_stations);
                            all_incoming_trains.add(train);
                        }
                    }
                }
            }
        }catch (Exception e){
//            Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return all_incoming_trains;
    }

    public void send_to_UI( String key, ArrayList<Train> trainArrayList){
        Bundle bundle = new Bundle();
        android.os.Message handler_msg =  handler.obtainMessage();
        bundle.putSerializable(key, trainArrayList);
        handler_msg.setData(bundle);
        handler.sendMessage(handler_msg);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setStatus(ArrayList<Train> incoming_trains){
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+this.msg.getTARGET_MAP_ID()+"'", null,null);
        cta_dataBase.close();
        if (record!=null){
            HashMap<String,String> target_station_record = (HashMap<String, String>) record.get(0);
            for (Train main_train : incoming_trains) {
                if (incoming_trains.size() > 0) {
                    ArrayList<TrainStops> remaining_stops = main_train.getRemaining_stops();
                    ArrayList<TrainStops> new_list = new ArrayList<>();
                    for (TrainStops remainining_train_stop : remaining_stops) {
                        if (remainining_train_stop.getStaId().equals(target_station_record.get("MAP_ID"))) {
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
                }
            }
            Collections.sort(incoming_trains);
        }
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

}