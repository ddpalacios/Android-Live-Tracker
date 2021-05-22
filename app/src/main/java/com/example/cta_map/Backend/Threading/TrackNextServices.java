package com.example.cta_map.Backend.Threading;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;

import java.util.ArrayList;

public class TrackNextServices extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Message message = API_Caller_Thread.msg;
        Chicago_Transits chicago_transits  = new Chicago_Transits();
        Boolean is_retrieving_first_nearest_train = message.getIs_retrieving_first_nearest_train();// intent.getBooleanExtra("isNew", false);
        Train train = message.getCurrentNotificationTrain();
        if (train == null){
            chicago_transits.StopThreads(message, context);
            chicago_transits.callThreads(context, message.getHandler() ,message, message.getDir(), message.getTarget_type(), message.getTARGET_MAP_ID(), false);


        }else {
            ArrayList<Train> current_incoming_trains = message.getOld_trains();
            if (current_incoming_trains == null) {
                Log.e("ERROR", "NO INCOMING TRAINS AVAILABLE");
                return;
            }
            int incoming_train_list_size = current_incoming_trains.size();
            Train new_train;
            if (is_retrieving_first_nearest_train != null && is_retrieving_first_nearest_train) {
                new_train = current_incoming_trains.get(0); // Find nearest train if train is out of scope to reset

            } else { // if train is still in scope - give option to keep tracking next train
                Integer initial_train_position = findTrainPosition(train, current_incoming_trains);
                if (initial_train_position != null && initial_train_position + 1 >= incoming_train_list_size) {
                    new_train = current_incoming_trains.get(0); // get first train since we reached end of train list
                } else {
                    new_train = current_incoming_trains.get(initial_train_position + 1);
                }
            }

            // now that we have new train, lets track it!
            chicago_transits.StopThreads(message, context);
            chicago_transits.reset(message.getOld_trains(), message);
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // Resetting train tracker
            cta_dataBase.commit(new_train, CTA_DataBase.TRAIN_TRACKER); // Adding new tracker

            cta_dataBase.close();
            String train_type = chicago_transits.train_line_code_to_regular(train.getRt());
            String map_id = new_train.getTarget_id();
            message.setNew_next_train_to_track(new_train);
            chicago_transits.callThreads(context, message.getHandler(), message, new_train.getTrDr(), train_type, map_id, false);

        }
    }

    private Integer findTrainPosition(Train train, ArrayList<Train> list_of_trains){
        int count = 0;
        for (Train incoming_train : list_of_trains){
            if (train.getRn().equals(incoming_train.getRn())){
                return count;
            }
            count+=1;
        }
        return null;
    }
}
