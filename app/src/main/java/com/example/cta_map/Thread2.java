package com.example.cta_map;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class Thread2 extends Thread{
    Handler handler;
    public void run() {
        Looper.prepare();
        handler = new ExampleHandler();

        Message msg = Message.obtain();
        msg.what = 500000;

        handler.sendMessage(msg);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Looper.loop();
        Log.e("Thread "+Thread.currentThread().getName(), "End of run()");


    }


}
