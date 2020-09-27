package com.example.cta_map.Backend.Threading;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.example.cta_map.Displayers.Chicago_Transits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class API_Caller_Thread implements Runnable {
    final Message msg;
   String station_type;
    boolean willCommunicate;
    String TAG = Thread.currentThread().getName();

    public API_Caller_Thread(Message msg, String station_type, boolean willCommunicate){
        this.msg = msg;
        this.station_type = station_type;
        this.willCommunicate = willCommunicate;
    }
    @Override
    public void run() {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+chicago_transits.TrainLineKeys(this.station_type.trim().toLowerCase());
        this.msg.keepSending(true);
        synchronized (this.msg){
            while (this.msg.IsSending()) {
                try {
                    final Document TRAIN_RESPONSE = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train_list = TRAIN_RESPONSE.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    ArrayList<IncomingTrains> all_incoming_trains = new ArrayList<>();
                    for (String raw_train : train_list){
                       IncomingTrains incoming_train = chicago_transits.get_train_info(raw_train);
                        all_incoming_trains.add(incoming_train);
                    }

                    this.msg.setIncoming_trains(all_incoming_trains);
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