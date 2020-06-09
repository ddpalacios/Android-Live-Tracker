package com.example.cta_map;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.logging.Handler;


public class Thread3 implements Runnable {
    final Message message;
    Context context;
    android.os.Handler handler;

    public Thread3(Message message, android.os.Handler handler){
        this.message = message;
        this.handler = handler;

    }

    @Override
    public void run() {
        int i=0;
        while (true) {
            Bundle bundle = new Bundle();
            android.os.Message msg = this.handler.obtainMessage();

            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            synchronized (this.message) {

                ArrayList<Integer> train_etas =this.message.get_train_etas();
                ArrayList<HashMap> chosen_trains = this.message.get_chosen_trains();
                bundle.putIntegerArrayList("train_etas", train_etas);
                bundle.putSerializable("chosen_trains", chosen_trains);
                msg.setData(bundle);

                Log.e("train etas", "Sending to UI...");

                handler.sendMessage(msg);

                this.message.notify();
                Log.e("train etas", "Notified");
                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

            }


        }


    }
}
