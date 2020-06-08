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
import java.util.List;



public class Thread1 implements Runnable {
    private final List<String[]> taskQueue;
    private final int           MAX_CAPACITY;
    ExampleHandler handler = new ExampleHandler();
    public Thread1(List<String[]> sharedQueue, int size)
    {
        this.taskQueue = sharedQueue;
        this.MAX_CAPACITY = size;
    }

    @Override
    public void run()
    {
        String url;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes

        url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",
                "red");


        Bundle bundle = new Bundle();
        while (true) {

            try {
                Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                final String[] train_list = content.select("train").outerHtml().split("</train>");
                produce(train_list);

            }
            catch (InterruptedException | IOException ex)
            {
                ex.printStackTrace();
            }


//            Message msg = handler.obtainMessage();
//            try {
//                Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
//                final String[] train_list = content.select("train").outerHtml().split("</train>");
//
//                bundle.putStringArray("raw_train_content", train_list);
//
//                msg.setData(bundle);
//
//                handler.sendMessage(msg);
//                Thread.sleep(10000);
//
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
        }

    }

    private void produce(String[] i) throws InterruptedException
    {
        synchronized (taskQueue)
        {
            while (taskQueue.size() == MAX_CAPACITY)
            {
                System.out.println("Queue is full " + Thread.currentThread().getName() + " is waiting , size: " + taskQueue.size());
                taskQueue.wait();
            }

            Thread.sleep(1000);
            for (String train: i){
                taskQueue.add(train);

            }




            taskQueue.notifyAll();
        }
    }
}



//public class Thread1 implements Runnable  {
//    ExampleHandler handler = new ExampleHandler();
//
//    public void run() {
//        String url;
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
//
//            url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",
//                   "red");
//
//
//        Bundle bundle = new Bundle();
//        while (true) {
//            Message msg = handler.obtainMessage();
//            try {
//                Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
//                final String[] train_list = content.select("train").outerHtml().split("</train>");
//
//                bundle.putStringArray("raw_train_content", train_list);
//
//                msg.setData(bundle);
//
//                handler.sendMessage(msg);
//                Thread.sleep(10000);
//
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

//
//    }
//}