package com.example.cta_map;

import android.util.Log;

public class Thread3 implements Runnable {
    Message message;

    public Thread3(Message message){
        this.message = message;

    }


    @Override
    public void run() {
        int i=0;
            synchronized (this.message) {


        }



    }
}
