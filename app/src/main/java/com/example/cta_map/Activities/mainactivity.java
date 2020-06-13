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
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.M)
@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    Bundle bb;
    ArrayList<String> arrayList = new ArrayList<>();
    final ArrayList<String> favoriteList = new ArrayList<>();



    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        bb = getIntent().getExtras();
        final String profile_id= bb.getString("profile_id");
        if (!sqlite.isEmpty("tracking_table")){
            boolean isempty = sqlite.deleteAll("tracking_table");
            Log.e("TRACKING TABLE IS EMPTY", isempty+"");

        }
        final ArrayList<HashMap> table_record = sqlite.GetTableRecordByID(Integer.parseInt(profile_id), "train_table");
        if (table_record.isEmpty()){
            favoriteList.add("No Favorite Stations.");
        }else{
            favoriteList.add(0, "Favorite Stations:");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        final ListView list = findViewById(R.id.station_lines);
        list.setAdapter(adapter);



        final ListView favoriteStations = findViewById(R.id.favorite_lines);
        final ArrayAdapter<String> favoriteadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, favoriteList);
        favoriteStations.setAdapter(favoriteadapter);


        final String[] main_menu = new String[]{"Add Favorite Station", "Find Station"};
        for (String items : main_menu) {
            arrayList.add(items);
            adapter.notifyDataSetChanged();
        }

        for (HashMap train_record: table_record){
//            Log.e("rec", train_record+"");
            favoriteList.add(train_record.get("station_name")+"-("+train_record.get("station_type")+")");
            favoriteadapter.notifyDataSetChanged();

        }




        favoriteStations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    return false;
                }

                String station = (String) favoriteStations.getItemAtPosition(position);
                String[] station_details = station.split("-\\(");
                String station_name = station_details[0];
                String station_type = station_details[1].replaceAll("\\)", "");
                String where = "profile_id = ?"
                        + " AND station_name = ?"
                        + " AND station_type = ?";
                favoriteList.clear();
                String[] args = new String[]{String.valueOf(profile_id), station_name, station_type};
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                sqlite.deleteRecord("train_table", where, args);
                ArrayList<HashMap> record = sqlite.GetTableRecordByID(Integer.parseInt(profile_id), "train_table");

                if (record.isEmpty()) {
                    favoriteList.add("No Favorite Stations");
                    favoriteadapter.notifyDataSetChanged();
                }
                else {
                    favoriteList.add(0, "Favorite Stations");
                    for (HashMap train_record: record){
                        favoriteList.add(train_record.get("station_name")+"-("+train_record.get("station_type")+")");
                        favoriteadapter.notifyDataSetChanged();

                    }

                }

                sqlite.close();
                return false;
            }

        });



        favoriteStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }
                Intent intent = new Intent(mainactivity.this, TrainTrackingActivity.class);

                String station = (String) favoriteStations.getItemAtPosition(position);
                String[] station_details = station.split("-\\(");
                String station_name = station_details[0];
                String station_type = station_details[1].replaceAll("\\)", "");
                ArrayList<String> target_station_record = sqlite.get_table_record("train_table",
                        "WHERE profile_id = '"+profile_id+"' AND station_name = '"+station_name+"' AND station_type = '"+station_type+"'");
                Toast.makeText(getApplicationContext(), target_station_record+"", Toast.LENGTH_LONG).show();
                ArrayList<String> tracking_record = new ArrayList<>(target_station_record.subList(1, target_station_record.size()));
                sqlite.add_train_tracker(tracking_record);

                startActivity(intent);





            }
        });



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    connect.apply();
                }
                connect.putInt("ProfileID", Integer.parseInt(profile_id));
                connect.apply();

                sqlite.close();


                startActivity(intent);


            }
        });
        sqlite.close();
    }
}