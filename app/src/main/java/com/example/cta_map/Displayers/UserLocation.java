package com.example.cta_map.Displayers;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

public class UserLocation extends Activity {
    Context context;
    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;

    public UserLocation(Context context){
        this.context = context;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);


    }

    public void getLastLocation(final Context ctx){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                this.mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Time time = new Time();
                                DatabaseHelper sqlite = new DatabaseHelper(ctx);
//                                NotificationBuilder notificationBuilder = new NotificationBuilder(context, inten);

                                Chicago_Transits chicago_transits = new Chicago_Transits();
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                }else{
                                    if (sqlite.isEmpty("userLocation_table")){
                                        sqlite.addLocation(location.getLatitude(), location.getLongitude());
                                    }else{
                                        sqlite.update_location("1", "userLocation_table", location.getLatitude(), location.getLongitude());
                                    }

                                    sqlite.close();

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this.context, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                this.context.startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        this.mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };
    public boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
            }
        }
    }


}