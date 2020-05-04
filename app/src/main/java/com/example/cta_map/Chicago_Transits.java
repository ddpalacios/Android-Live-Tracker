package com.example.cta_map;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
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



       String find_nearest_train_from(final String[] station_coordinates,final String stationName, final String stationType, final String SpecifiedTrainDirection){
        // TODO: Retrieve All Trains with specified direction and calculate/return nearest train from chosen station
           HashMap <String, String> StationTypeKey = TrainLineKeys();
           final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt="+StationTypeKey.get(stationType.toLowerCase());
           new Thread(new Runnable() {
               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
               @Override
               public void run() {
                   while (true) {
                       final ArrayList<Integer> indexies = new ArrayList<Integer>();
                       final ArrayList<String> chosenTrains = new ArrayList<String>();

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


//                           Log.e("Latititudes", Arrays.toString(latitude));
//                           Log.e("Longtitude", Arrays.toString(longtitude));
//                           Log.e("TrDR", Arrays.toString(train_direction));

                           for (int i=0; i< train_direction.length; i++){
                               String elem = train_direction[i];
                               if (elem.equals(SpecifiedTrainDirection)){
                                   indexies.add(i);

                               }

                           }
//                           Log.e("TRAINS", String.valueOf(indexies));

                           for (Integer index : indexies){
                               chosenTrains.add((latitude[index] + ","+ longtitude[index]));

                           }
//                           Log.e("Chosen Trains", String.valueOf(chosenTrains));
                           double closest_train = calculate_nearest_train_from(chosenTrains,station_coordinates,stationName, stationType , 1);
                           double feet = closest_train * 0.62137;
                           Log.e("DISTANCE", String.valueOf(feet)+ " mi away!");

















                       } catch (IOException | ParseException e) {
                           Log.d("Error", "Error in extracting");
                       }


                   }
               }


           }).start();





return null;


}



        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private double calculate_nearest_train_from(ArrayList<String> chosen_trains,String[] station_coordinates ,String station_name, String station_type, Integer num_trains) throws ParseException {
            ArrayList<Double> train_distance = new ArrayList<Double>();
            final int R = 6371; // Radious of the earth

            double station_lat = Double.parseDouble(station_coordinates[0]);
            double station_lon = Double.parseDouble(station_coordinates[1]);
//            Log.e("Cord", "LAT: " + station_lat+ " LON: "+ station_lon);
//
//
//
//
            for (String coord : chosen_trains){
                String[] train_cord = coord.split(",");
                double train_lat = Double.parseDouble(train_cord[0]);
                double train_lon = Double.parseDouble(train_cord[1]);
                Double latDistance = toRad(train_lat-station_lat);
                Double lonDistance = toRad(train_lon-station_lon);

                double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(toRad(station_lat)) * Math.cos(toRad(train_lat)) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

//
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                double distance = R * c;

                train_distance.add(distance);
            }
//
//
//
            Collections.sort(train_distance);
//            Log.e("CALCULATION", "Nearest train distance: " + train_distance.get(0));


        return train_distance.get(0);
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