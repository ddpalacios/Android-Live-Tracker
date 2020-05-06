package com.example.cta_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.opencsv.CSVReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
    private Button getData, closeConn, toMap, csv_reader, userLoc;
    private EditText station_name, station_type, direction;
    private TextView result, latTextView, lonTextView;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        final Context context = getApplicationContext();
        super.onCreate(savedInstanceState);
        final Debugger debug = new Debugger();
        final WebScrapper webScrapper = new WebScrapper();
        myDb = new DatabaseHelper(this);


        getData = (Button) findViewById(R.id.getData);
        closeConn = (Button) findViewById(R.id.closeData);
        station_name = (EditText) findViewById(R.id.station_name);
        station_type = (EditText) findViewById(R.id.station_type);
        direction = (EditText) findViewById(R.id.dest);
        toMap = (Button) findViewById(R.id.toMaps);

        headToMap();
        retrieveData();

    }

    private void headToMap(){

        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
                final Chicago_Transits chicago_transits = new Chicago_Transits(reader, closeConn);
                Intent intent = new Intent(mainactivity.this, MapsActivity.class);
                final String stationName = station_name.getText().toString().toLowerCase();
                final String stationType = station_type.getText().toString().toLowerCase();
                final String trainDirection = direction.getText().toString().toLowerCase();
                String[] station_coordinates = chicago_transits.retrieve_station_coordinates(stationName, stationType);
                chicago_transits.get_train_coordinates(station_coordinates, stationName, stationType, trainDirection);


                intent.putExtra("station_coordinates",station_coordinates);
                intent.putExtra("train_direction",trainDirection);
                intent.putExtra("station_name", stationName);
                intent.putExtra("station_type", stationType);





                startActivity(intent);


            }
        });



    }
    private void retrieveData(){
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
                final Chicago_Transits chicago_transits = new Chicago_Transits(reader, closeConn);




            }
        });




    }




}


