package com.example.cta_map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;



public class Thread1 implements Runnable {
    Message msg;
    Bundle bb;
    public Thread1(Message msg, Bundle bb){
        this.msg = msg;
        this.bb = bb;


    }
    @Override
    public void run() {
        String target_station_type = bb.getString("station_type");
        String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+target_station_type;
            while (true) {
//                Log.e("Update", "START\n");
                synchronized (this.msg){
                try {
                    final Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    this.msg.setMsg(train_list);
//                    Log.e("mes", Thread.currentThread().getName()+ " has set the message and is waiting...");
                    this.msg.wait();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
//                    Log.e("mes", Thread.currentThread().getName()+ " is done waiting...");

                }

        }




    }
}