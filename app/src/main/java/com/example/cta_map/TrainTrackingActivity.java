package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TrainTrackingActivity extends AppCompatActivity implements TrainDirection{
    final boolean[] connect = {true};
    List<String> ignored_stations;
    ArrayList<Integer> train_etas = new ArrayList<>();
    ArrayList<HashMap> chosen_trains = new ArrayList<>();
    Bundle bb; // Retrieve data from main screen


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.train_tracking_activity);
        final Chicago_Transits chicago_transits = new Chicago_Transits();

        super.onCreate(savedInstanceState);
        HashMap <String, String> StationTypeKey = chicago_transits.TrainLineKeys(); // Train line key codes
        bb=getIntent().getExtras();
        assert bb != null;
        final String target_station_type = bb.getString("target_station_type");
        final String target_station_name = bb.getString("target_station_name");
        final String[] specified_train_direction = {bb.getString("train_direction")};
        ListView list = (ListView) findViewById(R.id.train_layout_arrival_times);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);

        final ArrayList<String> stops = chicago_transits.retrieve_line_stations(chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops), target_station_type);
        final String url = String.format("https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=%s",  StationTypeKey.get(target_station_type.toLowerCase()));
        Log.e("url", url);
        /*

          Everything is being ran within its own thread.
         This allows us to run our continuous web extraction
         while also performing other user interactions

          */
        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                while (connect[0]){

                    try {
                        Document content = Jsoup.connect(url).get(); // JSOUP to webscrape XML
                        final String[] train_list = content.select("train").outerHtml().split("</train>"); //retrieve our entire XML format, each element == 1 <train></train>
                                for (String each_train : train_list) {
//                                    // prepare each train as a map
                                    HashMap<String, String> train_info = chicago_transits.get_train_info(chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations), each_train,target_station_name ,target_station_type);
                                    int start = 0;
                                    int end =0;
                                    if (Objects.equals(train_info.get("train_direction"), specified_train_direction[0])) {
                                        if (specified_train_direction[0].equals("1")){
                                            end = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", ""));
//
                                        }else if (specified_train_direction[0].equals("5")){
                                            start = stops.indexOf(Objects.requireNonNull(train_info.get("target_station")).replaceAll("[^a-zA-Z0-9]", "")) + 1;
                                            end = stops.size();

                                        }
                                        setup_train_direction(train_info, stops, start, end, Integer.parseInt(specified_train_direction[0]), getApplicationContext());
                                    }
                                }
                                Log.d("Update", "DONE.");

                        train_etas.clear();
                        chosen_trains.clear();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }).start();





    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapRelativeListView mapRelativeListView = new MapRelativeListView(context,findViewById(R.id.train_layout_arrival_times));
        BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(),R.raw.train_stations);
        String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(reader, current_train_info.get("target_station"), current_train_info.get("station_type"));
        Time times = new Time();

        ignored_stations = stops.subList(start, end);
        String next_stop = Objects.requireNonNull(current_train_info.get("next_stop")).replaceAll("[^a-zA-Z0-9]", "");

        if (ignored_stations.contains(next_stop)) {

        }else {
            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("train_lon"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lat"))), Double.parseDouble(Objects.requireNonNull(current_train_info.get("target_station_lon"))));
            int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
            train_etas.add(current_train_eta);
            Collections.sort(train_etas);
            chosen_trains.add(current_train_info);
            current_train_info.put(String.valueOf(current_train_eta), next_stop);
        }
        mapRelativeListView.add_to_list_view(train_etas, current_train_info, chosen_trains, connect);
    }


}
