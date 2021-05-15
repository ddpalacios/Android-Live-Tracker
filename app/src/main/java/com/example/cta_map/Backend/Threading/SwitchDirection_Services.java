package com.example.cta_map.Backend.Threading;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Displayers.Chicago_Transits;

public class SwitchDirection_Services extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Message message;
        Chicago_Transits chicago_transits = new Chicago_Transits();

        if (!MainActivity.message.getDestoryed()) { // If main activity has been started...
            message = MainActivity.message;
            Excecuteswitch(message);
            message.setMadeBroadcastSwitch(true);

        }else{
            message = API_Caller_Thread.msg;
            Excecuteswitch(message);
            message.setMadeBroadcastSwitch(true);

        }
        if (message.getT1().isAlive()) { // This is the case when we have the main activity previously declared
            chicago_transits.StopThreads(message, context);
            message.getT1().interrupt();

            chicago_transits.callThreads(context, message.getHandler(), message,message.getDir(), message.getTarget_type(), message.getTarget_station().getMap_id(), true);
        }else{
            message.getT1().interrupt();

            // if the app has not been started
            chicago_transits.callThreads(context, message.getHandler(), message,message.getDir(), message.getTarget_type(), message.getTarget_station().getMap_id(), true);

        }



    }


    private void Excecuteswitch(Message message){
        message.getT1().interrupt();
        String dir = message.getDir();
        message.setGreenNotified(false);
        message.setYellowNotified(false);
        message.setRedNotified(false);
        message.setApproachingNotified(false);
        if (dir !=null) {
            if (dir.equals("1")) {
                message.setDir("5");
            } else {
                message.setDir("1");
            }
        }
        Log.e("Service", "Direction changed to "+ message.getDir());

    }
}
