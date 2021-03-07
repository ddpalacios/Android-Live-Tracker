package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Adapters.TrainLineAdapter;
import com.example.cta_map.Activities.Adapters.TrainStationAdapter;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChooseStationActivity extends AppCompatActivity {
    HashMap<String, String> tracking_station;
    TrainStationAdapter adapter;
    ArrayList<ListItem> StationList;
    String TITLE_NAME = "STOP_NAME";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Chicago_Transits chicago_transits = new Chicago_Transits();

        setContentView(R.layout.activity_choose_station);
        RecyclerView recyclerView = findViewById(R.id.list_of_train_lines);
        Bundle bundle = getIntent().getExtras();
        tracking_station = (HashMap<String, String>) bundle.getSerializable("tracking_station");
        setTitle("To "+ tracking_station.get("main_station_name"));

        String train_direction = tracking_station.get("train_dir");
        String train_line = tracking_station.get("train_line").toUpperCase().trim();

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(chicago_transits.GetBackgroundColor(train_line.toLowerCase(), getApplicationContext())));



        /*

        -------
        Red -
        Tr: 1 = N
        Tr: 5 = S

        Purple:
        Tr: 1 = N
        Tr: 5 = S

        Yellow:
        Tr: 1 = N
        Tr: 5 = S
        ---------
        Blue -
        Tr: 1 = N, E
        Tr: 5 = S, W

        Green:
        Tr: 1 = N, W
        Tr: 5 = S, E
        -----------
        Pink:
        Tr: 1 = E, S, N
        Tr: 5 = E, S, N, W
        -------------
        Orange:
        Tr: 1 = N,E,W
        Tr: 5 = S,E,W

        Brown:
        Tr: 1 = N, E, W
        Tr: 5 = S, E, W
        --------------

         */
        ArrayList<Object> all_station_list = null;
        try {
            all_station_list = getStationList(train_line, train_direction, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (all_station_list != null){
            StationList = new ArrayList<>();
            for (Object sta : all_station_list){
                ListItem listItem = new ListItem();
                HashMap<String, String> station = (HashMap<String, String>) sta;
                listItem.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
                listItem.setTitle(station.get(TITLE_NAME));
                listItem.setTrain_dir_label(station.get("DIRECTION_ID"));
                listItem.setMapID(station.get("MAP_ID"));
                StationList.add(listItem);
            }
        }
        adapter = new TrainStationAdapter(getApplicationContext(),StationList, tracking_station);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu, menu);
            MenuItem clearData = menu.findItem(R.id.clearData);
            MenuItem getData = menu.findItem(R.id.getData);
            MenuItem searchView_item =  menu.findItem(R.id.app_bar_search);
            SearchView searchView = (SearchView) searchView_item.getActionView();
            searchView.setOnCloseListener(() -> {
                Toast.makeText(getApplicationContext(), "Closed", Toast.LENGTH_SHORT).show();
                try {
                    ArrayList<Object> all_station_list = getStationList(tracking_station.get("train_line"), tracking_station.get("train_dir"), null);
                    Chicago_Transits chicago_transits = new Chicago_Transits();
                    if (all_station_list !=null){
                        StationList.clear();
                        for (Object t : all_station_list){
                            ListItem listItem = new ListItem();
                            HashMap<String, String> station = (HashMap<String, String>) t;
                            listItem.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
                            listItem.setTitle(station.get(TITLE_NAME));
                            listItem.setTrain_dir_label(station.get("DIRECTION_ID"));
                            listItem.setMapID(station.get("MAP_ID"));
                            StationList.add(listItem);
                        }
                        adapter.notifyDataSetChanged();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                    Chicago_Transits chicago_transits = new Chicago_Transits();
                    try {
                        ArrayList<Object> all_station_list = cta_dataBase.excecuteQuery("*", "CTA_STOPS",
                                chicago_transits.TrainLineKeys(tracking_station.get("train_line")) + " = '1' AND "+TITLE_NAME,
                                newText,
                                null);
                        cta_dataBase.close();
                        if (all_station_list !=null){
                            StationList.clear();
                            for (Object t : all_station_list){
                                ListItem listItem = new ListItem();
                                HashMap<String, String> station = (HashMap<String, String>) t;
                                listItem.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
                                listItem.setTitle(station.get(TITLE_NAME));
                                listItem.setTrain_dir_label(station.get("DIRECTION_ID"));
                                listItem.setMapID(station.get("MAP_ID"));
                                StationList.add(listItem);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            getData.setOnMenuItemClickListener(item -> {
                Toast.makeText(getApplicationContext(), "Clicked "+ item.getTitle(), Toast.LENGTH_SHORT).show();
                try {
                    createDB(R.raw.cta_stops, R.raw.main_stations, R.raw.line_stops);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            });
            clearData.setOnMenuItemClickListener(item1 -> {
                Toast.makeText(getApplicationContext(), "Clicked "+ item1.getTitle(), Toast.LENGTH_SHORT).show();
                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                cta_dataBase.delete_all_records("L_STOPS");
                cta_dataBase.delete_all_records("MAIN_STATIONS");
                cta_dataBase.delete_all_records("CTA_STOPS");
                cta_dataBase.delete_all_records("USER_FAVORITES");
                cta_dataBase.delete_all_records("MARKERS");
                finish();
                startActivity(getIntent());


                return false;
            });
            return true;
        }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int createDB(int file1, int file2, int file3) throws IOException {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader file1Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file1);
        BufferedReader file2Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file2);
        BufferedReader file3Buffer = chicago_transits.setup_file_reader(getApplicationContext(), file3);
        chicago_transits.create_line_stops_table(file3Buffer, getApplicationContext(), null);
        chicago_transits.Create_TrainInfo_table(file1Buffer, getApplicationContext());
        chicago_transits.create_main_station_table(file2Buffer, getApplicationContext());
        chicago_transits.createMarkerTable(getApplicationContext());
        return 0;
    }
    public ArrayList<Object> getStationList(String train_line, String train_direction, String contains) throws Exception{
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        String east_condition = chicago_transits.TrainLineKeys(train_line) + " = '1' AND DIRECTION_ID = 'E'" ;
        String north_condition = chicago_transits.TrainLineKeys(train_line) + " = '1' AND DIRECTION_ID = 'N'" ;
        String west_condition = chicago_transits.TrainLineKeys(train_line) + " = '1' AND DIRECTION_ID = 'W'" ;
        String south_condition = chicago_transits.TrainLineKeys(train_line) + " = '1' AND DIRECTION_ID = 'S'" ;
        if (contains!=null){
            east_condition = east_condition +  "AND "+TITLE_NAME;
            north_condition = north_condition + "AND "+TITLE_NAME;
            west_condition = west_condition + "AND "+TITLE_NAME;
            south_condition = south_condition + "AND "+TITLE_NAME;
        }
        ArrayList<Object> NorthBound;
        ArrayList<Object> EastBound;
        ArrayList<Object> SouthBound;
        ArrayList<Object> WestBound;
        ArrayList<Object> all_station_list = new ArrayList<>();

        if (train_line.toLowerCase().equals("red") || train_line.toLowerCase().equals("yellow") || train_line.toLowerCase().equals("purple")){
            if (train_direction.equals("1")){
                NorthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", north_condition,contains,null);
                all_station_list.addAll(NorthBound);

            }else{
                SouthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", south_condition,contains,null);
                all_station_list.addAll(SouthBound);
            }

        }
        else if (train_line.toLowerCase().equals("blue")){
            if (train_direction.equals("1")){
                EastBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", east_condition,contains,null);
                NorthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", north_condition,contains,null);
                EastBound.addAll(NorthBound);
                all_station_list.addAll(EastBound);


            }else{
                SouthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", south_condition,contains,null);
                WestBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", west_condition,contains,null);
                SouthBound.addAll(WestBound);
                all_station_list.addAll(SouthBound);

            }
        }
        else if (train_line.toLowerCase().equals("green")){
            if (train_direction.equals("1")){
                NorthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", north_condition,contains,null);
                WestBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", west_condition,contains,null);
                NorthBound.addAll(WestBound);
                all_station_list.addAll(NorthBound);

            }else{
                SouthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", south_condition,contains,null);
                EastBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", east_condition,contains,null);
                SouthBound.addAll(EastBound);
                all_station_list.addAll(SouthBound);
            }
        }else if (train_line.toLowerCase().equals("orange") || train_line.toLowerCase().equals("brown")){

            if (train_direction.equals("1")) {
                NorthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", north_condition, contains, null);
                EastBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", east_condition, contains, null);
                WestBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", west_condition, contains, null);
                NorthBound.addAll(EastBound);
                NorthBound.addAll(WestBound);
                all_station_list.addAll(NorthBound);


            }else{
                SouthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", south_condition,contains,null);
                EastBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", east_condition, contains, null);
                WestBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", west_condition, contains, null);
                SouthBound.addAll(EastBound);
                SouthBound.addAll(WestBound);
                all_station_list.addAll(SouthBound);
            }

        }else if (train_line.toLowerCase().equals("pink")){
            NorthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", north_condition, contains, null);
            SouthBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", south_condition,contains,null);
            EastBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", east_condition, contains, null);
            WestBound = cta_dataBase.excecuteQuery("*", "CTA_STOPS", west_condition, contains, null);

            NorthBound.addAll(SouthBound);
            NorthBound.addAll(WestBound);
            NorthBound.addAll(EastBound);
            all_station_list.addAll(NorthBound);
        }
        cta_dataBase.close();
        return all_station_list;
    }





























}
