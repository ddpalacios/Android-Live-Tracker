
package com.example.cta_map.Backend.Threading;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.CursorJoiner;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.CTA_Stops;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Content_Parser_Thread implements Runnable {
    private final Context context;
    private final Message msg;
    private volatile boolean cancelled = false;

    Station target_station;
    private Handler handler;

    String TAG = "Content Parser";

    public Content_Parser_Thread(Context context, Handler handler, Message msg){
        this.msg = msg;
        this.context = context;
        this.handler = handler;
        this.target_station = msg.getTarget_station();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (this.msg){
            while (this.msg.IsSending()){
                if (cancelled) {
                    break;
                }
                ArrayList<Train> incoming_trains = this.msg.getIncoming_trains(); // From API Call
                setStatus(incoming_trains);
                send_to_UI(this.msg, "new_incoming_trains",incoming_trains);

                for (Train train : incoming_trains){
                    Log.e("INCOMING", train.getRn() + "# | "+ train.getRt() + " | "+train.getTarget_eta()+"m");

                }
//                    if (this.msg.getOld_trains()!=null){
//                        for (Train new_train: incoming_trains) {
//                            updateTrains(new_train, this.msg.getOld_trains());
//                        }
//                        send_to_UI(this.msg, "new_incoming_trains",incoming_trains);
//                    }else {
//                        send_to_UI(this.msg, "new_incoming_trains",incoming_trains);
//                    }
                this.msg.notify();
                try {
                    this.msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Log.e(TAG, "THREAD IS WAITING");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (cancelled){
                        break;
                    }
                }
                Log.e(TAG, "THREAD IS DONE WAITING");
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

    public void cancel() {
        cancelled = true;
    }


    public void send_to_UI(Message message, String key, ArrayList<Train> trainArrayList){
        Bundle bundle = new Bundle();
        android.os.Message handler_msg =  handler.obtainMessage();//this.msg.getHandler().obtainMessage();
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
       Station target_station_record = (Station) record.get(0);
        for (Train main_train : incoming_trains) {
            if (incoming_trains.size() > 0) {
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
            }
        }
//            Collections.sort(incoming_trains);
        }
    }




}