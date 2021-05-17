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
        Message message = (MainActivity.message !=null ? MainActivity.message : API_Caller_Thread.msg);
        Chicago_Transits chicago_transits = new Chicago_Transits();


        if (message.getT1().isAlive()) { // This is the case when we have the main activity previously declared
            chicago_transits.StopThreads(message, context);
            Excecuteswitch(message);
            chicago_transits.callThreads(context, message.getHandler(), message,message.getDir(), message.getTarget_type(), message.getTarget_station().getMap_id(), false);
        }else{
            // if the app has not been started
            Excecuteswitch(message);
            chicago_transits.callThreads(context, message.getHandler(), message,message.getDir(), message.getTarget_type(), message.getTarget_station().getMap_id(), false);

        }



    }


    private void Excecuteswitch(Message message){
        message.getT1().interrupt();
        String dir = message.getDir();
        message.setGreenNotified(false);
        message.setYellowNotified(false);
        message.setRedNotified(false);
        message.setApproachingNotified(false);
        message.setDoneNotified(false);
        if (dir !=null) {
            if (dir.equals("1")) {
                message.setDir("5");
            } else {
                message.setDir("1");
            }
        }
        message.setMadeBroadcastSwitch(true);
        Log.e("Service", "Direction changed to "+ message.getDir());

    }
}
