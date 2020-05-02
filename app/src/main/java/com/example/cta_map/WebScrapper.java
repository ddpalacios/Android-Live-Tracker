package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

class WebScrapper {

    private String[] retrieve(String tag, Document content){
        final String current_content = content.select(tag).text();
        final String[] all_content = current_content.split(" ");



        return all_content;
    }


    void Connect( final String url, final Boolean connect, final  String[] tags){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (connect){
                    try {
                        Document content = Jsoup.connect(url).get();
                        Log.d("CONNECTED",content.toString());
//                        for (int each_tag=0; each_tag< tags.length; each_tag++){
//                            String[] full_content_of_current_tag = retrieve(tags[each_tag], content);
//                            Log.d("Content",full_content_of_current_tag[0]);
//
//                        }
//

                    }
                    catch (IOException e){
                        Log.d("ERROR", "ERROR WHEN CONNECTING");

                    }


                }

            }
        }).start();







    }


}
