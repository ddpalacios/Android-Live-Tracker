package com.example.cta_map;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;


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
                    String stationCanidate = tokens[0].replace(" ", "_").toLowerCase();
                    HashMap<String, String> train_lines = new HashMap<>();
                    HashMap<String, String> train_types = GetStation(tokens, train_lines); //HashMap of All train lines

                    if (stationCanidate.equals(station_name) && Boolean.parseBoolean(train_types.get(station_type))) {
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

    private HashMap<String, String> GetStation(String[] tokens, HashMap<String, String> train_lines) {

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


    public HashMap<String, String> get_train_info(String each_train) {
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

        return train_info;

    }


    private String get_xml_tag_value(String raw_xml, String startTag, String endTag){

        return StringUtils.substringBetween(raw_xml, startTag, endTag);
    }

}
