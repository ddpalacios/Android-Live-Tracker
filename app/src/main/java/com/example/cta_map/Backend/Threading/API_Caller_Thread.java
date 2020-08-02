package com.example.cta_map.Backend.Threading;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.example.cta_map.Displayers.Chicago_Transits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;


public class API_Caller_Thread implements Runnable {
    final Message msg;
    HashMap<String, String> record;
    boolean willCommunicate;
    android.os.Handler handler;
    String TAG = Thread.currentThread().getName();
    public API_Caller_Thread(Message msg, HashMap<String, String> record, boolean willCommunicate){
        this.msg = msg;
        this.record = record;
        this.willCommunicate = willCommunicate;
    }
    @Override
    public void run() {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+chicago_transits.TrainLineKeys(this.record.get("station_type").replaceAll(" ", ""));
        synchronized (this.msg){
            while (this.msg.IsSending()) {
                try {
                    final Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    this.msg.setRawTrainList(train_list);
                    if (this.willCommunicate){
                        Log.e(TAG, url);
                        Log.e(TAG, train_list.length +" Trains.");
                        Log.e(TAG,  "is waiting... ");

                    }

                    this.msg.setStatus(true);
                    this.msg.wait();

                } catch (IOException e) {
                    Log.e("CONNECTION ERROR", "FAILED TO CONNECT TO URL");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}