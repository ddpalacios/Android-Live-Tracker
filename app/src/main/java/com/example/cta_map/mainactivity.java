package com.example.cta_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Debugger{
    void ShowToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

    }
}






@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {

    public final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=red";
    private Button getData, closeConn, toMap, csv_reader, userLoc;
    private TextView result, latTextView, lonTextView;
    private Boolean openConnection = true;
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



        result = (TextView) findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        getData = (Button) findViewById(R.id.getData);
        closeConn = (Button) findViewById(R.id.closeData);
        toMap = (Button) findViewById(R.id.ToMaps);
        csv_reader = (Button) findViewById(R.id.dataReader);
        userLoc = (Button) findViewById(R.id.getUserLoc);


        userLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debug.ShowToast(context, "Retrieving user coord...");
                getLastLocation();








            }
        });




        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainactivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

        final String[] tags = {"lat","lon","isApp", "nextStaNm"};
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnection = true;
                debug.ShowToast(context, "Extracting Content...");
//                webScrapper.Connect(url, true, tags);
                extract_train_content(url, debug);
            }
        });


        csv_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debug.ShowToast(context, "READING CSV...");
                InputStream is = getResources().openRawResource(R.raw.train_stations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                int cnt=0;
                while (true){
                    try {
                        if ((line = reader.readLine()) != null){
                            String[] tokens = line.split(",");
                             String station_name = tokens[0].toLowerCase();
                            HashMap<String, String> train_lines = new HashMap<>();
                            HashMap<String, String> trains = GetStation(tokens, train_lines);

                             if (station_name.equals("granville") && Boolean.parseBoolean(trains.get("red"))){
                                 cnt++;
                                 String[] coord = getCord(tokens);
                                 Log.e("COORD", Arrays.toString(coord));
                                 break;

                             }


                        }else{
                            break;
                        }

                    }catch (IOException e){
                        e.printStackTrace();

                    }


                }
                debug.ShowToast(context, cnt+" STATION(S) FOUND.");
            }
        });


    }


    private HashMap<String, String> GetStation(String [] tokens, HashMap<String, String> train_lines){

        // Train lines
        String red = tokens[1];
        String blue = tokens[2];
        String green = tokens[3];
        String brown = tokens[4];
        String purple = tokens[5];
        String yellow = tokens[6];
        String pink = tokens[7];
        String orange = tokens[8];

        // Add to our Data Structure
        train_lines.put("red", red);
        train_lines.put("blue", blue);
        train_lines.put("green", green);
        train_lines.put("purple", purple);
        train_lines.put("yellow", yellow);
        train_lines.put("pink", pink);
        train_lines.put("orange", orange);
        train_lines.put("brown", brown);



        return train_lines;
    }


    private String[] getCord(String [] tokens){

        // coordinates parse
        String station_coord = tokens[9] +tokens[10];
        station_coord = station_coord.replace("(", "").replace(")", "");
        return station_coord.split(" ");




    }



    private void extract_train_content(final String url, final Debugger debugger){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (openConnection) {

                    try {
                        Document content = Jsoup.connect(url).get();
                        final Elements lat = content.select("lat");
                        final Elements lon = content.select("lon");
                        final Elements isApp = content.select("isApp");
                        final Elements dest = content.select("nextStaNm");

                        String latitude = lat.text();
                        String longtitude = lon.text();
                        String destination = dest.text();
                        String isApproaching = isApp.text();


                        final String[] lat_list = latitude.split(" ");
                        final String[] lon_list = longtitude.split(" ");
                        final String[] app_list = isApproaching.split(" ");
                        final String[] destiniation_station = destination.split(" ");

                        Log.d("Latititudes", Arrays.toString(lat_list));
                        Log.d("Longtitude", Arrays.toString(lon_list));
                        Log.d("Longtitude", Arrays.toString(app_list));


                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                if (result.getText().toString().isEmpty()){
                                    for (int i=0; i< lat_list.length; i++){

                                        String isApp = app_list[i];
                                        if (isApp.equals("1")){
                                            result.append("\n\nTrain# "+i+"  "+lat_list[i]+"   "+ lon_list[i]+"\t\tApproaching: "+ destiniation_station[i]);

                                        }
                                        else{
                                            result.append("\n\nTrain# "+i+"  "+lat_list[i]+"   "+ lon_list[i]+"\t\tNext Stop: "+ destiniation_station[i]);

                                        }

                                    }
                                }
                                else {
                                    result.setText(null);
                                    run();

                                }

                            }
                        });

                    } catch (IOException e) {
                        Log.d("Error", "Error in extracting");
                    }


                }
            }
        }).start();

        closeConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnection = false;


                debugger.ShowToast(getApplicationContext(), "Connection Closed!");
                Log.d("Connection Status", "Connection Closed");
            }
        });

    }


    private LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.e("loc", mLastLocation.getLongitude()+"");

           /* latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");*/
        }
    };



    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
//        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }




    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.e("loc", location.getLongitude()+"");
//                                    latTextView.setText(location.getLatitude()+"");
//                                    lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }



    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }


}

