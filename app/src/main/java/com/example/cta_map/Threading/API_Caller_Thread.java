package com.example.cta_map.Threading;

import android.os.Bundle;
import android.util.Log;

import com.example.cta_map.Displayers.Chicago_Transits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.logging.Handler;


public class API_Caller_Thread implements Runnable {
    Message msg;
    HashMap<String, String> record;
    boolean willCommunicate;
    android.os.Handler handler;
    public API_Caller_Thread(Message msg, HashMap<String, String> record,  android.os.Handler handler, boolean willCommunicate){
        this.msg = msg;
        this.record = record;
        this.willCommunicate = willCommunicate;
        this.handler = handler;
    }
    @Override
    public void run() {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+chicago_transits.TrainLineKeys(this.record.get("station_type"));
        synchronized (this.msg){
            while (this.msg.IsSending()) {
                try {
                    final Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    if (train_list.length ==1 && train_list[0].equals("")){
                        Bundle bundle = new Bundle();
                        android.os.Message msg = this.handler.obtainMessage();
                        bundle.putBoolean("No_Trains", true);
                        msg.setData(bundle);
//                        handler.sendMessage(msg);
                        continue;
                    }
//                    this.msg.setMsg(train_list);
//                    if (this.willCommunicate){
//                        Log.e("Url", url);
//                        Log.e("Found", train_list.length +" Trains.");
//                        Log.e(Thread.currentThread().getName(),  "is waiting... ");
//
//                    }
//
                    this.msg.setStatus(true);
                    this.msg.wait();
//
//
                } catch (IOException | InterruptedException e) {
                    Log.e("CONNECTION ERROR", "FAILED TO CONNECT TO URL");
                    e.printStackTrace();
                }


            }

        }
    }
}