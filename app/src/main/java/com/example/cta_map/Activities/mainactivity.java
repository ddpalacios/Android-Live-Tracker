package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.M)
@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        SharedPreferences USER_RECORD = getSharedPreferences("User_Record", MODE_PRIVATE);
        final Integer profileId = USER_RECORD.getInt("ProfileID", -1);
        ArrayList<HashMap> recordByID = sqlite.GetTableRecordByID(profileId, "train_table");
        if (recordByID == null){
            Toast.makeText(getApplicationContext(), "Error in getting record by ID", Toast.LENGTH_LONG).show();
        }else {
            final ListView list = findViewById(R.id.station_lines);
            final ListView favoriteStations = findViewById(R.id.favorite_lines);
            fill_list(recordByID, list, favoriteStations);
        }

    }

    public void fill_list(ArrayList<HashMap> record, final ListView l1, final ListView l2) {

        final ArrayList<String> favoriteList = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter<String> favoriteadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, favoriteList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);


        l1.setAdapter(adapter);
        final String[] main_menu = new String[]{"Add Favorite Station", "Find Station"};


        l2.setAdapter(favoriteadapter);

        for (String items : main_menu) {
            arrayList.add(items);
            adapter.notifyDataSetChanged();
        }

        if (record.isEmpty()) {
            favoriteList.add("No Favorite Stations");
            favoriteadapter.notifyDataSetChanged();
        } else {
            favoriteList.add(0, "Favorite Stations");
            for (HashMap<String, String> rec : record) {
                String station_name = rec.get("station_name");
                String station_type = rec.get("station_type");
                favoriteList.add(station_name + "-(" + station_type + ")");
                favoriteadapter.notifyDataSetChanged();

            }
        }

        l2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences USER_RECORD = getSharedPreferences("User_Record", MODE_PRIVATE);
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                final Integer profileId = USER_RECORD.getInt("ProfileID", -1);

                String station = (String) l2.getItemAtPosition(position);
                String[] station_details = station.split("-\\(");
                String station_name = station_details[0];
                String station_type = station_details[1].replaceAll("\\)", "");
                String where = "profile_id = ?"
                        + " AND station_name = ?"
                        + " AND station_type = ?";
                favoriteList.clear();
                String[] args = new String[]{String.valueOf(profileId), station_name, station_type};
                Log.e("sss", args+"");
                Log.e("WHERE ", where);
                sqlite.deleteRecord("train_table", where, args);
                ArrayList<HashMap> record = sqlite.GetTableRecordByID(profileId, "train_table");
                if (record == null){
                    Toast.makeText(getApplicationContext(), "Error in getting record by ID", Toast.LENGTH_LONG).show();
                }else {
                    Log.e("trains", record + "");
                    fill_list(record, l1, l2);
                    sqlite.close();
                }
                return false;
            }

        });

        l2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences USER_RECORD = getSharedPreferences("User_Record", MODE_PRIVATE);
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                final Integer profileId = USER_RECORD.getInt("ProfileID", -1);
                Intent intent = new Intent(mainactivity.this, TrainTrackingActivity.class);
                String station = (String) l2.getItemAtPosition(position);
                String[] station_details = station.split("-\\(");
                String main_station;
                String station_name = station_details[0];
                String station_type = station_details[1].replaceAll("\\)", "");
                ArrayList<HashMap> record = sqlite.GetTableRecordByID(profileId, "train_table");
                if (record == null){
                    Toast.makeText(getApplicationContext(), "Error in getting record by ID", Toast.LENGTH_LONG).show();
                }else {
                    for (HashMap<String, String> rec : record) {
                        if (rec.get("station_name").equals(station_name) && rec.get("station_type").equals(station_type.replaceAll("\\)", ""))) {

                            ArrayList<String> table_record = sqlite.get_table_record("main_stations_table",
                                    "WHERE train_line = '" + station_type.replaceAll("\\)", "'") + "'");
                            if (rec.get("station_dir").equals("1")) {
                                main_station = table_record.get(2);
                            } else {
                                main_station = table_record.get(3);
                            }


                            intent.putExtra("station_type", rec.get("station_type"));
                            intent.putExtra("station_dir", rec.get("station_dir"));
                            intent.putExtra("main_station", main_station);

                            intent.putExtra("station_name", rec.get("station_name"));
                            intent.putExtra("station_lat", Double.parseDouble(rec.get("station_lat")));
                            intent.putExtra("station_lon", Double.parseDouble(rec.get("station_lon")));
                            sqlite.close();
                            startActivity(intent);


                        }
                    }
                }

            }
        });


        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mainactivity.this, ChooseLineActivity.class);
                SharedPreferences.Editor connect = getSharedPreferences("CONNECT", MODE_PRIVATE).edit();

                if (position == 0){
                    connect.putBoolean("connection", false);
                    connect.apply();
                }
                if (position == 1){
                    connect.putBoolean("connection", true);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences("User_Choice_Record", MODE_PRIVATE).edit();
                    editor.putBoolean("connection_to_url", true);
                    connect.apply();
                }
                startActivity(intent);
            }
        });


    }
}