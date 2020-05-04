package com.example.cta_map;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
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



       String find_nearest_train_from(final String stationName, final String stationType, final String SpecifiedTrainDirection){
        // TODO: Retrieve All Trains with specified direction and calculate/return nearest train from chosen station
           HashMap <String, String> StationTypeKey = TrainLineKeys();
           final ArrayList<Integer> indexies = new ArrayList<Integer>();
           final ArrayList<String> chosenTrains = new ArrayList<String>();
           final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+StationTypeKey.get(stationType.toLowerCase());
           new Thread(new Runnable() {
               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
               @Override
               public void run() {
                   while (true) {

                       try {
                           Document content = Jsoup.connect(url).get();
                           final Elements lat = content.select("lat");
                           final Elements lon = content.select("lon");
                           final Elements isApp = content.select("isApp");
                           final Elements trDr = content.select("trDr");
                           final Elements dest = content.select("nextStaNm");

                           String[] latitude = lat.text().split(" ");
                           String[] longtitude = lon.text().split(" ");
                           String[] destination = dest.text().split(" ");
                           String[] isApproaching = isApp.text().split(" ");
                           String[] train_direction = trDr.text().split(" ");


                           Log.e("Latititudes", Arrays.toString(latitude));
                           Log.e("Longtitude", Arrays.toString(longtitude));
                           Log.e("TrDR", Arrays.toString(train_direction));
                           for (int i=0; i< train_direction.length; i++){
                               String elem = train_direction[i];
                               if (elem.equals(SpecifiedTrainDirection)){
                                   indexies.add(i);

                               }

                           }
                           Log.e("TRAINS", String.valueOf(indexies));

                           for (Integer index : indexies){
                               chosenTrains.add((latitude[index] + ","+ longtitude[index]));

                           }
                           Log.e("Chosen Trains", String.valueOf(chosenTrains));
                           String closest_train = calculate_nearest_train_from(chosenTrains,stationName, stationType , 1);












                           break;





                       } catch (IOException | ParseException e) {
                           Log.d("Error", "Error in extracting");
                       }


                   }
               }


           }).start();





return null;


}



        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String calculate_nearest_train_from(ArrayList<String> chosen_trains, String station_name, String station_type, Integer num_trains) throws ParseException {

            final int R = 6371; // Radious of the earth
//            String[] station_cord = retrieve_station_coordinates(station_name, station_type);
//            NumberFormat nf = NumberFormat.getInstance();
//            double station_lat = Objects.requireNonNull(nf.parse(Arrays.toString(station_cord))).doubleValue();
//            double station_lat = Double.parseDouble(station_cord[0]);
//            double station_lon = Double.parseDouble(station_cord[1]);

//            Log.e("STATION CORD", Arrays.toString(station_cord));

//            for (String coord : chosen_trains){
//                Log.e("d", String.valueOf(station_lat));
//                String[] train_cord = coord.split(",");
//                double train_lat = Double.parseDouble(train_cord[0]);
//                double train_lon = Double.parseDouble(train_cord[1]);
//                Double latDistance = toRad(train_lat-station_lat);
//                Double lonDistance = toRad(train_lon-station_lon);
//
//                double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
//                    Math.cos(toRad(station_lat)) * Math.cos(toRad(train_lat)) *
//                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//
//
//                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//                double distance = R * c;
//
//                Log.e("CALCULATION", "The distance between two lat and long is::" + distance);


//            }





//            Double lat1 = Double.parseDouble(args[0]);
//            Double lon1 = Double.parseDouble(args[1]);
//            Double lat2 = Double.parseDouble(args[2]);
//            Double lon2 = Double.parseDouble(args[3]);
//            Double latDistance = toRad(lat2-lat1);
//            Double lonDistance = toRad(lon2-lon1);
//            Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
//                    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
//                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//            Double distance = R * c;
//
//            System.out.println(“The distance between two lat and long is::” + distance);





        return null;
        }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

       private HashMap<String, String> TrainLineKeys(){
        HashMap<String, String> TrainLineKeyCodes  = new HashMap<>();
           TrainLineKeyCodes.put("red", "red");
           TrainLineKeyCodes.put("green", "g");
           TrainLineKeyCodes.put("purple", "p");
           TrainLineKeyCodes.put("orange", "org");
           TrainLineKeyCodes.put("pink", "pink");
           TrainLineKeyCodes.put("yellow", "y");
           TrainLineKeyCodes.put("blue", "blue");

           return TrainLineKeyCodes;
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
            String station_coord = tokens[9] +" "+tokens[11];
            station_coord = station_coord.replace("(", "").replace(")", "").replace("\"", "");


            return station_coord.split(" ");
        }

}