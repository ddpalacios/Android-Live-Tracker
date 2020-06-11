package com.example.cta_map;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;


public class Thread4 implements Runnable {
    final Message msg;
    String type;
    String id;
    public Thread4(Message msg, String type, String id){
        this.msg = msg;
        this.type = type;
        this.id = id;
    }
    @Override
    public void run() {
        synchronized (this.msg){
            while (this.msg.IsSending()) {
                Chicago_Transits chicago_transits = new Chicago_Transits();
                Time times = new Time();
                String[] content = this.msg.getMsg();
                for (String each_train : content) {
                    HashMap<String, String> train_info = chicago_transits.get_train_info(each_train, this.type);
                    if (train_info.get("train_id").equals(id)) {
                        String train_lat = train_info.get("train_lat");
                        String train_lon = train_info.get("train_lon");
                        String train_next_stop = train_info.get("next_stop");
                        String train_direction = train_info.get("train_direction");
                        Log.e("NEXT", train_next_stop);





                        this.msg.setDir(train_direction);
                        this.msg.setCoord(train_lat, train_lon);
                        this.msg.setNextStop(train_next_stop);


//                        Log.e("FOUND", train_info + "");

                    }
                }
                    try {
                    this.msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Log.e(Thread.currentThread().getName(), "Done waiting");

            }

        }




    }
}
