package com.example.cta_map;

import android.util.Log;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;

public class Thread4 implements Runnable {
    PipedReader r;
    PipedWriter w;
    public Thread4(PipedReader r, PipedWriter w){
        this.r = r;
        this.w = w;
    }

    @Override
    public void run() {
        ArrayList<String> f = new ArrayList<>();

        boolean connection = true;



            try {
                Log.e("dddd", r.ready()+"'");
                if (this.r.ready()) {
                    // print the char array
                    f.add((char) r.read() + "");
                    Log.e("size", "" + f);
                    


                }

                } catch (IOException e) {
                e.printStackTrace();
            }






    }
}
