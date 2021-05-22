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


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            main_context = context;
            ScheduleAlarm(context, intent);
            Chicago_Transits chicago_transits = new Chicago_Transits();
            message = ((MainActivity.message != null ? MainActivity.message : API_Caller_Thread.msg));
            String direction =intent.getStringExtra("direction");
            String map_id =intent.getStringExtra("map_id");
            String station_type =intent.getStringExtra("station_type");
            Log.e("MESSAGE", "MESSAGE: "+ message);
            if (message == null) { // if NULL - App has not been started yet.
                message = new Message();
                message.setHandler(handler);
                message.setTarget_type(station_type);
            }

            message.setAlarmTriggered(true);
            // App has been started
            chicago_transits.StopThreads(message, context); // Stop current threads
            chicago_transits.callThreads(context, message.getHandler(), message,direction,station_type, map_id, true);

        }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ScheduleAlarm(Context context, Intent intent) {
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String alarm_id  = intent.getStringExtra("alarm_id");
        String day_of_week_alarm_id = intent.getStringExtra("day_of_week_alarm_id");
        int day_of_week = intent.getExtras().getInt("day_of_week");
        String map_id =intent.getStringExtra("map_id");
        String station_type =intent.getStringExtra("station_type");
        Alarm alarm_record = new Alarm();

        if (day_of_week_alarm_id!=null){
            ArrayList<Object>r =  cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, CTA_DataBase.ALARM_ID +" = '"+alarm_id+"'", null,null);
            alarm_record = (Alarm) r.get(0);
            chicago_transits.scheduleAlarm(context, day_of_week, alarm_record, day_of_week_alarm_id, alarm_id); // Reschedule alarm
            Log.e("ALARM", "ALARM WAS RESCHEDULED");
        }else{
            Log.e("ALARM", "ALARM WAS NOT RESCHEDULED");

        }




    }
}