package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;
import com.example.cta_map.DataBase.UserStation;

import java.util.ArrayList;

public class TrainStationActivity  extends AppCompatActivity {
    Bundle bb; // Retrieve data from main screen

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_station_activity);
        final ListView list = (ListView) findViewById(R.id.train_stops);
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        bb = getIntent().getExtras();
        final String station_type = bb.getString("target_station_type");
        final String station_direction = bb.getString("train_direction");
        final String main_station = bb.getString("main_station");

        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);

        ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", station_type);
        if (station_type.equals("purple")){
            line_stops.subList(9,18).clear();
        }
        for (String each_stop: line_stops){
            arrayList.add(each_stop);
            adapter.notifyDataSetChanged();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                String target_station = String.valueOf(list.getItemAtPosition(position));
                Chicago_Transits chicago_transits = new Chicago_Transits();
                SharedPreferences url_connection = getSharedPreferences("CONNECT", MODE_PRIVATE);
                boolean urlConnection = url_connection.getBoolean("connection", true);


                Integer ProfileID = url_connection.getInt("ProfileID", -1);

                if (ProfileID == -1){ return; }

                if (!urlConnection) {

                    Intent intent = new Intent(TrainStationActivity.this, mainactivity.class);


                    UserStation userStation = new UserStation(target_station, station_type);
                    String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(sqlite,target_station, station_type );

                    Log.e("connt", target_station_coordinates[0]+"");

                    userStation.setTrain_lat(Double.parseDouble(target_station_coordinates[0]));
                    userStation.setTrain_lon(Double.parseDouble(target_station_coordinates[1]));
                    userStation.setDirection(Integer.parseInt(station_direction));
                    userStation.set_main(main_station);
                    userStation.setID(ProfileID);
                    final ArrayList<String> user_record = sqlite.get_table_record("User_info", "WHERE profile_id = '"+ProfileID+"'");

                    sqlite.addUserStation(userStation);
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    intent.putExtra("username", user_record.get(3));
                    intent.putExtra("pass", user_record.get(7));

                    startActivity(intent);


                }else{
                    Intent intent = new Intent(TrainStationActivity.this, TrainTrackingActivity.class);
                    String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(sqlite,target_station, station_type );
                    ArrayList<String> tracking_record = new ArrayList<>();
                    tracking_record.add(String.valueOf(ProfileID));
                    tracking_record.add(target_station);
                    tracking_record.add(station_type);
                    tracking_record.add(target_station_coordinates[0]);
                    tracking_record.add(target_station_coordinates[1]);
                    tracking_record.add(station_direction);
                    tracking_record.add(main_station);
                    sqlite.add_train_tracker(tracking_record);
                    sqlite.close();
                    startActivity(intent);







                }


            }
        });














        sqlite.close();
    }

//        Intent intent = this.getIntent();
//        String train_direction = null;
//        String train_direction_name = null;
//        String target_station_type = null;
//            if (intent != null) {
//            target_station_type = intent.getStringExtra("target_station_type");
//            train_direction_name = intent.getStringExtra("train_direction_name");
//            train_direction = intent.getStringExtra("train_direction");
//        }
//
//
//    ArrayList<String> train_stops = db.get_column_values("line_stops_table", target_station_type.toLowerCase());
//        Log.e("stops", train_stops+"");
//    if (target_station_type.equals("purple")){
//        train_stops.subList(9,18).clear();
//    }
//
//    for (String each_stop: train_stops){
//        if (each_stop == null){
//            continue;
//        }
//        arrayList.add(each_stop);
//        adapter.notifyDataSetChanged();
//
//    }
//    final String finalTarget_station_type = target_station_type;
//    final String finalTrain_direction = train_direction;
//        final String finalTrain_direction_name = train_direction_name;
//
//
//
//
//
//
//
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String target_station = String.valueOf(list.getItemAtPosition(position));
//            SharedPreferences CONNECTION = getSharedPreferences("CONNECT", MODE_PRIVATE);
//            boolean urlConnection = CONNECTION.getBoolean("connection", true);
//            Toast.makeText(getApplicationContext(), urlConnection + "", Toast.LENGTH_SHORT).show();
//            DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
//            Double[] target_station_coordinates = sqlite.FindStationValues(target_station, finalTarget_station_type);
//
//            if (!urlConnection) {
//                Intent intent = new Intent(TrainStationActivity.this, mainactivity.class);
//                SharedPreferences USER_RECENT_TRAIN_RECORD = getSharedPreferences("User_Recent_Station_Record", MODE_PRIVATE);
//                Integer profileId = USER_RECENT_TRAIN_RECORD.getInt("ProfileID", 0);
//                if (target_station_coordinates == null) {
//                    Toast.makeText(getApplicationContext(), "No Station Found.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Station Found.", Toast.LENGTH_SHORT).show();
//
//
//
//                    UserStation userStation = new UserStation(target_station, finalTarget_station_type);
//                    userStation.setTrain_lat(target_station_coordinates[0]);
//                    userStation.setTrain_lon(target_station_coordinates[1]);
//                    userStation.setDirection(Integer.parseInt(finalTrain_direction));
//                    intent.putExtra("station_type", finalTarget_station_type);
//
//
//                    Log.e("PROF ID", profileId + "");
//                    userStation.setID(profileId);
//                    sqlite.addUserStation(userStation);
//
//
//                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
//                    startActivity(intent);
//
//                }
//
//
//            } else {
//                Intent intent = new Intent(TrainStationActivity.this, TrainTrackingActivity.class);
//                intent.putExtra("station_type", finalTarget_station_type);
//                intent.putExtra("main_station", finalTrain_direction_name);
//                intent.putExtra("station_dir", finalTrain_direction);
//                intent.putExtra("station_name", target_station);
//                intent.putExtra("station_lat", target_station_coordinates[0]);
//                intent.putExtra("station_lon", target_station_coordinates[1]);
//
//
//
//                startActivity(intent);
//            }
//        }
//    });

}
