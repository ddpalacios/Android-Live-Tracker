package com.example.cta_map;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TrainTrackingActivity extends AppCompatActivity{
    final boolean[] connect = {true};
    List<String> ignored_stations;
    ArrayList<Integer> train_etas = new ArrayList<>();
    ArrayList<HashMap> chosen_trains = new ArrayList<>();
   ExampleHandler handler = new ExampleHandler();
    Bundle bb; // Retrieve data from main screen
    Boolean[] green = new Boolean[] {false};
    Boolean[] yellow = new Boolean[] {false};
    Boolean[] pink = new Boolean[] {false};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
            bb= getIntent().getExtras();
             DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
            String target_station_type = bb.getString("station_type");
            String specified_train_direction = bb.getString("station_dir");
            String target_station_name = bb.getString("station_name");
            Double target_station_lat = bb.getDouble("station_lat");
            Double target_station_lon = bb.getDouble("station_lon");



        Message message = new Message();
        Thread t1 = new Thread(new Thread1(message, bb), "API_CALL_Thread");
        t1.start();


        Thread t2 = new Thread(new Thread2(message, bb, sqlite), "Content Parser");
        t2.start();

//
//        Thread t3 = new Thread(new Thread3(message, t3), "Displayer");
//        t3.start();














    }


    private Button initiate_button(int widget) {
        Button button = findViewById(widget);
        return button;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context) {
        SharedPreferences TRAIN_RECORD = getSharedPreferences("Train_Record", MODE_PRIVATE);

        final String[] target_station_coordinates = new String[]{String.valueOf(TRAIN_RECORD.getFloat("station_lat", 0)), String.valueOf(TRAIN_RECORD.getFloat("station_lon", 0))};
        Chicago_Transits chicago_transits = new Chicago_Transits();
        MapRelativeListView mapRelativeListView = new MapRelativeListView(context, findViewById(R.id.train_layout_arrival_times));
        Time times = new Time();
        ignored_stations = stops.subList(start, end);
        String next_stop = current_train_info.get("next_stop").replaceAll("[^a-zA-Z0-9]", "");

        if (!ignored_stations.contains(next_stop)) {


            Double current_train_distance_from_target_station = chicago_transits.calculate_coordinate_distance(Double.parseDouble(current_train_info.get("train_lat")),
                    Double.parseDouble(current_train_info.get("train_lon")),
                    Double.parseDouble(target_station_coordinates[0]),
                    Double.parseDouble(target_station_coordinates[1]));




            int current_train_eta = times.get_estimated_time_arrival(25, current_train_distance_from_target_station);
            train_etas.add(current_train_eta);
            Collections.sort(train_etas);
            chosen_trains.add(current_train_info);
            current_train_info.put(String.valueOf(current_train_eta), next_stop);
        }
        mapRelativeListView.add_to_list_view(train_etas, TRAIN_RECORD, current_train_info,chosen_trains, connect, current_train_info.get("train_direction"));
    }
        }

