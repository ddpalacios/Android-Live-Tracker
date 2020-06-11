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

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.M)
@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();




        final ArrayList<String> favoriteList = new ArrayList<>();
        final ListView favoriteStations = findViewById(R.id.favorite_lines);
        ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter<String> favoriteadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, favoriteList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
        final ListView list = findViewById(R.id.station_lines);
        list.setAdapter(adapter);

        favoriteStations.setAdapter(favoriteadapter);
        DatabaseHelper sqlite = new DatabaseHelper(context);
        SharedPreferences USER_RECORD = getSharedPreferences("User_Record", MODE_PRIVATE);
        final Integer profileId = USER_RECORD.getInt("ProfileID", -1);
        ArrayList<HashMap> record = sqlite.GetTableRecord(profileId, "train_table");
        final String[] main_menu = new String[]{"Add Favorite Station", "Find Station"};

        for (String items : main_menu) {
            arrayList.add(items);
            adapter.notifyDataSetChanged();
        }


      fill_list(record, favoriteList, favoriteadapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mainactivity.this,ChooseLineActivity.class);
                SharedPreferences.Editor connect = getSharedPreferences("CONNECT", MODE_PRIVATE).edit();

                if (position == 0){
                    connect.putBoolean("connection", false);
                    connect.apply();
                    startActivity(intent);


                }
                if (position == 1){
                    connect.putBoolean("connection", true);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences("User_Choice_Record", MODE_PRIVATE).edit();
                    editor.putBoolean("connection_to_url", true);


                    connect.apply();
                    startActivity(intent);

                }

            }
        });

        favoriteStations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String station = (String) favoriteStations.getItemAtPosition(position);
                String[] station_details = station.split("-\\(");
                String station_name = station_details[0];
                String station_type = station_details[1].replaceAll("\\)", "");
                String where = "profile_id = ?"
                        + " AND station_name = ?"
                        + " AND station_type = ?";
                favoriteList.clear();
                String[] args = new String[]{String.valueOf(profileId), station_name, station_type};
                Log.e("sss", args+"");
                DatabaseHelper sqlite = new DatabaseHelper(context);
                Log.e("WHERE ", where);
                sqlite.deleteRecord("train_table", where, args);
                ArrayList<HashMap> record = sqlite.GetTableRecord(profileId, "train_table");
                Log.e("trains", record + "");
                fill_list(record, favoriteList, favoriteadapter);

                return false;
            }

        });

        favoriteStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mainactivity.this, TrainTrackingActivity.class);
                String station = (String) favoriteStations.getItemAtPosition(position);
                String[] station_details = station.split("-\\(");
                String main_station;

                String station_name = station_details[0];
                String station_type = station_details[1].replaceAll("\\)", "");
                DatabaseHelper sqlite = new DatabaseHelper(context);
                ArrayList<HashMap> record = sqlite.GetTableRecord(profileId, "train_table");
                SharedPreferences.Editor editor = getSharedPreferences("User_Recent_Station_Record", MODE_PRIVATE).edit();
                for (HashMap<String, String> rec : record){
                    if (rec.get("station_name").equals(station_name) && rec.get("station_type").equals(station_type.replaceAll("\\)", ""))){
                        editor.putBoolean("connection_to_url", true);
                        editor.putInt("RecordID", Integer.parseInt(rec.get("RECORD_ID")));
                        editor.putInt("ProfileID", Integer.parseInt(rec.get("profile_id")));
                        editor.putFloat("station_lat", Float.parseFloat(rec.get("station_lat")));
                        editor.putFloat("station_lon", Float.parseFloat(rec.get("station_lon")));
                        editor.putString("station_name", rec.get("station_name"));
                        editor.putString("station_type", rec.get("station_type"));
                        editor.putInt("station_dir", Integer.parseInt(rec.get("station_dir")));
                        editor.apply();
                        intent.putExtra("from_sql", true);
                        ArrayList<String> table_record = sqlite.get_table_record("main_stations_table",
                                "WHERE train_line = '"+station_type.replaceAll("\\)", "'")+"'");
                        if (rec.get("station_dir").equals("1")){


                            main_station = table_record.get(2);


                        }else {
                            main_station = table_record.get(3);
                        }


                        intent.putExtra("station_type", rec.get("station_type"));
                        intent.putExtra("station_dir", rec.get("station_dir"));
                        intent.putExtra("main_station", main_station);

                        intent.putExtra("station_name",rec.get("station_name"));
                        intent.putExtra("station_lat", Double.parseDouble(rec.get("station_lat")));
                        intent.putExtra("station_lon",Double.parseDouble(rec.get("station_lon")));

                        startActivity(intent);


                    }
                }


            }
        });

    }

    public void fill_list(ArrayList<HashMap> record, ArrayList<String> favoriteList, ArrayAdapter favoriteadapter){
        Log.e("re", record+"");
        if (record.isEmpty()) {
//            Toast.makeText(getApplicationContext(), "No Favorite Stations", Toast.LENGTH_SHORT).show();
            favoriteList.add("No Favorite Stations");
            favoriteadapter.notifyDataSetChanged();
        }
        else {
            favoriteList.add(0, "Favorite Stations");
            for (HashMap<String, String> rec : record) {
                String station_name = rec.get("station_name");
                String station_type = rec.get("station_type");
                favoriteList.add(station_name + "-(" + station_type + ")");
                favoriteadapter.notifyDataSetChanged();


            }
        }


    }
}