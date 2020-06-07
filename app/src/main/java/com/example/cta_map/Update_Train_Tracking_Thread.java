package com.example.cta_map;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Document;

import org.jsoup.Jsoup;

import java.io.IOException;

public class Update_Train_Tracking_Thread extends Thread {
    String url;
    public Update_Train_Tracking_Thread(String url){
        this.url = url;

    }
        Handler handler;
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            Log.e(" Thread", "MyThread running");

        handler = new Handler() {
                public void handleMessage(@NonNull Message msg) {
                    // Act on the message
                    Log.e("Recieved", msg.toString()+"");
                }
            };

            Message msg = Message.obtain();
            try {
                msg.obj = Jsoup.connect(url).get(); // Some Arbitrary object
//                Handler.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }






            Looper.loop();

        }
    }
