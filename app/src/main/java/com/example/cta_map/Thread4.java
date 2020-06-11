package com.example.cta_map;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class Thread4 implements Runnable {
    final Message msg;
    String type;
    public Thread4(Message msg, String type){
        this.msg = msg;
        this.type = type;
    }
    @Override
    public void run() {
        String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+type;
        synchronized (this.msg){
            while (true) {

                try {
                    final Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                    final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                    this.msg.setMsg(train_list);
                    Log.e(Thread.currentThread().getName(), Thread.currentThread().getName()+ " has set the message and is waiting...");
                    this.msg.setDir("1");
                    this.msg.wait();
                    Log.e("update",Thread.currentThread().getName()+" is done waiting");


                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }




    }
}
