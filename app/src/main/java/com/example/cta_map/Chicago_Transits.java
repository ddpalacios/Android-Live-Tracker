package com.example.cta_map;
import android.annotation.SuppressLint;
import android.content.Context;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


class Chicago_Transits {


    @SuppressLint("LongLogTag")
    String[] retrieve_station_coordinates(BufferedReader reader, String station_name, String station_type) {

        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    String stationCanidate = tokens[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
//                    Log.e("station", stationCanidate + " "+ station_name);
                    HashMap<String, String> train_types = GetStation(tokens); //HashMap of All train lines
                    if (stationCanidate.equals(station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()) && Boolean.parseBoolean(train_types.get(station_type))) {
//                        Log.e("FOUND !!!!!!!!!!!!!!!!!!!!!!! station", stationCanidate + " "+ station_name);

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
    public BufferedReader setup_file_reader(Context context, int file){
        InputStream CSVfile = context.getResources().openRawResource(file);
        return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    ArrayList<String> retrieve_line_stations(BufferedReader reader, String station_type, Boolean remove_char){
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
                    train_lines.put("yellow", tokens[3]);
                    train_lines.put("pink", tokens[4]);
                    train_lines.put("orange", tokens[5]);
                    train_lines.put("brown", tokens[6]);
                    train_lines.put("purple", tokens[7]);
                    String stops = train_lines.get(station_type);
                    if (remove_char) {
                        train_line_stops.add(stops.replaceAll("[^a-zA-Z0-9]", ""));
                    }else{
                        train_line_stops.add(stops);
                    }
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


    public HashMap<String, String> get_train_info(BufferedReader reader, String each_train, String target_name, String station_type) {
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

        train_info.put("isApproaching", isApproaching.replaceAll(" ", ""));
        train_info.put("isDelayed", isDelayed.replaceAll(" ", ""));
        String new_main = main_station.substring(2);
        train_info.put("main_station", new_main);
        train_info.put("train_id", train_id.replaceAll(" ", ""));
        String new_stop = next_train_stop.substring(2);
        train_info.put("next_stop",new_stop);
        train_info.put("train_direction", train_direction.replaceAll(" ", ""));
        train_info.put("train_lat", train_lat.replaceAll(" ", ""));
        train_info.put("train_lon", train_lon.replaceAll(" ", ""));
        train_info.put("station_type", station_type.replaceAll(" ", ""));
        String main_station_name = train_info.get("main_station");

        String[] main_station_coordinates = retrieve_station_coordinates(reader, main_station_name, station_type);
        train_info.put("main_lat", main_station_coordinates[0]);
        train_info.put("main_lon", main_station_coordinates[1]);
        train_info.put("target_station", target_name);




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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<Integer> calculate_station_range_eta(HashMap<String, String> current_train_info, int start, int end,int dir, Context context){
        Time time = new Time();
        int starting_idx =0;
        ArrayList<Integer> train_stop_etas = new ArrayList<>();
        BufferedReader train_station_stops_reader = setup_file_reader(context, R.raw.train_line_stops);
        ArrayList<String> all_stops = retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"), false);
        Log.e("stops", all_stops+"");
        List<String> all_stops_till_target = all_stops.subList(start, end);
        Log.e("all stops", all_stops_till_target+"");

        if (dir==1){
            starting_idx = all_stops_till_target.size() -1;
        }
        for (int i=0; i < all_stops_till_target.size(); i++){
            BufferedReader train_station_coordinates_reader = setup_file_reader(context, R.raw.train_stations);
            String remaining_stop = all_stops_till_target.get(starting_idx);
            String[] remaining_station_coordinates = retrieve_station_coordinates(train_station_coordinates_reader, remaining_stop, current_train_info.get("station_type"));
            String[] current_train_loc = (current_train_info.get("train_lat") + ","+current_train_info.get("train_lon")).split(",");
            double train_distance_to_next_stop = calculate_coordinate_distance(
                    Double.parseDouble(current_train_loc[0]),
                    Double.parseDouble(current_train_loc[1]),
                    Double.parseDouble(remaining_station_coordinates[0]),
                    Double.parseDouble(remaining_station_coordinates[1]));

            int next_stop_eta = time.get_estimated_time_arrival(25, train_distance_to_next_stop);
            if (dir == 1){
                starting_idx --;
            }
            else{
                starting_idx++;
            }

            train_stop_etas.add(next_stop_eta);
        }
    return train_stop_etas;
    }

}
