package com.example.cta_map.Activities;//package com.example.cta_map.Activities;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.R;
import com.example.cta_map.StationAdapter;
import com.example.cta_map.Stations;
import com.example.cta_map.optionAdapter;
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
        final ArrayList<Stations> list = new ArrayList<>();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Home Page");


        ArrayList<HashMap> favorite_stations = sqlite.getAllRecord("favorite_stations");
        sqlite.close();
        for (HashMap t: favorite_stations){
            list.add(new Stations("#"+t.get("PrimaryId")+"."+" "+t.get("fav_station_name")+"."," "+t.get("fav_station_type"),t.get("fav_station_dir")+""));
        }

        String[] options = new String[]{"Add New Station", "Find Station", "View Map"};
        optionAdapter a = new optionAdapter(getApplicationContext(), options);
        optionsrv.setAdapter(a);
        optionsrv.setLayoutManager(new LinearLayoutManager(this));


        final StationAdapter adapter = new StationAdapter(getApplicationContext(), list);
//        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
//        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

    }





}

