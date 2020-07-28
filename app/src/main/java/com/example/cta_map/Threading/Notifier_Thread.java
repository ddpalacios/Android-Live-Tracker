package com.example.cta_map.Threading;


import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Notifier_Thread implements Runnable {
    final Message message;
    Context context;
    android.os.Handler handler;
    Thread t1, t2,t3;
    boolean willCommunicate;
    public Notifier_Thread(Message message, Context context, Thread t1, Thread t2, Thread t3,boolean willCommunicate){
        this.message = message;
        this.context = context;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.willCommunicate = willCommunicate;

    }

    @Override
    public void run() {
        this.t1.setPriority(7);
        this.t2.setPriority(2);
        this.t3.setPriority(1);
        this.t1.start();
//        this.t2.start();
//        this.t3.start();
        Log.e("DONE", "All three threads have finished execution");





//        while (true) {
//            Bundle bundle = new Bundle();
//            android.os.Message msg = this.handler.obtainMessage();
//
////            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
//
//            synchronized (this.message) {
//                if(!this.message.IsSending()){
////                    break;
//                }
//                ArrayList<HashMap> chosen_trains = this.message.get_chosen_trains();
////                for (HashMap t: chosen_trains){
//////                    Log.e(Thread.currentThread().getName(), t+"");
////                    if (t.get("train_eta")== null){
////                        this.message.notifyAll();
////
////                    }
////                }
//                ArrayList<HashMap> ignored_trains = this.message.getIgnored();
//
//                if (this.message.get_train_etas() !=null){
//                    HashMap train_etas= this.message.get_train_etas();
//                    bundle.putSerializable("station_range_eta", train_etas);
//                }
//
//
//                String main_station = this.message.getMainStation();
//
//                if (this.willCommunicate){
//                    Log.e("notifier", "Recieved "+ chosen_trains.get(0).get("train_eta")+"");
//                    Log.e("notifier", "Recieved "+ chosen_trains.size()+" Are Displaying.");
////                    Log.e(Thread.currentThread().getName(), "Sending to UI...");
//                }
//
////                if (chosen_trains.get(0).get("train_eta")!= null) {
//
//                    bundle.putSerializable("chosen_trains", chosen_trains);
//                    bundle.putSerializable("ignored_trains", ignored_trains);
//                    bundle.putString("main_station", main_station);
//                    msg.setData(bundle);
//                    handler.sendMessage(msg);
//                    this.message.notifyAll();
//
////                }else{
////                    this.message.notifyAll();
////                }
//
//                if (!this.message.getClicked()) {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        Log.e(Thread.currentThread().getName(), "Intrrupted");
//                    }
//                }
//                else{
//                    this.message.setClicked(false);
//                }

//
//            }
//
//
//        }
//
//
    }
}
