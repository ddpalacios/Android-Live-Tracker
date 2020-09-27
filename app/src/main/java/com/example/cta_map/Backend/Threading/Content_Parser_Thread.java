package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class Content_Parser_Thread implements Runnable
{
    private final Message msg;
    private String target_type;
    private String target_name;
    private String target_dir;
    private Context context;
    private String target_station_id;

    //    HashMap<String, String> record;
//    android.os.Handler handler;
//    boolean willCommunicate;
//    Context context;
//    ArrayList<String> stops;
    String TAG = Thread.currentThread().getName();
//    public Content_Parser_Thread(Message msg,Context context, HashMap<String, String> record,android.os.Handler handler , ArrayList<String> stops, boolean willCommunicate){
//        this.msg = msg;
//        this.context = context;
//        this.record = record;
//        this.handler = handler;
//        this.stops = stops;
//        this.willCommunicate = willCommunicate;
//    }
    public Content_Parser_Thread(Context context, Message msg, String target_type, String target_dir, String target_name, String target_station_id){
        this.msg = msg;
        this.context = context;
        this.target_type = target_type;
        this.target_dir = target_dir;
        this.target_station_id = target_station_id;
        this.target_name = target_name;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
        ArrayList<Object> found_target = cta_dataBase.excecuteQuery("*","cta_stops", "MAP_ID = '"+this.target_station_id.trim()+"'");
        HashMap<String, String> target_station_record = (HashMap<String,String>) found_target.get(0);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Time time = new Time();
        Chicago_Transits chicago_transits = new Chicago_Transits();

        try{




//            String target_station = Objects.requireNonNull(this.record.get("station_name")).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
//            final ArrayList<String> modified_stops = new ArrayList<>();
//            HashMap<String, ArrayList<HashMap>> parsed_train_data = new HashMap();
//            List<String> modified_valid_stations;
//            for (String each_stop : this.stops) { modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }

            ArrayList<String> all_target_stops = getStationStopsByType(this.target_type, this.target_name, this.target_dir);
            while (this.msg.IsSending()){
                synchronized (this.msg){
                    ArrayList<AllTrainsTable> chosen_trains = new ArrayList<>();

                    ArrayList<IncomingTrains> all_incoming_trains = this.msg.getIncoming_trains();
                    if (all_incoming_trains.size() == 0){ throw new EmptyStackException(); }
                    for (IncomingTrains current_incoming_train: all_incoming_trains){
                        if(all_target_stops.contains(current_incoming_train.getNextStpId().trim()) && current_incoming_train.getTrDr().trim().equals(target_dir.trim())) {
                            AllTrainsTable new_train_record = new AllTrainsTable();
                            Double current_train_lat = current_incoming_train.getLat();
                            Double current_train_lon = current_incoming_train.getLon();
                            Double target_station_lat = Double.parseDouble(target_station_record.get("location").split(",")[0].trim());
                            Double target_station_lon = Double.parseDouble(target_station_record.get("location").split(",")[1].trim());
                            ArrayList<Object> found_nextStpStation = cta_dataBase.excecuteQuery("*", "cta_stops", "MAP_ID = '" + current_incoming_train.getNextStpId() + "'");
                            HashMap<String, String> next_stop_station_record = (HashMap<String, String>) found_nextStpStation.get(0);
                            Double next_stop_station_lat = Double.parseDouble(next_stop_station_record.get("location").split(",")[0].trim());
                            Double next_stop_station_lon = Double.parseDouble(next_stop_station_record .get("location").split(",")[1].trim());

                            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(current_train_lat, current_train_lon,
                                    target_station_lat, target_station_lon);

                            int current_eta_from_target_station = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                            Double current_train_distance_from_next_stop_station = chicago_transits.calculate_coordinate_distance(current_train_lat, current_train_lon,
                                                                                                                                  next_stop_station_lat,next_stop_station_lon);
                            int current_eta_from_next_stop_station = time.get_estimated_time_arrival(25, current_train_distance_from_next_stop_station);
                            new_train_record.setTrain_id(current_incoming_train.getRn());
                            new_train_record.setNotified(false);
                            new_train_record.setPred_arrival_time(current_incoming_train.getPrdt());
                            new_train_record.setNext_stop(current_incoming_train.getNextStaNm());
                            new_train_record.setNext_stop_eta(current_eta_from_next_stop_station+"");
                            new_train_record.setNext_stop_distance(current_train_distance_from_next_stop_station);
                            if (current_incoming_train.getIsDly().equals("1")) {
                                new_train_record.setDelayed(true);
                            }else{
                                new_train_record.setDelayed(false);
                            }
                            if (current_incoming_train.getIsApp().equals("1")) {
                                new_train_record.setApproaching(true);
                            }else{
                                new_train_record.setApproaching(false);
                            }
                            new_train_record.setDistance_to_target(current_train_distance_from_target_station);
                            new_train_record.setTo_target_eta(current_eta_from_target_station+"");
                            new_train_record.setTracking_type(this.target_type);
                            new_train_record.setTrain_lat(current_train_lat);
                            new_train_record.setTrain_lon(current_train_lon);
                            new_train_record.setTarget_id(this.target_station_id);
                            new_train_record.setTrain_dir(current_incoming_train.getTrDr());

                            chosen_trains.add(new_train_record);

                        }








                    }




//
//                    HashMap<Integer, String> train_etas = new HashMap<>();
//                    ArrayList<HashMap> chosen_trains = new ArrayList<>();
//                    ArrayList<HashMap> ignored_trains = new ArrayList<>();
//
//
//                    if (Objects.equals(this.record.get("station_dir"), "1")) {
//                        modified_valid_stations = modified_stops.subList(modified_stops.indexOf(target_station), modified_stops.size());
//                    } else {
//                        modified_valid_stations = modified_stops.subList(0, modified_stops.indexOf(target_station) + 1);
//                    }
//                    for (String raw_content: this.msg.getRawTrainContent()){
//                        HashMap<String, String> current_train_info = chicago_transits.get_train_info(raw_content, Objects.requireNonNull(this.record.get("station_type")).replaceAll(" ", ""));
//                        String modified_next_stop = Objects.requireNonNull(current_train_info.get("next_stop")).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
//                        if (Objects.equals(current_train_info.get("train_direction"), this.record.get("station_dir")) && modified_valid_stations.contains(modified_next_stop)) {
//
//                            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
//                                    Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))),
//                                    Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))),
//                                    Double.parseDouble(Objects.requireNonNull(this.record.get("station_lat"))),
//                                    Double.parseDouble(Objects.requireNonNull(this.record.get("station_lon"))));
//
//
//                            try {
//                                String query1 = "SELECT station_id FROM cta_stops WHERE station_name = '" + current_train_info.get("next_stop").trim()+ "'" + " AND " + current_train_info.get("station_type") + " = 'true'";
//                                String station_id = sqlite.getValue(query1);
//                                String[] next_stop_station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//
//                                Double current_train_distance_from_next_station = chicago_transits.calculate_coordinate_distance(
//                                        Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))),
//                                        Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))),
//                                        Double.parseDouble(Objects.requireNonNull(next_stop_station_coord[0])),
//                                        Double.parseDouble(Objects.requireNonNull(next_stop_station_coord[1])));
//
//                                int current_train_next_stop_eta = time.get_estimated_time_arrival(25, current_train_distance_from_next_station);
//                                current_train_info.put("next_stop_distance", current_train_distance_from_next_station+"");
//                                current_train_info.put("next_stop_eta", current_train_next_stop_eta+"");
//                                int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
//                                train_etas.put(current_train_eta, current_train_info.get("train_id"));
//                                current_train_info.put("target_station", this.record.get("station_name"));
//                                current_train_info.put("train_eta", current_train_eta+"");
//                                current_train_info.put("train_distance", current_train_distance_from_target_station+"");
//                                chosen_trains.add(current_train_info);
//                                Log.e(TAG, current_train_info+"");
//
//                                sqlite.AddTrain(current_train_info);
//
//                            }catch (Exception e){e.printStackTrace();}
//
//
//                        }
//
//                        if (!modified_valid_stations.contains(modified_next_stop) && Objects.equals(current_train_info.get("train_direction"), this.record.get("station_dir"))) {
//                            ignored_trains.add(current_train_info);
//                        }
//                    }
//
//                    TreeMap<Integer, String> map = new TreeMap(train_etas);
//                    this.msg.setTrainMap(map);



//                    if (this.willCommunicate) {
//                        Log.e(Thread.currentThread().getName(), "Chosen Trains: " + chosen_trains.size());
//                        Log.e(Thread.currentThread().getName(), "Ignored Trains: " + ignored_trains.size());
//                    }
//
//                    parsed_train_data.put("chosen_trains", chosen_trains);
//                    parsed_train_data.put("ignored_trains", ignored_trains);
//                    this.msg.setParsedTrainData(parsed_train_data);
//
//                    this.msg.notify();
//                    try {
//                        this.msg.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<String> getStationStopsByType(String target_type, String target_name, String target_dir){
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
        ArrayList<String> all_target_stops = new ArrayList<>();
        ArrayList<Object> line_stops_table = cta_dataBase.excecuteQuery(this.target_type.toLowerCase().trim(), "line_stops_table", null);
        for (int i=0; i<line_stops_table.size(); i++){
            HashMap<String, String> cur_stops_by_type = (HashMap<String, String>) line_stops_table.get(i);
            String target_stop = Objects.requireNonNull(cur_stops_by_type.get(target_type)).trim();
            if (target_stop.equals("null")){
                break;
            }


            if (target_stop.equals(target_name) && target_dir.equals("1")){
                all_target_stops.clear();
                if (target_stop.equals("O'Hare")){
                    all_target_stops.add("30171");
                    continue;
                }else if (target_stop.equals("Harlem (O'Hare Branch)")){
                    all_target_stops.add("30145");
                    continue;
                }else if (target_stop.equals("Western (O'Hare Branch)")){
                    all_target_stops.add("30130");
                    continue;
                }
                try {
                    ArrayList<Object> cta_stops_result = cta_dataBase.excecuteQuery("MAP_ID", "cta_stops", "station_name = '" + target_stop + "' AND " + target_type + " = 'TRUE'");
                    HashMap<String, String> found_station_name = (HashMap<String, String>) cta_stops_result.get(0);
                    all_target_stops.add(found_station_name.get("MAP_ID"));
                    continue;
                }catch (Exception e){e.printStackTrace();}


            }else if (target_stop.equals(target_name) && target_dir.equals("5")){
                if (target_stop.equals("O'Hare")){
                    all_target_stops.add("30171");
                    break;
                }else if (target_stop.equals("Harlem (O'Hare Branch)")){
                    all_target_stops.add("30145");
                    break;
                }else if (target_stop.equals("Western (O'Hare Branch)")){
                    all_target_stops.add("30130");
                    break;
                }
                try {
                    ArrayList<Object> cta_stops_result = cta_dataBase.excecuteQuery("MAP_ID", "cta_stops", "station_name = '" + target_stop + "' AND " + target_type + " = 'TRUE'");
                    HashMap<String, String> found_station_name = (HashMap<String, String>) cta_stops_result.get(0);
                    all_target_stops.add(found_station_name.get("MAP_ID"));
                    break;
                }catch (Exception e){e.printStackTrace();}
            }

            if (target_stop.equals("O'Hare")){
                all_target_stops.add("30171");
                continue;
            }else if (target_stop.equals("Harlem (O'Hare Branch)")){
                all_target_stops.add("30145");
                continue;
            }else if (target_stop.equals("Western (O'Hare Branch)")){
                all_target_stops.add("30130");
                continue;
            }
            try {
                ArrayList<Object> cta_stops_result = cta_dataBase.excecuteQuery("MAP_ID", "cta_stops", "station_name = '" + target_stop + "' AND " + target_type + " = 'TRUE'");
                HashMap<String, String> found_station_name = (HashMap<String, String>) cta_stops_result.get(0);
                all_target_stops.add(found_station_name.get("MAP_ID"));
            }catch (Exception e){e.printStackTrace();}
        }
     return all_target_stops;
    }



}