package com.example.cta_map.Activities;//package com.example.cta_map.Activities;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class mainactivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        cta_dataBase.testfunc();

        CTA_DataBase sqlite = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> record = sqlite.excecuteQuery("*", "main_stations", "main_station_type = 'Blue'");
        for (int i=0; i<record.size(); i++){
            Object obj = record.get(i);
            HashMap<String, String> r = (HashMap<String, String>) obj;
            Log.e(Thread.currentThread().getName(),r.get("northbound")+"");
        }
        Log.e(Thread.currentThread().getName(), "COUNT: "+ record.size());
        ArrayList<Object> record1 = sqlite.excecuteQuery("*", "cta_stops", "station_name = 'Granville'");
        HashMap<String, String> r = (HashMap<String, String>) record1.get(0);
        Log.e(Thread.currentThread().getName(), "NEW "+ r.get("station_name")+" "+ r.get("location"));

        CardView choose_station = (CardView) findViewById(R.id.choose_station);
        choose_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainactivity.this, ChooseLineActivity.class);
                startActivity(intent);
            }
        });


//        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recycler_view);
//        RecyclerView optionsrv = (RecyclerView) findViewById(R.id.ViewOptions);
//        final ArrayList<Stations> list = new ArrayList<>();
//        ActionBar actionBar = getSupportActionBar();
//        assert actionBar != null;
//        actionBar.setTitle("Home Page");






//        String query1 = "SELECT station_id FROM cta_stops WHERE station_name = 'Granville'" + " AND red = 'true'";
//        String station_id = sqlite.getValue(query1);
//        Log.e("WWW", station_id+" ");
//
//        ArrayList<HashMap> favorite_stations = sqlite.getAllRecord("favorite_stations");
//        sqlite.close();
//        for (HashMap t: favorite_stations){
//            list.add(new Stations("#"+t.get("PrimaryId")+"."+" "+t.get("fav_station_name")+"."," "+t.get("fav_station_type"),t.get("fav_station_dir")+""));
//        }
//
//        String[] options = new String[]{"Add New Station", "Find Station", "View Map"};
//        optionAdapter a = new optionAdapter(getApplicationContext(), options);
//        optionsrv.setAdapter(a);
//        optionsrv.setLayoutManager(new LinearLayoutManager(this));
//
//
//        final StationAdapter adapter = new StationAdapter(getApplicationContext(), list);
////        // Attach the adapter to the recyclerview to populate items
//        rvContacts.setAdapter(adapter);
////        // Set layout manager to position the items
//        rvContacts.setLayoutManager(new LinearLayoutManager(this));
//
    }
//


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int createDB(int file1, int file2, int file3){
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader file1Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file1);
        BufferedReader file2Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file2);
        BufferedReader file3Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file3);

        chicago_transits.Create_TrainInfo_table(file1Buffer, getApplicationContext());
        chicago_transits.create_line_stops_table(file2Buffer, getApplicationContext());
        chicago_transits.create_main_station_table(file3Buffer, getApplicationContext());




        return 0;
    }






}

