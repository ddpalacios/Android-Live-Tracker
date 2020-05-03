package com.example.cta_map;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

class Chicago_Transits {
    private BufferedReader reader;
    Chicago_Transits(BufferedReader reader){
        this.reader = reader;

    }

   String[] retrieve_station_coordinates(String station_name, String station_type){
       String line;
       while (true){
                    try {

                        if ((line = this.reader.readLine()) != null){
                            String[] tokens = line.split(",");
                             String stationCanidate = tokens[0].toLowerCase();
                            HashMap<String, String> train_lines = new HashMap<>();
                            HashMap<String, String> train_types = GetStation(tokens, train_lines); //HashMap of All train lines

                            if (stationCanidate.equals(station_name) && Boolean.parseBoolean(train_types.get(station_type))){
                                return getCord(tokens);


                            }

                             }else {break;}



                    }catch (IOException e){
                        e.printStackTrace();
                    }


                }
       return null;
       }



       String find_nearest_train_from(String stationName, String stationType, String trainDirection){
        // TODO: Retrieve All Correct Trains with specified direction and calculate/return nearest train from chosen station



       }




        private HashMap<String, String> GetStation(String [] tokens, HashMap<String, String> train_lines){

        // Train lines
        String red = tokens[1];
        String blue = tokens[2];
        String green = tokens[3];
        String brown = tokens[4];
        String purple = tokens[5];
        String yellow = tokens[6];
        String pink = tokens[7];
        String orange = tokens[8];

        // Add to our Data Structure
        train_lines.put("red", red);
        train_lines.put("blue", blue);
        train_lines.put("green", green);
        train_lines.put("purple", purple);
        train_lines.put("yellow", yellow);
        train_lines.put("pink", pink);
        train_lines.put("orange", orange);
        train_lines.put("brown", brown);



        return train_lines;
    }


        private String[] getCord(String [] tokens) {

            // coordinates parse
            String station_coord = tokens[9] + tokens[10];
            station_coord = station_coord.replace("(", "").replace(")", "");
            return station_coord.split(" ");
        }

}