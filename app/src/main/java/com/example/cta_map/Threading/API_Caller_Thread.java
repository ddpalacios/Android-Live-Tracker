package com.example.cta_map.Threading;

import android.util.Log;

import com.example.cta_map.Displayers.Chicago_Transits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;


public class API_Caller_Thread implements Runnable {
    Message msg;
    HashMap<String, String> record;
    boolean willCommunicate;
    public API_Caller_Thread(Message msg, HashMap<String, String> record, boolean willCommunicate){
        this.msg = msg;
        this.record = record;
        this.willCommunicate = willCommunicate;
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

                    this.msg.setMsg(train_list);

                    if (this.willCommunicate){
                        Log.e("Url", url);
                        Log.e("Sending", train_list.length +" Trains.");
                        Log.e(Thread.currentThread().getName(),  "is waiting... ");

                    }

                    this.msg.wait();

                    if (this.willCommunicate) {
                        Log.e("update", Thread.currentThread().getName() + " is done waiting");
                    }


                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}