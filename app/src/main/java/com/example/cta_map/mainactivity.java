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






@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {

    private Button getData, closeConn, toMap, csv_reader, userLoc;
    private EditText station_name, station_type, direction;
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
        station_name = (EditText)findViewById(R.id.station_name);
        station_type = (EditText)findViewById(R.id.station_type);
        direction = (EditText)findViewById(R.id.dest);


        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream CSVfile = getResources().openRawResource(R.raw.train_stations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));
                final Chicago_Transits chicago_transits = new Chicago_Transits(reader);
                openConnection = true;
                debug.ShowToast(context, "Extracting Content...");
                final String stationName = station_name.getText().toString().toLowerCase();
                final String stationType = station_type.getText().toString().toLowerCase();
                final String trainDirection = direction.getText().toString().toLowerCase();

                String[] station_coordinates = chicago_transits.retrieve_station_coordinates(stationName, stationType);
                String nearest_train = chicago_transits.find_nearest_train_from(station_coordinates, stationName, stationType, trainDirection);

                if (station_coordinates == null){
                    debug.ShowToast(context, "No Station Found! Check Spelling!");
                }else {
                    debug.ShowToast(context, stationName+" ("+stationType+")"+" Is Located at: "+ Arrays.toString(station_coordinates));
                }


            }
        });


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


}


//        toMap = (Button) findViewById(R.id.ToMaps);
//        csv_reader = (Button) findViewById(R.id.dataReader);
//        userLoc = (Button) findViewById(R.id.getUserLoc);


//        userLoc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                debug.ShowToast(context, "Retrieving user coord...");
//                getLastLocation();
//
//            }
//        });
//
//
//
//
//        toMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mainactivity.this, MapsActivity.class);
//                startActivity(intent);
//
//            }
//        });
//


//
//
//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @SuppressLint("SetTextI18n")
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Location mLastLocation = locationResult.getLastLocation();
//            Log.e("locCallBack", mLastLocation.getLatitude()+""+mLastLocation.getLongitude()+"");
//
//           /* latTextView.setText(mLastLocation.getLatitude()+"");
//            lonTextView.setText(mLastLocation.getLongitude()+"");*/
//        }
//    };
//
//
//
//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData(){
//
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(0);
//        mLocationRequest.setFastestInterval(0);
////        mLocationRequest.setNumUpdates(1);
//        mLocationRequest.setInterval(0);
//        mLocationRequest.setFastestInterval(0);
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.requestLocationUpdates(
//                mLocationRequest, mLocationCallback,
//                Looper.myLooper()
//        );
//
//    }
//
//
//
//
//    @SuppressLint("MissingPermission")
//    private void getLastLocation(){
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(
//                        new OnCompleteListener<Location>() {
//                            @SuppressLint("SetTextI18n")
//                            @Override
//                            public void onComplete(@NonNull Task<Location> task) {
//                                Location location = task.getResult();
//                                if (location == null) {
//                                    requestNewLocationData();
//                                } else {
//                                    double usr_lat = location.getLatitude();
//                                    double usr_lon = location.getLongitude();
//                                    Log.e("lastLoc", location.getLatitude()+""+location.getLongitude()+"");
//
////                                    latTextView.setText(location.getLatitude()+"");
////                                    lonTextView.setText(location.getLongitude()+"");
//                                }
//                            }
//                        }
//                );
//            } else {
//                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            requestPermissions();
//        }
//    }
//
//
//
//    private boolean checkPermissions() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        return false;
//    }
//
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                PERMISSION_ID
//        );
//    }
//
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//                LocationManager.NETWORK_PROVIDER
//        );
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Granted. Start getting the location information
//            }
//        }
//    }
//
//
//    @Override
//    public void onResume(){
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//
//    }



