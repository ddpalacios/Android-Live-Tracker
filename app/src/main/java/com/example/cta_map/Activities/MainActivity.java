package com.example.cta_map.Activities;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

// For navigation

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.place_holder, new MainPlaceHolder_Fragment());
        ft.commit();
        FloatingActionButton floatingActionButton = findViewById(R.id.AddStationFloatingButton);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseTrainLineActivity.class);
            startActivity(intent);
        });



    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem clearData = menu.findItem(R.id.clearData);
        MenuItem getData = menu.findItem(R.id.getData);

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
}


