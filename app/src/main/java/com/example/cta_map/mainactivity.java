package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


class Debugger{
    void ShowToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    DatabaseHelper myDb;
    private Button  toMap;
    private EditText station_name, station_type, direction;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);


        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        station_name = (EditText) findViewById(R.id.station_name);
        station_type = (EditText) findViewById(R.id.station_type);
        direction = (EditText) findViewById(R.id.dest);
        toMap = (Button) findViewById(R.id.toMaps);
        headToMap();


    }

    private void headToMap(){

        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Context context = getApplicationContext();
                final Debugger debug = new Debugger();


                InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
                final Chicago_Transits chicago_transits = new Chicago_Transits(reader);
                Intent intent = new Intent(mainactivity.this, MapsActivity.class);


                final String stationName = station_name.getText().toString().toLowerCase().replaceAll(" ", "");
                final String stationType = station_type.getText().toString().toLowerCase();
                final String trainDirection = direction.getText().toString().toLowerCase();



                String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(stationName, stationType);
                if (target_station_coordinates == null){
                    debug.ShowToast(context, "Error! Target Station Not Found");
                }
                else {
                    intent.putExtra("target_station_name", stationName);
                    intent.putExtra("target_station_type", stationType);
                    intent.putExtra("target_station_coordinates", target_station_coordinates);
                    intent.putExtra("train_direction", trainDirection);


                    startActivity(intent);
                }

            }

        });



    }




}


