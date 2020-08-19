package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.UserLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class Train_Estimations_Thread implements Runnable {
    final Message msg;
    android.os.Handler handler;
    HashMap<String, String> tracking_record;
    boolean willCommunicate;
    UserLocation userLocation;
    Context context;
    public Train_Estimations_Thread(Message msg, UserLocation userLocation,   HashMap<String, String> tracking_record,android.os.Handler handler, Context context, boolean willCommunicate) {
        this.msg = msg;
        this.tracking_record = tracking_record;
        this.context = context;
        this.handler = handler;
        this.userLocation = userLocation;
        this.willCommunicate = willCommunicate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
            while (this.msg.IsSending()){
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
                synchronized (this.msg) {

                Bundle bundle = new Bundle();
                android.os.Message handler_msg = this.handler.obtainMessage();
                HashMap<String, ArrayList<HashMap>> parsed_train_data = this.msg.getParsedTrainData();
                TreeMap<Integer, String> treeMap = this.msg.getTrainMap();
                if (parsed_train_data == null) {
                    Log.e(Thread.currentThread().getName(), "Data is NULL");
                    this.msg.notifyAll();
                    continue;
                }

                bundle.putSerializable("estimated_train_data", parsed_train_data);
                bundle.putSerializable("sorted_train_eta_map", treeMap);
                handler_msg.setData(bundle);
                handler.sendMessage(handler_msg);
                this.msg.setParsedTrainData(null);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    this.msg.notify();


            }

        }




        }
    }
