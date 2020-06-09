package com.example.cta_map;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

class Thread2 implements Runnable
{


    Message msg;
    public Thread2(Message msg){
            this.msg = msg;
        }

    @Override
    public void run() {
        synchronized (this.msg){
//            while (true) {

//                Log.e(Thread.currentThread().getName(), "Notifying...");
//                this.msg.notifyAll();
//            }



        }

    }
}
