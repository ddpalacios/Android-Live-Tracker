package com.example.cta_map.Activities;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Adapters.TrainDirAdapter;
import com.example.cta_map.Activities.Adapters.TrainLineAdapter;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChooseTrainDirectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();

        HashMap<String, String> tracking_station = (HashMap<String, String>) bundle.getSerializable("tracking_station");

        setTitle(tracking_station.get("train_line")+ " Line");
        setContentView(R.layout.activity_choose_direction);
        RecyclerView recyclerView = findViewById(R.id.list_of_train_lines);
        ArrayList<ListItem> arrayList = new ArrayList<>();

        String train_line = tracking_station.get("train_line");
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(chicago_transits.GetBackgroundColor(train_line.toLowerCase(), getApplicationContext())));
        HashMap<String, String> main_stations = (HashMap<String, String>) cta_dataBase.excecuteQuery("*", "MAIN_STATIONS", "STATION_TYPE = '"+tracking_station.get("train_line").toUpperCase()+"'", null,null).get(0);

        ListItem listItem = new ListItem();
        listItem.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
        listItem.setTitle(main_stations.get("NORTHBOUND"));
        listItem.setTrain_dir("1");

        arrayList.add(listItem);

        ListItem listItem2 = new ListItem();
        listItem2.setImage(chicago_transits.getTrainImage(tracking_station.get("train_line")));
        listItem2.setTitle(main_stations.get("SOUTHBOUND"));
        listItem2.setTrain_dir("5");
        arrayList.add(listItem2);

        recyclerView.setAdapter(new TrainDirAdapter(getApplicationContext(),arrayList, tracking_station));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }





}
