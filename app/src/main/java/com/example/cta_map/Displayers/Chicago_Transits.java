package com.example.cta_map.Displayers;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.CTA_Stations;
import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.DataBase.MainStation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Chicago_Transits {

    public void create_main_station_table(BufferedReader reader, Context context){
        Database2 sqlite = new Database2(context);


        String line;
        int row=0;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    if (row == 0){
                        row++;
                        continue;
                    }
                    String[] tokens = (line.replaceAll("\"","")
                            .replaceAll(",,",",")).split(",");
                    MainStation mainStation = new MainStation(tokens[0], tokens[1], tokens[2], tokens[3]);
                    sqlite.addMainStations(mainStation);
                    row++;

                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            sqlite.close();

        }
    }

    @SuppressLint("LongLogTag")
    public void create_line_stops_table(BufferedReader reader, Context context) {
        Database2 sqlite = new Database2(context);

        String line;
        int row=0;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    if (row == 0){
                        row++;
                        continue;
                    }
                    String[] tokens = (line.replaceAll("\"","")
                            .replaceAll(",,",",")).split(",");


                    CTA_Stations cta_stations = new CTA_Stations(null);
                    cta_stations.setGreen(tokens[0]);
                    cta_stations.setRed(tokens[1]);
                    cta_stations.setBlue(tokens[2]);
                    cta_stations.setYellow(tokens[3]);
                    cta_stations.setPink(tokens[4]);
                    cta_stations.setOrange(tokens[5]);
                    cta_stations.setBrown(tokens[6]);
                    cta_stations.setPurple(tokens[7]);


                    sqlite.add_station_lines(cta_stations);
                    row++;


                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            sqlite.close();

        }
    }



    public void Create_TrainInfo_table(BufferedReader reader, Context context) {
        Database2 sqlite = new Database2(context);

        String line;
        int row=0;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    if (row == 0){
                        row++;
                        continue;
                    }
                    String[] tokens = (line.replaceAll("\"","")


                            .replaceAll(",,",",")).split(",");

                    CTA_Stations cta_stations = new CTA_Stations(tokens[0]);
                    cta_stations.setGreen(tokens[3]);
                    cta_stations.setRed(tokens[1]);
                    cta_stations.setBlue(tokens[2]);
                    cta_stations.setYellow(tokens[6]);
                    cta_stations.setPink(tokens[7]);
                    cta_stations.setOrange(tokens[8]);
                    cta_stations.setBrown(tokens[4]);
                    cta_stations.setPurple(tokens[5]);
                    cta_stations.setLat(tokens[9]
                            .replaceAll("\\(", "")
                            .replaceAll("\\)", "")
                            .replaceAll(",,",","));
                    cta_stations.setLon(tokens[10]
                            .replaceAll("\\(", "")
                            .replaceAll("\\)", "")
                            .replaceAll(",,",","));



                    sqlite.add_stations(cta_stations);
                    row++;

                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        sqlite.close();




    }

    public String[] retrieve_station_coordinates(Database2 sqlite, String station_id) {

        try {
            ArrayList<String> record = sqlite.get_table_record("cta_stops", "WHERE station_id = '"+station_id+"'");
            return new String[]{record.get(10), record.get(11)};


        }catch (Exception e){
            Log.e("SQLITE ERROR", "COULD NOT FIND STATION IN DATABASE!");
        }
        return null;
    }





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public BufferedReader setup_file_reader(Context context, int file){
        InputStream CSVfile = context.getResources().openRawResource(file);
        return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

    }





    public HashMap<String, String> get_train_info( String each_train, String station_type) {
        HashMap<String, String> train_info = new HashMap<>();

        String currTrain = each_train.replaceAll("\n", "")
                .replaceAll("\t", "")
                .replaceAll("<train>", "</train>")
                .replaceAll("</train>", "");


        currTrain = currTrain.replaceAll("<nextStaId>[\\s\\S]*?</nextStaId>", "");
        currTrain = currTrain.replaceAll("<nextStpId>[\\s\\S]*?</nextStpId>", "");
        currTrain = currTrain.replaceAll("<destSt>[\\s\\S]*?</destSt>", "");
        String main_station = get_xml_tag_value(currTrain, "<destNm>", "</destNm>");
        String train_direction = get_xml_tag_value(currTrain, "<trDr>", "</trDr>");
        String next_train_stop = get_xml_tag_value(currTrain, "<nextStaNm>", "</nextStaNm>");
        String predicted_arrival_time = get_xml_tag_value(currTrain, "<arrT>", "</arrT>");
        String isApproaching = get_xml_tag_value(currTrain, "<isApp>", "</isApp>");
        String isDelayed = get_xml_tag_value(currTrain, "<isDly>", "</isDly>");
        String train_lat = get_xml_tag_value(currTrain, "<lat>", "</lat>");
        String train_lon = get_xml_tag_value(currTrain, "<lon>", "</lon>");
        String train_id = get_xml_tag_value(currTrain, "<rn>", "</rn>");
        if (isApproaching != null) {
            train_info.put("isApproaching", isApproaching.replaceAll(" ", ""));
            train_info.put("isDelayed", isDelayed.replaceAll(" ", ""));
            String new_main = main_station.substring(2);
            train_info.put("main_station", new_main);
            train_info.put("train_id", train_id.replaceAll(" ", ""));
            String new_stop = next_train_stop.substring(2);
            train_info.put("next_stop", new_stop);
            try {
                train_info.put("next_stop_pred_arr_time",
                        predicted_arrival_time.trim().split(":")[0].replaceAll("20200822", "") + ":" +
                                predicted_arrival_time.trim().split(":")[1] + ":" +
                                predicted_arrival_time.trim().split(":")[2]);
            }catch (Exception e){
                e.printStackTrace();
            }
            train_info.put("train_direction", train_direction.replaceAll(" ", ""));
            train_info.put("train_lat", train_lat.replaceAll(" ", ""));
            train_info.put("train_lon", train_lon.replaceAll(" ", ""));
            train_info.put("station_type", station_type.replaceAll(" ", ""));

            if (train_info.isEmpty()) {
                return null;
            }
        }

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

        return R * c * 0.621371; // KM to Miles

    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public String TrainLineKeys(String station_type){
        HashMap<String, String> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red", "red");
        TrainLineKeyCodes.put("blue", "blue");
        TrainLineKeyCodes.put("brown", "brn");
        TrainLineKeyCodes.put("green", "g");
        TrainLineKeyCodes.put("orange", "org");
        TrainLineKeyCodes.put("pink", "pink");
        TrainLineKeyCodes.put("purple", "p");
        TrainLineKeyCodes.put("yellow", "y");

        return TrainLineKeyCodes.get(station_type);
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

//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public ArrayList<Integer> calculate_station_range_eta(HashMap<String, String> current_train_info, int start, int end,int dir, Context context){
//        Time time = new Time();
//        int starting_idx =0;
//        ArrayList<Integer> train_stop_etas = new ArrayList<>();
//        BufferedReader train_station_stops_reader = setup_file_reader(context, R.raw.train_line_stops);
//        ArrayList<String> all_stops = retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"), false);
//        Log.e("stops", all_stops+"");
//        List<String> all_stops_till_target = all_stops.subList(start, end);
//        Log.e("all stops", all_stops_till_target+"");
//
//        if (dir==1){
//            starting_idx = all_stops_till_target.size() -1;
//        }
//        for (int i=0; i < all_stops_till_target.size(); i++){
//            BufferedReader train_station_coordinates_reader = setup_file_reader(context, R.raw.train_stations);
//            String remaining_stop = all_stops_till_target.get(starting_idx);
//            String[] remaining_station_coordinates = retrieve_station_coordinates(train_station_coordinates_reader, remaining_stop, current_train_info.get("station_type"));
//            String[] current_train_loc = (current_train_info.get("train_lat") + ","+current_train_info.get("train_lon")).split(",");
//            double train_distance_to_next_stop = calculate_coordinate_distance(
//                    Double.parseDouble(current_train_loc[0]),
//                    Double.parseDouble(current_train_loc[1]),
//                    Double.parseDouble(remaining_station_coordinates[0]),
//                    Double.parseDouble(remaining_station_coordinates[1]));
//
//            int next_stop_eta = time.get_estimated_time_arrival(25, train_distance_to_next_stop);
//            if (dir == 1){
//                starting_idx --;
//            }
//            else{
//                starting_idx++;
//            }
//
//            train_stop_etas.add(next_stop_eta);
//        }
//    return train_stop_etas;
//    }

    public HashMap<String, Integer> train_speed_mapping(){
        HashMap<String, Integer> train_speeds = new HashMap<>();
        train_speeds.put("green", 25);
        train_speeds.put("red", 25);
        train_speeds.put("blue", 25);
        train_speeds.put("orange", 25);
        train_speeds.put("pink", 25);
        train_speeds.put("purple", 55);
        train_speeds.put("yellow", 25);
        train_speeds.put("brown", 25);

        return train_speeds;
    }


}
