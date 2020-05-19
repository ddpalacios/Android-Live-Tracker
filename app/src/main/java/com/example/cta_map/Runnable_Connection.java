package com.example.cta_map;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

class Runnable_Connection {

    void establish_connection(final String url) throws IOException {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"DefaultLocale", "WrongConstant"})
            @Override
            public void run() {

                Document content = null;
                try {
                    content = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (content != null){
                }



            }}).start();






    }

    void start(){


    }

}
