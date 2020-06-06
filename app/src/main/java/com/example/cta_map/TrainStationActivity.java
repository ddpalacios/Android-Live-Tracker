package com.example.cta_map;
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

import java.io.BufferedReader;
import java.util.ArrayList;

public class TrainStationActivity  extends AppCompatActivity {
    Bundle bb; // Retrieve data from main screen

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_station_activity);
        final Chicago_Transits chicago_transits = new Chicago_Transits();
        final ListView list = (ListView) findViewById(R.id.train_stops);
        ArrayList<String> arrayList = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
    Intent intent = this.getIntent();
    String train_direction = null;
    String train_direction_name = null;
    String target_station_type = null;
        if (intent != null) {
        target_station_type = intent.getStringExtra("target_station_type");
        train_direction_name = intent.getStringExtra("train_direction_name");
        train_direction = intent.getStringExtra("train_direction");
    }


    ArrayList<String> train_stops = db.getValues("line_stops_table", target_station_type.toLowerCase());
    if (target_station_type.equals("purple")){
        train_stops.subList(9,18).clear();
    }

    for (String each_stop: train_stops){
        arrayList.add(each_stop);
        adapter.notifyDataSetChanged();

    }
    final String finalTarget_station_type = target_station_type;
    final String finalTrain_direction = train_direction;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String target_station = String.valueOf(list.getItemAtPosition(position));
            SharedPreferences CONNECTION = getSharedPreferences("CONNECT", MODE_PRIVATE);
            boolean urlConnection = CONNECTION.getBoolean("connection",true);
            Toast.makeText(getApplicationContext(), urlConnection+"", Toast.LENGTH_SHORT).show();

            if (!urlConnection){
                Intent intent = new Intent(TrainStationActivity.this,mainactivity.class);
                SharedPreferences USER_RECORD = getSharedPreferences("User_Record", MODE_PRIVATE);
                Integer profileId = USER_RECORD.getInt("ProfileID",0);
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                Double[] target_station_coordinates = sqlite.FindStationValues(target_station, finalTarget_station_type);
                Log.e("coord", target_station_coordinates[0]+"");




                UserStation userStation = new UserStation(target_station, finalTarget_station_type);
                userStation.setTrain_lat(target_station_coordinates[0]);
                userStation.setTrain_lon(target_station_coordinates[1]);
                userStation.setDirection(Integer.parseInt(finalTrain_direction));

                Log.e("PROF ID", profileId+"");
                userStation.setID(profileId);
                sqlite.addUserStation(userStation);


                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }

            else {
                Intent intent = new Intent(TrainStationActivity.this, TrainTrackingActivity.class);
                intent.putExtra("target_station_type", finalTarget_station_type);
                intent.putExtra("target_station_name", target_station);
                intent.putExtra("train_direction", finalTrain_direction);
                startActivity(intent);
            }
        }
    });

}
}
