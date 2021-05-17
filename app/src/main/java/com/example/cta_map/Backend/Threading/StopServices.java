package com.example.cta_map.Backend.Threading;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;

public class StopServices   extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            Message message = (MainActivity.message!=null ? MainActivity.message : API_Caller_Thread.msg);
            Chicago_Transits chicago_transits = new Chicago_Transits();
            chicago_transits.reset(message.getOld_trains(), message);
            chicago_transits.StopThreads(message, context); // stops current threads

            if (!MainActivity.message.getDestoryed()){ // If main activity has been started...
                message = MainActivity.message;
                String dir = message.getDir();
                String type= message.getTarget_type();
                String map_id =message.getTarget_station().getMap_id();
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
