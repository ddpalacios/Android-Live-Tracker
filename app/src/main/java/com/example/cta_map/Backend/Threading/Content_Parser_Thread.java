package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class Content_Parser_Thread implements Runnable
{
    final Message msg;
    HashMap<String, String> record;
    android.os.Handler handler;
    boolean willCommunicate;
    Context context;
    ArrayList<String> stops;
    String TAG = Thread.currentThread().getName();
    public Content_Parser_Thread(Message msg,Context context, HashMap<String, String> record,android.os.Handler handler , ArrayList<String> stops, boolean willCommunicate){
        this.msg = msg;
        this.context = context;
        this.record = record;
        this.handler = handler;
        this.stops = stops;
        this.willCommunicate = willCommunicate;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Time time = new Time();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        try{
            String target_station = Objects.requireNonNull(this.record.get("station_name")).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            final ArrayList<String> modified_stops = new ArrayList<>();
            HashMap<String, ArrayList<HashMap>> parsed_train_data = new HashMap();
            List<String> modified_valid_stations;
            for (String each_stop : this.stops) { modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }
            while (this.msg.IsSending()){
                synchronized (this.msg){
                    if (this.msg.getRawTrainContent() == null){ // no API call
                        this.msg.notify();
                        Log.e(TAG, "NULL object. Waiting. ");
                        continue;
                    }

                    HashMap<Integer, String> train_etas = new HashMap<>();
                    ArrayList<HashMap> chosen_trains = new ArrayList<>();
                    ArrayList<HashMap> ignored_trains = new ArrayList<>();


                    if (Objects.equals(this.record.get("station_dir"), "1")) {
                        modified_valid_stations = modified_stops.subList(modified_stops.indexOf(target_station), modified_stops.size());
                    } else {
                        modified_valid_stations = modified_stops.subList(0, modified_stops.indexOf(target_station) + 1);
                    }
                    for (String raw_content: this.msg.getRawTrainContent()){
                        HashMap<String, String> current_train_info = chicago_transits.get_train_info(raw_content, Objects.requireNonNull(this.record.get("station_type")).replaceAll(" ", ""));
                        String modified_next_stop = Objects.requireNonNull(current_train_info.get("next_stop")).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                        if (Objects.equals(current_train_info.get("train_direction"), this.record.get("station_dir")) && modified_valid_stations.contains(modified_next_stop)) {

                            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(
                                    Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))),
                                    Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))),
                                    Double.parseDouble(Objects.requireNonNull(this.record.get("station_lat"))),
                                    Double.parseDouble(Objects.requireNonNull(this.record.get("station_lon"))));


                            try {
                                Database2 sqlite = new Database2(this.context);
                                String query1 = "SELECT station_id FROM cta_stops WHERE station_name = '" + current_train_info.get("next_stop").trim()+ "'" + " AND " + current_train_info.get("station_type") + " = 'true'";
                                String station_id = sqlite.getValue(query1);
                                String[] next_stop_station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);

                                Double current_train_distance_from_next_station = chicago_transits.calculate_coordinate_distance(
                                        Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))),
                                        Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))),
                                        Double.parseDouble(Objects.requireNonNull(next_stop_station_coord[0])),
                                        Double.parseDouble(Objects.requireNonNull(next_stop_station_coord[1])));

                                int current_train_next_stop_eta = time.get_estimated_time_arrival(25, current_train_distance_from_next_station);
                                current_train_info.put("next_stop_distance", current_train_distance_from_next_station+"");
                                current_train_info.put("next_stop_eta", current_train_next_stop_eta+"");

                            }catch (Exception e){e.printStackTrace();}


                            int current_train_eta = time.get_estimated_time_arrival(25, current_train_distance_from_target_station);
                            train_etas.put(current_train_eta, current_train_info.get("train_id"));
                            current_train_info.put("target_station", this.record.get("station_name"));
                            current_train_info.put("train_eta", current_train_eta+"");
                            current_train_info.put("train_distance", current_train_distance_from_target_station+"");
                            chosen_trains.add(current_train_info);


                        }

                        if (!modified_valid_stations.contains(modified_next_stop) && Objects.equals(current_train_info.get("train_direction"), this.record.get("station_dir"))) {
                            ignored_trains.add(current_train_info);
                        }

                        Log.e(TAG, current_train_info+" ");


                    }

                    TreeMap<Integer, String> map = new TreeMap(train_etas);
                    this.msg.setTrainMap(map);



                    if (this.willCommunicate) {
                        Log.e(Thread.currentThread().getName(), "Chosen Trains: " + chosen_trains.size());
                        Log.e(Thread.currentThread().getName(), "Ignored Trains: " + ignored_trains.size());
                    }

                    parsed_train_data.put("chosen_trains", chosen_trains);
                    parsed_train_data.put("ignored_trains", ignored_trains);
                    this.msg.setParsedTrainData(parsed_train_data);

                    this.msg.notify();
                    try {
                        this.msg.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}