package com.example.cta_map;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
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

    public void getLastLocation( final Intent inten,
                                 final Context context,
                                 final HashMap<String, String>train_info,
                                 final Integer train_eta,
                                 final Boolean[] t,
                                 final Boolean[] yel,
                                 final Boolean[] pink,
                                 final GoogleMap mMap,
                                 final Boolean inMaps){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                this.mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Time time = new Time();
                                NotificationBuilder notificationBuilder = new NotificationBuilder(context, inten);

                                Chicago_Transits chicago_transits = new Chicago_Transits();
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                }else{
                                    if (train_info ==null ){
                                        Log.e("None", "None");

                                    }else {




                                        Double distance_from_user_and_target = chicago_transits.calculate_coordinate_distance(
                                                location.getLatitude(),
                                                location.getLongitude(),
                                                Double.parseDouble(train_info.get("target_station_lat")),Double.parseDouble(train_info.get("target_station_lon")));
                                        int user_to_target_eta = 5;//time.get_estimated_time_arrival((int) 3.1, distance_from_user_and_target);
                                        if (inMaps) {
                                            MapMarker mapMarker = new MapMarker(mMap);
                                            mapMarker.display_marker_boundries(inten, context, train_eta, user_to_target_eta, train_info, train_info.get("station_type"), 0, 10);
                                        }else{
                                        if (user_to_target_eta <= train_eta) {
                                                int minutes_to_spare = train_eta - user_to_target_eta;
                                                if (!t[0]){
                                                    notificationBuilder.notificationDialog("Train is "+train_eta+" Minutes Away From "+train_info.get("target_station"),
                                                            "You Have "+minutes_to_spare+" Minutes To Spare.");
                                                    Log.e("Update", "Green!!!!");
                                                    Log.e("bool", t[0]+"");
                                                    t[0] = true;
                                                    }
                                                else{
                                                    Log.e("bool", t[0]+"");
                                                    Log.e("Update", "notified for green");

                                                }
                                            }else if (user_to_target_eta > train_eta){
                                                int late_amount = user_to_target_eta - train_eta;
                                                if (late_amount >=0 && late_amount <4 ){

                                                    if (!yel[0]){
                                                        notificationBuilder.notificationDialog("Train is "+train_eta+" Minutes Away From "+train_info.get("target_station"),
                                                                "You are "+late_amount+" Minutes Late.");
                                                        Log.e("Update", "Yellow!!!!");
                                                        Log.e("bool", yel[0]+"");
                                                        yel[0] = true;
                                                    }
                                                    else{
                                                        Log.e("bool", yel[0]+"");
                                                        Log.e("Update", "notified for yellow");

                                                    }


                                                }else if (late_amount >=4){

                                                    if (!pink[0] && train_eta!=0){
                                                        notificationBuilder.notificationDialog("Train is "+train_eta+" Minutes Away From "+train_info.get("target_station"),
                                                                "You are "+late_amount+" Minutes Late. You May Miss this Train!");
                                                        Log.e("Update", "Pink!!!!");
                                                        Log.e("bool", pink[0]+"");
                                                        pink[0] = true;
                                                    }
                                                    else{
                                                        Log.e("bool", pink[0]+"");
                                                        Log.e("Update", "notified for Pink");

                                                    }
                                                }
                                            }

                                    }
                                    }

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
