package com.example.cta_map.Activities;//package com.example.cta_map.Activities;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.R;
import com.example.cta_map.StationAdapter;
import com.example.cta_map.Stations;
import com.example.cta_map.optionAdapter;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class mainactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Database2 sqlite = new Database2(getApplicationContext());
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView optionsrv = (RecyclerView) findViewById(R.id.ViewOptions);
        ArrayList<Stations> list = new ArrayList<>();
        ArrayList<HashMap> favorite_stations = sqlite.getAllRecord("favorite_stations");
//        Log.e("dd", "fav"+ favorite_stations);
        for (HashMap t: favorite_stations){
//            Log.e("ttt", "f"+t);
            list.add(new Stations("#"+t.get("PrimaryId")+"."+" "+t.get("fav_station_name")+"."," "+t.get("fav_station_type"),t.get("fav_station_dir")+""));
        }

        String[] options = new String[]{"Add New Station", "Find Station", "View Map"};
        optionAdapter a = new optionAdapter(getApplicationContext(), options);
        optionsrv.setAdapter(a);
        optionsrv.setLayoutManager(new LinearLayoutManager(this));





        StationAdapter adapter = new StationAdapter(getApplicationContext(), list);
//        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
//        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));




    }
}

//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.cta_map.DataBase.Database2;
//import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.R;
//
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//@RequiresApi(api = Build.VERSION_CODES.M)
//@SuppressLint("Registered")
//public class mainactivity extends AppCompatActivity {
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    Bundle bb;
//    ArrayList<String> arrayList = new ArrayList<>();
//    final ArrayList<String> favoriteList = new ArrayList<>();
//
//
//
//    public void fill_fav_station_list(ArrayList<HashMap> table_record, ArrayAdapter<String> favoriteadapter){
//        if (table_record.isEmpty()){
//                favoriteList.add("No Favorite Stations.");
//            }else {
//                favoriteList.add(0, "Favorite Stations:");
//                for (HashMap station: table_record){
//                    Log.e("ff", station+"");
//                    if (station.get("fav_station_dir").equals("1")){
//                        favoriteList.add("("+station.get("fav_station_type")+") Station ID#"+station.get("fav_station_id")+"   -"+station.get("fav_station_name") +"-      ||North||");
//
////                        query = "SELECT northbound FROM main_stations WHERE main_station_type = '"+station.get("fav_station_type").toString().toUpperCase()+"'";
//                    }else{
//                        favoriteList.add("("+station.get("fav_station_type")+") Station ID#"+station.get("fav_station_id")+"   -"+station.get("fav_station_name") +"-   ||South||");
//
//
//                    }
////                        query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+station.get("fav_station_type").toString().toUpperCase()+"'";
////
////                    }
//                    String main_station = sqlite.getValue(query);
//
//
//                    favoriteadapter.notifyDataSetChanged();
//                }
//
//        }
//
//    }
//
//    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_main);
//        super.onCreate(savedInstanceState);
//        final ArrayAdapter<String> favoriteadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, favoriteList){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view =super.getView(position, convertView, parent);
//                TextView textView=(TextView) view.findViewById(android.R.id.text1);
//                textView.setTextColor(Color.BLACK);
//
//                // Set the item text style to bold
//                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//
//                // Change the item text size
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
//                return view;
//            }
//        };
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        Database2 sqlite = new Database2(getApplicationContext());
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList){
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view =super.getView(position, convertView, parent);
//                TextView textView=(TextView) view.findViewById(android.R.id.text1);
//                textView.setTextColor(Color.BLACK);
//
//                // Set the item text style to bold
//                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//
//                // Change the item text size
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
//                return view;
//            }
//        };
//        final ListView favoriteStations = findViewById(R.id.favorite_lines);
//        final ListView list = findViewById(R.id.station_lines);
//        favoriteStations.setAdapter(favoriteadapter);
//        list.setAdapter(adapter);
////        BufferedReader all_cta_stops = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_stations);
////        BufferedReader main_stations = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.main_stations);
////        BufferedReader line_stops = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops);
////
////        chicago_transits.Create_TrainInfo_table(all_cta_stops, getApplicationContext());
////        chicago_transits.create_line_stops_table(line_stops, getApplicationContext());
////        chicago_transits.create_main_station_table(main_stations, getApplicationContext());
//
//
//
//        final String[] main_menu = new String[]{"Add Favorite Station", "Find Station"};
//        for (String items : main_menu) {
//            arrayList.add(items);
//            adapter.notifyDataSetChanged();
//        }
//
//        fill_fav_station_list(sqlite.getAllRecord("favorite_stations"), favoriteadapter);
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mainactivity.this, ChooseLineActivity.class);
//                intent.putExtra("position", position);
//                startActivity(intent);
//            }
//        });
//
//        favoriteStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mainactivity.this, TrainTrackingActivity.class);
//                String item = String.valueOf(favoriteStations.getItemAtPosition(position));
//                Database2 sqlite = new Database2(getApplicationContext());
//                String station_id =StringUtils.substringBetween(item, "#", " ");
//                String station_type =StringUtils.substringBetween(item, "(", ")");
//                String station_dir =StringUtils.substringBetween(item, "||", "||");
//                String station_name =StringUtils.substringBetween(item, "-", "-");
//                String v= null;
//                if (station_dir.equals("North")) {
//                    v = "1";
//                }else{
//                    v="5";
//                }
//                String query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+station_type.toUpperCase()+"'";
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//                String main_station = sqlite.getValue(query);
//                sqlite.add_tracking_station(station_name, station_type, v, main_station, station_coord, station_id);
//
//
//
//
//                startActivity(intent);
//
//
//
//            }
//        });
//
//        favoriteStations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Database2 db= new Database2(getApplicationContext());
//                String item = String.valueOf(favoriteStations.getItemAtPosition(position));
//                String[] station_id =new String[]{StringUtils.substringBetween(item, "#", "-")};
//                String where = "station_id = ?";
//                db.DeleteRecentStation(where, station_id);

//
//                return false;
//            }
//        });
//    }
//}