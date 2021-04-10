package com.example.cta_map.Displayers;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Adapters.CustomInfoWindowAdapter;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.CTA_Stops;
import com.example.cta_map.DataBase.L_stops;
import com.example.cta_map.DataBase.MainStation;
import com.example.cta_map.DataBase.Markers;
import com.example.cta_map.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class Chicago_Transits {

    public  Integer getTrainImage(String train_line){
        if (train_line.toLowerCase().equals("purple")){
            train_line = "p";

        }else if(train_line.toLowerCase().equals("orange")){
            train_line = "org";

        }else if(train_line.toLowerCase().equals("yellow")){
            train_line = "y";

        }else if(train_line.toLowerCase().equals("green")){
            train_line = "g";
        }else if(train_line.toLowerCase().equals("brown")){
            train_line = "brn";

        }
        HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();
        train_line = train_line.toLowerCase().trim();
        TrainLineKeyCodes.put("red", R.drawable.red );
        TrainLineKeyCodes.put("blue", R.drawable.blue);
        TrainLineKeyCodes.put("brn", R.drawable.brown);
        TrainLineKeyCodes.put("g", R.drawable.green);
        TrainLineKeyCodes.put("org", R.drawable.orange);
        TrainLineKeyCodes.put("pink", R.drawable.pink);
        TrainLineKeyCodes.put("p", R.drawable.purple);
        TrainLineKeyCodes.put("y", R.drawable.yellow);
        return TrainLineKeyCodes.get(train_line);
    }


    public void createMarkerTable(Context context){
        CTA_DataBase cta_dataBase= new CTA_DataBase(context);
        ArrayList<Object> redLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "RED = '1'", null,null);
        ArrayList<Object> BlueLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "BLUE = '1'",null, null);
        ArrayList<Object> GreenLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "G = '1'", null,null);
        ArrayList<Object> OrangeLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "ORG = '1'", null,null);
        ArrayList<Object> PurpleLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "P = '1'", null,null);
        ArrayList<Object> BrownLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "BRN = '1'", null,null);
        ArrayList<Object> PinkLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "PINK = '1'", null,null);
        ArrayList<Object> YellowLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "Y = '1'", null,null);
        populateMarkers(context, redLine, "red");
        populateMarkers(context, BlueLine, "blue");
        populateMarkers(context, GreenLine, "green");
        populateMarkers(context, PurpleLine, "purple");
        populateMarkers(context, BrownLine, "brown");
        populateMarkers(context, YellowLine, "yellow");
        populateMarkers(context, PinkLine, "pink");
        populateMarkers(context, OrangeLine, "orange");




    }
    private void populateMarkers(Context context, ArrayList<Object> all_stations, String station_type){
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Markers> list_of_markers = new ArrayList<>();
        for (Object station : all_stations){
            Markers marker = new Markers();
            HashMap<String, String> current_station = (HashMap<String, String>) station;
            marker.setMarker_name(current_station.get("STATION_NAME"));
            marker.setMarker_id(current_station.get("MAP_ID"));
            marker.setMarker_type(station_type);
            marker.setMarker_lat(Double.parseDouble(current_station.get("LAT")));
            marker.setMarker_lon(Double.parseDouble(current_station.get("LON")));
            list_of_markers.add(marker);

        }
        for (Markers marker : list_of_markers) {
            cta_dataBase.commit(marker, "MARKERS");
        }
    }








    public void create_main_station_table(BufferedReader reader, Context context){
        CTA_DataBase sqlite = new CTA_DataBase(context);
        try{
//            ArrayList<Object> cta_stops_table = sqlite.excecuteQuery("*", "MAIN_STATIONS", null, null);
//            if (cta_stops_table != null){
//                sqlite.close();
//                Log.e(Thread.currentThread().getName(), "RECORD EXSITS");
//
//                return;
//            }
        }catch (Exception e){Log.e(Thread.currentThread().getName(), "No MainStations table, will create!");}
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



                    String station_type = tokens[0];
                    String northbound = tokens[1];
                    String southbound = tokens[2];
                    String express = tokens[3];

                    MainStation mainStation = new MainStation();
                    mainStation.setStationType(station_type);
                    mainStation.setNorthBound(northbound);
                    mainStation.setSouthBound(southbound);
                    mainStation.setExpress(express);
                    sqlite.commit(mainStation, "MAIN_STATIONS");

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

    public ArrayList<String> create_line_stops_table(BufferedReader reader, Context context, String type) {
        CTA_DataBase sqlite = null;
           sqlite = new CTA_DataBase(context);
        String line;
        int row=0;
        ArrayList<String> ordered_station_list = new ArrayList<>();
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    if (row == 0) {
                        row++;
                        continue;
                    }
                    String[] tokens = (line.replaceAll("\"", "")
                            .replaceAll(",,", ",")).split(",");
                    L_stops cta_STOPS = new L_stops();

                    if (type != null){
                    switch (type.toLowerCase()) {
                        case "red":
                            // code block
                            ordered_station_list.add(tokens[2]);
                            break;
                        case "blue":
                            // code block
                            ordered_station_list.add(tokens[3]);
                            break;
                        case "brown":
                            ordered_station_list.add(tokens[7]);
                            break;


                        case "purple":
                            ordered_station_list.add(tokens[8]);
                            break;
                        case "yellow":
                            ordered_station_list.add(tokens[4]);
                            break;

                        case "pink":
                            ordered_station_list.add(tokens[5]);
                            break;

                        case "green":
                            ordered_station_list.add(tokens[1]);
                            break;

                        case "orange":
                            ordered_station_list.add(tokens[6]);
                            break;

                        default:
                            cta_STOPS.setGreen(tokens[1]);
                            cta_STOPS.setRed(tokens[2]);
                            cta_STOPS.setBlue(tokens[3]);
                            cta_STOPS.setYellow(tokens[4]);
                            cta_STOPS.setPink(tokens[5]);
                            cta_STOPS.setOrange(tokens[6]);
                            cta_STOPS.setBrown(tokens[7]);
                            cta_STOPS.setPurple(tokens[8]);

                            sqlite.commit(cta_STOPS, "L_STOPS");
                            row++;
                    }
                }else{
                        cta_STOPS.setGreen(tokens[1]);
                        cta_STOPS.setRed(tokens[2]);
                        cta_STOPS.setBlue(tokens[3]);
                        cta_STOPS.setYellow(tokens[4]);
                        cta_STOPS.setPink(tokens[5]);
                        cta_STOPS.setOrange(tokens[6]);
                        cta_STOPS.setBrown(tokens[7]);
                        cta_STOPS.setPurple(tokens[8]);

                        sqlite.commit(cta_STOPS, "L_STOPS");
                        row++;

                    }


                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
                sqlite.close();


        }
        return ordered_station_list;
    }



    public void Create_TrainInfo_table(BufferedReader reader, Context context) throws IOException {
        CTA_DataBase sqlite = new CTA_DataBase(context);
        String line;
        int row=0;
        while (true) {
            try{
            if ((line = reader.readLine()) != null) {
                if (row == 0){
                        row++;
                        continue;
                    }
            }


            String[] tokens = line.split(",");


//            String[] tokens = line.split(","); //(line.replaceAll("\"","").replaceAll(",,",",")).split(",");
            CTA_Stops cta_stops = new CTA_Stops();
            cta_stops.setSTOP_ID(tokens[0]);
            cta_stops.setDIRECTION_ID(tokens[1]);
            cta_stops.setSTOP_NAME(tokens[2]);




                String station_name = tokens[3];

                if (!station_name.matches("\\A\\p{ASCII}*\\z")){
                    cta_stops.setSTATION_NAME("O'Hare");

                }
            cta_stops.setSTATION_NAME(station_name);
            cta_stops.setMAP_ID(tokens[4]);
            if (Boolean.parseBoolean(tokens[5])){
                cta_stops.setADA("1");

            }else{
                cta_stops.setADA("0");
            }

            if (Boolean.parseBoolean(tokens[6])){
                cta_stops.setRED("1");

            }else{
                cta_stops.setRED("0");
            }
            if (Boolean.parseBoolean(tokens[7])){
                cta_stops.setBLUE("1");
            }else{
                cta_stops.setBLUE("0");
            }
            if (Boolean.parseBoolean(tokens[8])){
                cta_stops.setG("1");

            }else{
                cta_stops.setG("0");


            }
            if (Boolean.parseBoolean(tokens[9])){
                cta_stops.setBRN("1");
            }else{
                cta_stops.setBRN("0");

            }
            if (Boolean.parseBoolean(tokens[10])){
                cta_stops.setP("1");

            }else{
                cta_stops.setP("0");


            }
            if (Boolean.parseBoolean(tokens[11])){
                cta_stops.setPEXP("1");

            }else{
                cta_stops.setPEXP("0");

            }
            if (Boolean.parseBoolean(tokens[12])){
                cta_stops.setY("1");


            }else{
                cta_stops.setY("0");


            }
            if (Boolean.parseBoolean(tokens[13])){
                cta_stops.setPINK("1");

            }else{
                cta_stops.setPINK("0");


            }
            if (Boolean.parseBoolean(tokens[14])){
                cta_stops.setORG("1");

            }else{
                cta_stops.setORG("0");


            }

            String lat = tokens[15].replaceAll("[^0-9.\\-]", "");
            String lon = tokens[16].replaceAll("[^0-9.\\-]", "");


            if (lat.equals("") || lon.equals("") ){
                Log.e("STATION", tokens[15]+" ,"+tokens[16]);
            }

            cta_stops.setLAT(Double.parseDouble(lat));
            cta_stops.setLON(Double.parseDouble(lon));

            sqlite.commit(cta_stops, "cta_stops");

            row++;


        }
            catch (IOException e) {
                e.printStackTrace();
            }
            sqlite.close();
            if (row == 300){
                break;
            }

        }





    }
    public static boolean isPureAscii(String v) {
        return Charset.forName("US-ASCII").newEncoder().canEncode(v);
        // or "ISO-8859-1" for ISO Latin 1
        // or StandardCharsets.US_ASCII with JDK1.7+
    }

//    public String[] retrieve_station_coordinates(Database2 sqlite, String station_id) {
//        try {
//            ArrayList<String> record = sqlite.get_table_record("cta_stops", "WHERE station_id = '"+station_id+"'");
//            return new String[]{record.get(10), record.get(11)};
//
//        }catch (Exception e){
//            Log.e("SQLITE ERROR", "COULD NOT FIND STATION IN DATABASE!");
//        }
//        return null;
//    }





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public BufferedReader setup_file_reader(Context context, int file){
        try {
            InputStream CSVfile = context.getResources().openRawResource(file);
            return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

        }catch (Exception e){
            Log.e("ERROR IN FILE", "CAN NOT OPEN FILE");
        }
        return null;

    }





    public Train get_train_info( String each_train) {
        /*
            <eta>
        <staId>41450</staId>
        <stpId>30279</stpId>
        <staNm>Chicago</staNm>
        <stpDe>Service toward Howard</stpDe>
        <rn>905</rn>
        <rt>Red</rt>
        <destSt>30173</destSt>
        <destNm>Howard</destNm>
        <trDr>1</trDr>
        <prdt>20210409 12:23:02</prdt>
        <arrT>20210409 12:25:02</arrT>
        <isApp>0</isApp>
        <isSch>0</isSch>
        <isDly>0</isDly>
        <isFlt>0</isFlt>
        <flags />
        <lat>41.88481</lat>
        <lon>-87.62781</lon>
        <heading>358</heading>
    </eta>
 */
        Train train = new Train();
        try {

            String staId = get_xml_tag_value(each_train, "staId");
            String stpId = get_xml_tag_value(each_train, "stpId");
            String staNm = get_xml_tag_value(each_train, "staNm");
            String stpDe = get_xml_tag_value(each_train, "stpDe");
            String rn = get_xml_tag_value(each_train, "rn");
            String rt = get_xml_tag_value(each_train, "rt");
            String destSt = get_xml_tag_value(each_train, "destSt");
            String destNm= get_xml_tag_value(each_train, "destNm");
            String trDr = get_xml_tag_value(each_train, "trDr");
            String prdt= get_xml_tag_value(each_train, "prdt");
            String arrT = get_xml_tag_value(each_train, "arrT");
            String isApp = get_xml_tag_value(each_train, "isApp");
            String isDly= get_xml_tag_value(each_train, "isDly");
            String isFlt = get_xml_tag_value(each_train, "isFlt");
            String lat = get_xml_tag_value(each_train, "lat");
            String lon= get_xml_tag_value(each_train, "lon");
            String heading= get_xml_tag_value(each_train, "heading");


            train.setRn(rn);
            train.setViewIcon(false);
            train.setSelected(false);
            train.setDestNm(destNm);
            train.setTrDr(trDr);
            train.setStaId(staId);
            train.setStpId(stpId);
            train.setStaNm(staNm);
            train.setStpDe(stpDe);
            train.setRt(rt);

            String date1 = getDate(prdt.split(" ")[0]);
            train.setPrdt(date1+" "+prdt.split(" ")[1]);
            String date2 = getDate(arrT.split(" ")[0]);
            train.setArrT(date2 +" " +arrT.split(" ")[1]);
            train.setIsApp(isApp);
            train.setHeading(heading);
            train.setIsDly(isDly);
            train.setDestSt(destSt);
            train.setIsFlt(isFlt);
            train.setLat(Double.parseDouble(lat.trim()));
            train.setLon(Double.parseDouble(lon.trim()));


        }catch (Exception e){
            return null;
        }

        return train;
    }

    private String getDate(String prdt){
        String year = prdt.substring(0, 4);
        String month = prdt.substring(4, 6);
        String day = prdt.substring(6, 8);
        return  year+ "-"+month+"-"+day;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void plot_marker(Context context, Message message, GoogleMap mMap, Train train, HashMap<String, String > target_station){
        MapMarker mapMarker = new MapMarker(mMap, context, message);
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context, message,"Test");
        mMap.setInfoWindowAdapter(adapter);
        if (train == null){
            mapMarker.addMarker(
                    Double.parseDouble(Objects.requireNonNull(target_station.get("LAT"))),
                    Double.parseDouble(Objects.requireNonNull(target_station.get("LON"))),
                    "Station# "+target_station.get("MAP_ID"),
                    "target",
                    1f,
                    false,
                    true,
                    false,
                    target_station.get("STATION_NAME"), false);
        }else {
            if (train.getSelected()) {
                if (!train.getViewIcon()) {
                    mapMarker.addMarker(
                            train.getLat(),
                            train.getLon(),
                            "Train# " + train.getRn(),
                            train.getRt().toLowerCase(),
                            1f,
                            true,
                            false,
                            false,
                            train.getRn(),
                            false).showInfoWindow();
                }else{
                    mapMarker.addMarker(
                            train.getLat(),
                            train.getLon(),
                            "Train# " + train.getRn(),
                            train.getStatus().toLowerCase(),
                            1f,
                            true,
                            false,
                            false,
                            train.getRn(),
                            true).showInfoWindow();

                }
            }else {
                if (!train.getViewIcon()) {
                    mapMarker.addMarker(
                            train.getLat(),
                            train.getLon(),
                            "Train# " + train.getRn(),
                            train.getRt().toLowerCase(),
                            1f,
                            true,
                            false,
                            false,
                            train.getRn(), false);

                }
            }
        }
    }


    public TrainStops get_remaining_train_stop_info( String each_train) {
        /*
            <eta>
        <staId>41450</staId>
        <stpId>30279</stpId>
        <staNm>Chicago</staNm>
        <stpDe>Service toward Howard</stpDe>
        <rn>905</rn>
        <rt>Red</rt>
        <destSt>30173</destSt>
        <destNm>Howard</destNm>
        <trDr>1</trDr>
        <prdt>20210409 12:23:02</prdt>
        <arrT>20210409 12:25:02</arrT>
        <isApp>0</isApp>
        <isSch>0</isSch>
        <isDly>0</isDly>
        <isFlt>0</isFlt>
        <flags />
        <lat>41.88481</lat>
        <lon>-87.62781</lon>
        <heading>358</heading>
    </eta>
 */
        TrainStops train = new TrainStops();
        try {

            String staId = get_xml_tag_value(each_train, "staId");
            String stpId = get_xml_tag_value(each_train, "stpId");
            String staNm = get_xml_tag_value(each_train, "staNm");
            String stpDe = get_xml_tag_value(each_train, "stpDe");
            String rn = get_xml_tag_value(each_train, "rn");
            String rt = get_xml_tag_value(each_train, "rt");
            String destSt = get_xml_tag_value(each_train, "destSt");
            String destNm= get_xml_tag_value(each_train, "destNm");
            String trDr = get_xml_tag_value(each_train, "trDr");
            String prdt= get_xml_tag_value(each_train, "prdt");
            String arrT = get_xml_tag_value(each_train, "arrT");
            String isApp = get_xml_tag_value(each_train, "isApp");
            String isDly= get_xml_tag_value(each_train, "isDly");
            String isFlt = get_xml_tag_value(each_train, "isFlt");


            train.setRn(rn);
            train.setViewIcon(false);
            train.setSelected(false);
            train.setDestNm(destNm);
            train.setTrDr(trDr);
            train.setStaId(staId);
            train.setStpId(stpId);
            train.setStaNm(staNm);
            train.setStpDe(stpDe);
            train.setRt(rt);


            String date1 = getDate(prdt.split(" ")[0]);
            train.setPrdt(date1+" "+prdt.split(" ")[1]);
            String date2 = getDate(arrT.split(" ")[0]);
            train.setArrT(date2 +" " +arrT.split(" ")[1]);

            train.setIsApp(isApp);
            train.setIsDly(isDly);
            train.setDestSt(destSt);
            train.setIsFlt(isFlt);


        }catch (Exception e){return null;}

        return train;
    }
    public int getSpinnerPosition(String train_line) {
        HashMap<String, Integer> positions = new HashMap<>();
        positions.put("blue", 0);
        positions.put("red", 1);
        positions.put("green", 2);
        positions.put("yellow", 3);
        positions.put("pink", 4);
        positions.put("orange", 5);
        positions.put("purple", 6);
        positions.put("brown", 7);
        return positions.get(train_line.toLowerCase().trim());
    }

    private String get_xml_tag_value(String raw_xml, String startTag){
        String end_tag = "</"+startTag+">";

        return StringUtils.substringBetween(raw_xml,"<"+startTag+">", end_tag).trim();
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


    public Integer getDayOfWeekNum(String dayOfWeek){
        HashMap<String, Integer> week = new HashMap<>();
        week.put("mon", 2);
        week.put("tues", 3);
        week.put("wen", 4);
        week.put("thu", 5);
        week.put("fri", 6);
        week.put("sat", 7);
        week.put("sun", 1);
        return week.get(dayOfWeek.toLowerCase());



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

        return TrainLineKeyCodes.get(station_type.toLowerCase());



    }


    public Integer GetBackgroundColor(String color, Context context) {
        HashMap<String, Integer> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red", context.getResources().getColor(R.color.red_100));
        TrainLineKeyCodes.put("blue", context.getResources().getColor(R.color.blue_100));
        TrainLineKeyCodes.put("brown", context.getResources().getColor(R.color.brown));
        TrainLineKeyCodes.put("green", context.getResources().getColor(R.color.green_100));
        TrainLineKeyCodes.put("orange", context.getResources().getColor(R.color.orange));
        TrainLineKeyCodes.put("pink", context.getResources().getColor(R.color.pink));
        TrainLineKeyCodes.put("purple",context.getResources().getColor(R.color.purple));
        TrainLineKeyCodes.put("yellow", context.getResources().getColor(R.color.yellow));

        return TrainLineKeyCodes.get(color.toLowerCase());
    }


        public void ZoomIn(GoogleMap mMap, Float zoomLevel, Double lat, Double lon){
        LatLng target = new LatLng(lat, lon);
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
