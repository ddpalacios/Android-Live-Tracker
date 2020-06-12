package com.example.cta_map.Threading;

import android.os.Bundle;
import android.os.Handler;

public class Thread5 implements Runnable {
    Message message;
    Handler handler;


    public Thread5(Message msg, Handler handler){
        this.handler = handler;

        this.message = msg;
    }

    @Override
    public void run(){
        while (true){
            Bundle bundle = new Bundle();

            android.os.Message msg = this.handler.obtainMessage();

            try { Thread.sleep(700); } catch (InterruptedException e) { e.printStackTrace(); }
            synchronized (this.message){
                if(!this.message.IsSending()){
                    break;
                }

                bundle.putString("train_dir", this.message.getDir());
                bundle.putString("train_coordinates", this.message.getCoord());
                bundle.putString("train_next_stop", this.message.getNextStop());


                msg.setData(bundle);

//                Log.e("train etas", "Sending to UI...");

                handler.sendMessage(msg);

                this.message.notify();
                try { Thread.sleep(700); } catch (InterruptedException e) { e.printStackTrace(); }


            }



        }





//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//            while (true) {
//                synchronized (this.msg) {
//
//
//                    this.msg.notify();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }
//
//
//
    }
}
