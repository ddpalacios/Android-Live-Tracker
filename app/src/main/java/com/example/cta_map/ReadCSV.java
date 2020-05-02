package com.example.cta_map;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadCSV {
    File file;
    public ReadCSV(String csv_file){
        this.file = new File(csv_file);

    }

    public String getStationCoord(String station, String line){
        String coordinates=null;
        File file_ = new File("/home/daniel/StudioProjects/LIVE-TRACKING-CTA-MOBILE/app/src/main/java/com/example/cta_map/train_stations.csv");
        try {
            Log.d("Awaiting file", "Opening csv file: "+this.file);
            Scanner inputStream = new Scanner(file_);
            while (inputStream.hasNext()){
                String data = inputStream.next();
                String[] values = data.split(",");
                Log.d("Values", values[4]);
            }


        }catch (FileNotFoundException e){
            Log.d("ERROR", "ERROR FILE NOT FOUND!!!");

            e.printStackTrace();
        }



        return coordinates;

    }

}
