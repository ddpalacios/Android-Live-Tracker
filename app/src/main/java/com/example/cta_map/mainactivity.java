package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        Bundle bb;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
        final ListView list = (ListView) findViewById(R.id.station_lines);
        list.setAdapter(adapter);

        final ArrayList<String> favoriteList = new ArrayList<>();
        final ArrayAdapter<String> favoriteadapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, favoriteList);
        final ListView favoriteStations = (ListView) findViewById(R.id.favorite_lines);
        favoriteStations.setAdapter(favoriteadapter);
        favoriteList.add("Favorite Stations");
        favoriteadapter.notifyDataSetChanged();
        DatabaseHelper sqlite = new DatabaseHelper(context);
        SharedPreferences USER_RECORD = getSharedPreferences("User_Record", MODE_PRIVATE);
        final Integer profileId = USER_RECORD.getInt("ProfileID", -1);
        ArrayList<HashMap> record = sqlite.GetTableRecord(profileId, "train_table");
        if (record.isEmpty()) {
            favoriteList.clear();
            Toast.makeText(context, "No Favorite Stations", Toast.LENGTH_SHORT).show();
            favoriteList.add(0, "No Favorite Stations");
            favoriteadapter.notifyDataSetChanged();
        } else {
            Log.e("trains", record + "");
            for (HashMap<String, String> rec : record) {
                Log.e("Rec",rec+"");
                String station_name = rec.get("station_name");
                String station_type = rec.get("station_type");
                favoriteList.add(station_name + " (" + station_type + ")");
                favoriteadapter.notifyDataSetChanged();

            }

        }


        final String[] main_menu = new String[]{"Add Favorite Station", "Find Station"};
        for (String items : main_menu) {
            arrayList.add(items);
            adapter.notifyDataSetChanged();
        }


        favoriteStations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String station = (String) favoriteStations.getItemAtPosition(position);
                String[] station_details = station.split(" \\(");
                String station_name = station_details[0];
                String station_type = station_details[1];
                String where = "profile_id = ?"
                        + " AND station_name = ?"
                        + " AND station_type = ?";
                favoriteList.clear();
                favoriteList.add(0, "Favorite Stations");
                String[] args = new String[]{String.valueOf(profileId), station_name, station_type.replaceAll("\\)", "")};
                DatabaseHelper sqlite = new DatabaseHelper(context);
                Log.e("WHERE ", where);
                sqlite.deleteRecord("train_table", where, args);
                ArrayList<HashMap> record = sqlite.GetTableRecord(profileId, "train_table");
                Log.e("trains", record + "");
                if (record.isEmpty()) {
                    Toast.makeText(context, "No Favorite Stations", Toast.LENGTH_SHORT).show();
                    favoriteList.set(0, "No Favorite Stations");
                    favoriteadapter.notifyDataSetChanged();
                }
            else {
                    for (HashMap<String, String> rec : record) {
                        station_name = rec.get("station_name");
                        station_type = rec.get("station_type");
                        favoriteList.add(station_name + " (" + station_type + ")");
                        favoriteadapter.notifyDataSetChanged();


                    }
                }
                return false;
            }

        });

        favoriteStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mainactivity.this, TrainTrackingActivity.class);
                String station = (String) favoriteStations.getItemAtPosition(position);
                String[] station_details = station.split(" \\(");
                String station_name = station_details[0];
                String station_type = station_details[1];
                DatabaseHelper sqlite = new DatabaseHelper(context);
                ArrayList<HashMap> record = sqlite.GetTableRecord(profileId, "train_table");
                SharedPreferences.Editor editor = getSharedPreferences("Train_Record", MODE_PRIVATE).edit();
                for (HashMap<String, String> rec : record){
                    if (rec.get("station_name").equals(station_name) && rec.get("station_type").equals(station_type.replaceAll("\\)", ""))){
                        editor.putInt("RecordID", Integer.parseInt(rec.get("RECORD_ID")));
                        editor.putInt("ProfileID", Integer.parseInt(rec.get("profile_id")));
                        editor.putFloat("station_lat", Float.parseFloat(rec.get("station_lat")));
                        editor.putFloat("station_lon", Float.parseFloat(rec.get("station_lon")));
                        editor.putString("station_name", rec.get("station_name"));
                        editor.putString("station_type", rec.get("station_type"));
                        editor.putInt("station_dir", Integer.parseInt(rec.get("station_dir")));
                        editor.apply();

                        startActivity(intent);


                    }
                }


            }
        });




        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent favorite_station_intent = new Intent(mainactivity.this,ChooseLineActivity.class);
                Intent browse_station_intent = new Intent(mainactivity.this,ChooseLineActivity.class);
                SharedPreferences.Editor connect = getSharedPreferences("CONNECT", MODE_PRIVATE).edit();

                if (position == 0){
                    connect.putBoolean("connection", false);
                    connect.apply();
                    startActivity(favorite_station_intent);


                }
                if (position == 1){
                    connect.putBoolean("connection", true);
                    connect.apply();
                    startActivity(browse_station_intent);

                }

            }
        });

    }
}