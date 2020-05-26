package com.example.cta_map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

@SuppressLint("Registered")
public class ChooseDirectionActivity  extends AppCompatActivity {
    ListView list;
    Bundle bb; // Retrieve data from main screen
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_direction);
        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        final String target_station_type = bb.getString("target_station_type").toLowerCase();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        list = (ListView) findViewById(R.id.direction_choice);
        BufferedReader reader = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.main_stations);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    String stationCanidate = tokens[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    if (stationCanidate.equals(target_station_type)){
                        Log.e("Bounds", tokens[1]+" "+ tokens[2]);
                        arrayList.add("To "+tokens[1]);
                        arrayList.add("To "+tokens[2]);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseDirectionActivity.this,TrainStationActivity.class);
                if (position == 0){
                    intent.putExtra("train_direction", String.valueOf(1));
                    intent.putExtra("train_direction_name", String.valueOf(list.getItemAtPosition(position)));

                }else{
                    intent.putExtra("train_direction", String.valueOf(5));
                    intent.putExtra("train_direction_name", String.valueOf(list.getItemAtPosition(position)));

                }

                intent.putExtra("target_station_type", target_station_type);
                startActivity(intent);


            }
        });



    }
}
