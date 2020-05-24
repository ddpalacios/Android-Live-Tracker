package com.example.cta_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class Debugger{
    void ShowToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}

@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {
    private Button  toMap;
    private EditText station_name, station_type, direction;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        station_name = (EditText) findViewById(R.id.station_name);
        station_type = (EditText) findViewById(R.id.station_type);
        direction = (EditText) findViewById(R.id.dest);
        toMap = (Button) findViewById(R.id.toMaps);
        toMap.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                final Context context = getApplicationContext();
                final Debugger debug = new Debugger();
                final Chicago_Transits chicago_transits = new Chicago_Transits();
                BufferedReader station_coordinates_reader = chicago_transits.setup_file_reader(context, R.raw.train_stations);
                Intent intent = new Intent(mainactivity.this, MapsActivity.class);
                final String stationName = station_name.getText().toString().toLowerCase().replaceAll(" ", "");
                final String stationType = station_type.getText().toString().toLowerCase();
                final String trainDirection = direction.getText().toString().toLowerCase();
                String[] target_station_coordinates = chicago_transits.retrieve_station_coordinates(station_coordinates_reader, stationName, stationType);
                if (target_station_coordinates == null){
                    debug.ShowToast(context, "Error! Target Station Not Found");
                }else {
                    intent.putExtra("target_station_name", stationName);
                    intent.putExtra("target_station_type", stationType);
                    intent.putExtra("train_direction", trainDirection);
                    startActivity(intent);
                }
            }

        });
    }
}