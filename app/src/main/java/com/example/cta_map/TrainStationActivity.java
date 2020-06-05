package com.example.cta_map;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TrainStationActivity  extends AppCompatActivity {
    Bundle bb; // Retrieve data from main screen

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_station_activity);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        final ListView list = (ListView) findViewById(R.id.train_stops);
        ArrayList<String> arrayList = new ArrayList<>();
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
    ArrayList<String> train_stops = chicago_transits.retrieve_line_stations(chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops), target_station_type, false);
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
