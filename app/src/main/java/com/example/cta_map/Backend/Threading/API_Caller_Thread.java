package com.example.cta_map.Backend.Threading;

import android.util.Log;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;

public class API_Caller_Thread implements Runnable {
    String station_type;
    final Message msg;
    String TAG = "API CALLER";
    private volatile boolean cancelled = false;
    public API_Caller_Thread(Message msg){
        this.msg = msg;
    }
    @Override
    public void run() {
        this.msg.keepSending(true);
        this.msg.setDirectionChanged(false);
        this.msg.setSendingNotifications(false);
        synchronized (this.msg){
            while (this.msg.IsSending()) {
                if (cancelled) {
                    break;
                }
                if (this.msg.getTarget_type()!= null) {
                    ArrayList<Train> new_incoming_trains = call_cta_rest(msg.getTarget_type());
                    this.msg.setIncoming_trains(new_incoming_trains);
                }
                try {
                    Log.e(TAG, "is waiting... ");
                    this.msg.wait();
                    this.msg.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (cancelled){
                        break;
                    }
                }
            }
        }
        Log.e(TAG, "is Killed.");

    }

    public void cancel() {
        cancelled = true;
    }

    public ArrayList<Train> call_cta_rest(String station_type)  {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ArrayList<Train> all_incoming_trains = null;
        String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+chicago_transits.TrainLineKeys(station_type.trim().toLowerCase());
        try {
            final Document TRAIN_RESPONSE = Jsoup.connect(url).get(); // JSOUP to webscrape XML
            final String[] train_list = TRAIN_RESPONSE.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
            all_incoming_trains = new ArrayList<>();
            for (String raw_train : train_list) {
                Train train = chicago_transits.get_train_info(raw_train);
                train.setTrain_type(station_type.toLowerCase());
                all_incoming_trains.add(train);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return all_incoming_trains;
    }
}