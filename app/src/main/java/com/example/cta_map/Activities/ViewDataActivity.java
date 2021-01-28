package com.example.cta_map.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ViewDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        final CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        Button home = findViewById(R.id.Home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataActivity.this, mainactivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });



        RenderData();
        Button data_test_button = findViewById(R.id.data_test_button);
        Button clear_button = findViewById(R.id.clear_data);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cta_dataBase.delete_all_records("L_STOPS");
                cta_dataBase.delete_all_records("MAIN_STATIONS");
                cta_dataBase.delete_all_records("CTA_STOPS");
                cta_dataBase.delete_all_records("USER_FAVORITES");
                RenderData();


            }
        });


        data_test_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    createDB(R.raw.cta_stops, R.raw.main_stations, R.raw.line_stops);
                    RenderData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void RenderData(){

        TextView UF = findViewById(R.id.fav_stations_textView);
        TextView MS = findViewById(R.id.main_stations_textView2);
        TextView LS = findViewById(R.id.l_stops_textView11);
        TextView CS = findViewById(R.id.cta_stops_textView15);
        final CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());

        ArrayList<Object> fav_station_count = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", null, null);
        ArrayList<Object> cta_stops = cta_dataBase.excecuteQuery("*", "CTA_STOPS", null, null);
        ArrayList<Object> L_stops = cta_dataBase.excecuteQuery("*", "L_STOPS", null, null);
        ArrayList<Object> main_stations = cta_dataBase.excecuteQuery("*", "MAIN_STATIONS", null, null);
        if (fav_station_count == null){
            UF.setText("Fav Station: 0");
        }else{
            UF.setText("Fav Station: "+ fav_station_count.size());
        }
        if (cta_stops == null){
            CS.setText("Cta_Stops: 0");
        }else {
            CS.setText("Cta_Stops: "+ cta_stops.size());
        }
        if (main_stations == null){
            MS.setText("Main Stations: 0");
        }else{
            MS.setText("Main Stations: "+main_stations.size());
        }
        if (L_stops == null){
            LS.setText("L Stops: 0");
        }else{
            LS.setText("L_Stops: "+ L_stops.size());
        }






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
        return 0;
    }

}
