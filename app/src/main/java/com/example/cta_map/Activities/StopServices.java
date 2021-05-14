package com.example.cta_map.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;

public class StopServices   extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean willDismiss = intent.getBooleanExtra("willDismiss", false);
        Boolean willTrackNext = intent.getBooleanExtra("track_next", false);


        if (willDismiss!= null && willDismiss){
            Message message = API_Caller_Thread.msg;
            String dir = message.getDir();
            String type= message.getTarget_type();
            String map_id =message.getTarget_station().getMap_id();
            Chicago_Transits chicago_transits = new Chicago_Transits();
            chicago_transits.reset(message.getOld_trains(), message);
            message.getT1().interrupt();
            int res = new Chicago_Transits().cancelRunningThreads(message); // cancel current running threads
            if (res > 0) {
                if (message.getT1().isAlive()) {
                    message.getT1().interrupt();
                } else {
                    MainActivity.LogMessage("Thread not alive.");
                }
                try {
                    API_Caller_Thread.msg.getT1().join(MainActivity.TIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }}

            if (new Chicago_Transits().isMyServiceRunning(context,new ExampleService().getClass())){
                new Chicago_Transits().stopService(context);
            }

            if (!MainActivity.message.getDestoryed()){
                message = MainActivity.message;
                dir = message.getDir();
                type= message.getTarget_type();
                map_id =message.getTarget_station().getMap_id();
                CTA_DataBase cta_dataBase = new CTA_DataBase(context);
                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                cta_dataBase.close();
                chicago_transits.callThreads(context, message.getHandler() ,message, dir, type, map_id, false);
            }else{
                CTA_DataBase cta_dataBase = new CTA_DataBase(context);

                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                Log.e("Notification", "App was destroyed");
                cta_dataBase.close();

            }

        }
    }
}
