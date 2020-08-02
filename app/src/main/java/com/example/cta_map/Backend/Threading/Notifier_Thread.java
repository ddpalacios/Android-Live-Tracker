package com.example.cta_map.Backend.Threading;


import android.content.Context;
import android.util.Log;


public class Notifier_Thread implements Runnable {
    Thread t1, t2,t3;
    public Notifier_Thread( Thread t1, Thread t2, Thread t3){
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;

    }

    @Override
    public void run() {
            this.t1.start();
            this.t2.start();
            this.t3.start();
    }
}
