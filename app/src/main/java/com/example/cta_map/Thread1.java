package com.example.cta_map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

public class Thread1 implements Runnable  {

    Context context;
    public Looper looper;
    ExampleHandler handler = new ExampleHandler();




    public void run() {
        String url;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes

            url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",
                   "red");


        Bundle bundle = new Bundle();
        while (true) {
            Message msg = handler.obtainMessage();
            try {
                Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                final String[] train_list = content.select("train").outerHtml().split("</train>");



                bundle.putStringArray("raw_train_content", train_list);

                msg.setData(bundle);

                handler.sendMessage(msg);
                Thread.sleep(10000);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}