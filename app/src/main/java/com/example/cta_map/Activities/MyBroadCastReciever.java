package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.NotificationBuilder;
import com.example.cta_map.Displayers.Train;

import java.util.ArrayList;
import java.util.HashMap;

public class MyBroadCastReciever extends BroadcastReceiver {
    NotificationBuilder notificationBuilder;
    boolean greenNotified = false;
    boolean RedNotified = false;
    boolean isApp = false;
    boolean YellowNotified = false;
    boolean hasTrains = false;
    Context main_context;
    Train nearest_train = null;
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            ArrayList<Train> current_incoming_trains = (ArrayList<Train>) bundle.getSerializable("new_incoming_trains");


            if (current_incoming_trains!= null && current_incoming_trains.size() > 0) {
                if (nearest_train!=null){
                    boolean found = false;
                    for (Train train : current_incoming_trains){
                        if (train.getRn().equals(nearest_train.getRn())){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        notificationBuilder.notificationDialog("Train# "+nearest_train.getRn()+" Has Arrived!", "Train# "+nearest_train.getRn() + " has arrived", nearest_train);
                        nearest_train = null;
                        greenNotified = false;
                        RedNotified = false;
                        YellowNotified = false;
                        hasTrains = false;
                        isApp = false;
                    }
                }
                if (current_incoming_trains.size() > 0){
                    nearest_train = current_incoming_trains.get(0);
                    if (nearest_train.getStatus().equals("GREEN") && !greenNotified ) {
                        notificationBuilder.notificationDialog("Train# "+nearest_train.getRn()+" You still have time!", "Train# "+nearest_train.getRn() + " is "+ nearest_train.getRemaining_stops().size() + " stops away!", nearest_train);
                        greenNotified = true;
                    }
                    else if (nearest_train.getStatus().equals("YELLOW") && !YellowNotified){
                        notificationBuilder.notificationDialog("Train# "+nearest_train.getRn()+" Be on your way!", "Train# "+nearest_train.getRn() + " is "+ nearest_train.getRemaining_stops().size() + " stops away!", nearest_train);
                        YellowNotified = true;
                    }else if (nearest_train.getStatus().equals("RED")){
                        if (!RedNotified) {
                            notificationBuilder.notificationDialog("Train# " + nearest_train.getRn() + " is arriving soon!", "Train# " + nearest_train.getRn() + " is " + nearest_train.getRemaining_stops().size() + " stops away!", nearest_train);
                            RedNotified = true;
                        }
                        if (nearest_train.getIsApp().equals("1")&& !isApp){
                            notificationBuilder.notificationDialog("Train# "+nearest_train.getRn()+" is approaching!", "Train# "+nearest_train.getRn() + " is "+ nearest_train.getRemaining_stops().size() + " stops away!", nearest_train);
                            isApp = true;
                        }
                    }
                }
         }else {
                if (current_incoming_trains != null) {
                    if (current_incoming_trains.size() == 0 && !hasTrains)
                        notificationBuilder.notificationDialog("No Available Trains", "There aren't any trains near by yet.", nearest_train);
                    hasTrains = true;

                }
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        main_context = context;
        Toast.makeText(context.getApplicationContext(), "BroadCast RECIEOVER", Toast.LENGTH_LONG).show();
        Message message = new Message();
        notificationBuilder = new NotificationBuilder(context, intent);
        String map_id= intent.getExtras().getString("map_id");
        String station_type = intent.getExtras().getString("station_type");


        Thread t1 = new Thread(new API_Caller_Thread(message, context, handler));
//        Thread t2= new Thread(new Content_Parser_Thread(context, handler,message));
        if (map_id !=null) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" +map_id + "'", null, null);
            HashMap<String, String> tracking_record = (HashMap<String, String>) record.get(0);
            cta_dataBase.close();
            message.setTARGET_MAP_ID(tracking_record.get("MAP_ID"));
            message.setDir("1");
            message.setTarget_type(station_type);
            message.setTarget_station(tracking_record);
            t1.start();
//            t2.start();


        }
    }


}
