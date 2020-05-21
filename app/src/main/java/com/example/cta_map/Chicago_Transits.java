package com.example.cta_map;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;


class Chicago_Transits {
    private BufferedReader reader;

    Chicago_Transits(BufferedReader reader) {
        this.reader = reader;

    }

    String[] retrieve_station_coordinates(String station_name, String station_type) {

        String line;
        while (true) {
            try {
                if ((line = this.reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    String stationCanidate = tokens[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    HashMap<String, String> train_types = GetStation(tokens); //HashMap of All train lines

                    if (stationCanidate.equals(station_name.replaceAll("[^a-zA-Z0-9]", "")) && Boolean.parseBoolean(train_types.get(station_type))) {
                        return getCord(tokens);
                    }

                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    ArrayList<String> retrieve_line_stations(BufferedReader reader, String station_type){
        String line;
        ArrayList<String> train_line_stops = new ArrayList<>();
        HashMap<String, String> train_lines = new HashMap<>();
        while (true){
            try{
                if ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");

                    train_lines.put("green", tokens[0]);
                    train_lines.put("red", tokens[1]);
                    train_lines.put("blue", tokens[2]);


                    String stops = train_lines.get(station_type);
                    train_line_stops.add(stops.replaceAll("[^a-zA-Z0-9]", "").replaceAll(" ", "").toLowerCase());


                }else{
                    break;
                }



            }catch (IOException e){
                e.printStackTrace();
            }


        }
        train_line_stops.remove(0);
        if (train_line_stops.contains("null")){
            train_line_stops.removeAll(Collections.singleton("null"));
        }



        return train_line_stops;


    }



    private HashMap<String, String> GetStation(String[] tokens) {
        HashMap<String, String> train_lines = new HashMap<>();
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

    private String[] getCord(String[] tokens) {

        // coordinates parse
        String station_coord = tokens[9] + " " + tokens[11];
        station_coord = station_coord.replace("(", "").replace(")", "").replace("\"", "");

        return station_coord.split(" ");
    }


    public HashMap<String, String> get_train_info(String each_train, String station_type) {
        HashMap<String, String> train_info = new HashMap<>();

        String currTrain = each_train.replaceAll("\n", "")
                .replaceAll(" ", "")
                .replaceAll("<train>", "</train>")
                .replaceAll("</train>", "");

        currTrain = currTrain.replaceAll("<nextStaId>[\\s\\S]*?</nextStaId>", "");
        currTrain = currTrain.replaceAll("<nextStpId>[\\s\\S]*?</nextStpId>", "");
        currTrain = currTrain.replaceAll("<rn>[\\s\\S]*?</rn>", "");
        currTrain = currTrain.replaceAll("<destSt>[\\s\\S]*?</destSt>", "");
        String main_station = get_xml_tag_value(currTrain, "<destNm>", "</destNm>");
        String train_direction = get_xml_tag_value(currTrain, "<trDr>", "</trDr>");
        String next_train_stop = get_xml_tag_value(currTrain, "<nextStaNm>", "</nextStaNm>");
        String predicted_arrival_time = get_xml_tag_value(currTrain, "<arrT>", "</arrT>");
        String isApproaching = get_xml_tag_value(currTrain, "<isApp>", "</isApp>");
        String isDelayed = get_xml_tag_value(currTrain, "<isDly>", "</isDly>");
        String train_lat = get_xml_tag_value(currTrain, "<lat>", "</lat>");
        String train_lon = get_xml_tag_value(currTrain, "<lon>", "</lon>");
        train_info.put("isApproaching", isApproaching);
        train_info.put("isDelayed", isDelayed);
        train_info.put("main_station", main_station.toLowerCase().replace(" ", ""));
//    train_info.put("arrival_time", predicted_arrival_time);
        train_info.put("next_stop", next_train_stop.toLowerCase().replace(" ", ""));
        train_info.put("train_direction", train_direction);
        train_info.put("train_lat", train_lat);
        train_info.put("train_lon", train_lon);
        String main_station_name = train_info.get("main_station");

        String[] main_station_coordinates = retrieve_station_coordinates(main_station_name, station_type);
        train_info.put("main_lat", main_station_coordinates[0]);
        train_info.put("main_lon", main_station_coordinates[1]);




        return train_info;

    }


    private String get_xml_tag_value(String raw_xml, String startTag, String endTag){

        return StringUtils.substringBetween(raw_xml, startTag, endTag);
    }


    public Double calculate_coordinate_distance(double lat1, double lon1, double lat2, double lon2){
        final int R = 6371; // Radious of the earth


        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);


        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;

    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public HashMap<String, String> TrainLineKeys(){
        HashMap<String, String> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red", "red");
        TrainLineKeyCodes.put("blue", "blue");
        TrainLineKeyCodes.put("brown", "brn");
        TrainLineKeyCodes.put("green", "g");
        TrainLineKeyCodes.put("orange", "org");
        TrainLineKeyCodes.put("pink", "pink");
        TrainLineKeyCodes.put("purple", "p");
        TrainLineKeyCodes.put("yellow", "y");

        return TrainLineKeyCodes;
    }


        public void ZoomIn(GoogleMap mMap, Float zoomLevel, String[] coord){
        assert coord != null;
        LatLng target = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(zoomLevel)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(90)                  // Sets the tilt of the camera to 40 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


}
