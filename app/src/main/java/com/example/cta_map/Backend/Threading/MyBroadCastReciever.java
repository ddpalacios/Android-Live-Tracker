package com.example.cta_map.Backend.Threading;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.NotificationBuilder;
import com.example.cta_map.Displayers.Train;

import java.util.ArrayList;
import java.util.HashMap;

public class MyBroadCastReciever extends BroadcastReceiver {
    NotificationBuilder notificationBuilder;
    Context main_context;
    Message message;
    public static String TAG = "Broadcast";

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            ArrayList<Train> current_incoming_trains = (ArrayList<Train>) bundle.getSerializable("new_incoming_trains");
            if (current_incoming_trains != null && current_incoming_trains.size() > 0) {
                boolean isBeingNotified = new Chicago_Transits().StartNotificationServices(main_context, message,current_incoming_trains);
                if (isBeingNotified) {
                    MainActivity.ToastMessage(main_context, "Services are being started.");
                } else {
                    MainActivity.ToastMessage(main_context, "No Services open yet.");

                }
            };
            }
        };


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            main_context = context;
            notificationBuilder = new NotificationBuilder(context, intent);
            String alarm_id  = intent.getStringExtra("alarm_id");
            String day_of_week_alarm_id = intent.getStringExtra("day_of_week_alarm_id");
            int day_of_week = intent.getExtras().getInt("day_of_week");
            String direction =intent.getStringExtra("direction");
            String map_id =intent.getStringExtra("map_id");
            String station_type =intent.getStringExtra("station_type");




            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
            ArrayList<Object> current_user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null, null);
//            // if we have the main activity starting, use that message obj, if not - create a new one
            message = ((MainActivity.message != null ? MainActivity.message : new Message()));
            Chicago_Transits chicago_transits = new Chicago_Transits();




            if (day_of_week_alarm_id!=null){
                Alarm alarm_record = new Alarm();
                ArrayList<Object>r =  cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, CTA_DataBase.ALARM_ID +" = '"+alarm_id+"'", null,null);
                if (r.size() > 0) {
                    HashMap<String, String> rec = (HashMap<String, String>) r.get(0);
                    alarm_record.setMin(rec.get(CTA_DataBase.MIN));
                    alarm_record.setHour(rec.get(CTA_DataBase.HOUR));
                    alarm_record.setTime(rec.get(CTA_DataBase.TIME));
                    alarm_record.setWeekLabel(rec.get(CTA_DataBase.WEEK_LABEL));
                    alarm_record.setDirection(rec.get(CTA_DataBase.ALARM_DIRECTION));
                    alarm_record.setIsRepeating(Integer.parseInt(rec.get(CTA_DataBase.WILL_REPEAT)));
                    alarm_record.setMap_id(map_id);
                    alarm_record.setStationType(station_type);
                }
                alarm_record.setAlarm_id(alarm_id);
                chicago_transits.scheduleAlarm(main_context, day_of_week, alarm_record, day_of_week_alarm_id, alarm_id); // Reschedule alarm
            }



            if (chicago_transits.isMyServiceRunning(main_context,new MainNotificationService().getClass())) {
                chicago_transits.stopService(main_context); // stopping a service if one currently exists
            }

            if (message.getT1() != null && current_user_tracking_record != null) { // This is the case when we have the main activity previously declared
//                int res = new Chicago_Transits().cancelRunningThreads(message); // cancel current running threads
//                if (res > 0) {
//                    if (message.getT1().isAlive()) {
//                        message.getT1().interrupt();
//                    } else {
//                        MainActivity.LogMessage("Thread not alive.");
//                    }
//                    try {
//                        API_Caller_Thread.msg.getT1().join(MainActivity.TIMEOUT);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (message.getHandler()!=null){ // if the main activity is open, use handler from UI Thread
//                        handler = message.getHandler();
//                    }
//                }
                chicago_transits.StopThreads(message, context);
                chicago_transits.callThreads(main_context, handler, message,direction,station_type, map_id, true);

            }else{
                // if the app has not been started
                Log.e(TAG, "STARTING FRESH FROM ALARM");
                message = new Message();
                new Chicago_Transits().callThreads(main_context, handler, message,direction,station_type, map_id, true);
            }

//            if (alarm_record != null) {
//                String day_of_week_alarm_id = intent.getExtras().getString("day_of_week_alarm_id");
//                int day_of_week = intent.getExtras().getInt("day_of_week");
//                chicago_transits.scheduleAlarm(main_context, day_of_week, alarm_record, day_of_week_alarm_id); // Reschedule alarm
//            }
//            if (chicago_transits.isMyServiceRunning(main_context,new ExampleService().getClass())) {
//                    chicago_transits.stopService(main_context); // stopping a service if one currently exists
//            }
//
//                if (message.getT1() != null && current_user_tracking_record != null){ // This is the case when we have the main activity previously declared
//                int res = new Chicago_Transits().cancelRunningThreads(message); // cancel current running threads
//                if (res > 0) {
//                    if (message.getT1().isAlive()) {
//                        message.getT1().interrupt();
//                    }else{
//                        MainActivity.LogMessage("Thread not alive.");
//                    }
//                    try {
//                        API_Caller_Thread.msg.getT1().join(MainActivity.TIMEOUT);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    new Chicago_Transits().callThreads(main_context, handler, message,alarm_record.getDirection(),alarm_record.getStationType(), alarm_record.getMap_id());
//
//                }else{
//                    MainActivity.LogMessage("Could not stop threads.");
//                }
//            }else{
//                // if the app has not been started
//                new Chicago_Transits().callThreads(main_context, handler, message,alarm_record.getDirection(),alarm_record.getStationType(), alarm_record.getMap_id());
            cta_dataBase.close();
        }
}