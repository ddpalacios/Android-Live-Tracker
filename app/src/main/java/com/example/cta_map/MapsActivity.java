package com.example.cta_map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;





public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private EditText station_name, station_type, direction;
    DatabaseHelper myDb;


    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);









    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myDb = new DatabaseHelper(this);
        final Cursor res = myDb.getAllData();
        while (res.moveToNext()){
            mMap = googleMap;
            Bundle bb;
            bb=getIntent().getExtras();
            assert bb != null;
            String [] station_coordinates = bb.getStringArray("station_coordinates");
            String train_dir = bb.getString("train_direction");
            String station_name = bb.getString("station_name");

//            Log.e("retrieved", res.getString(1) +","+ res.getString(2));
            // Add a marker in Sydney and move the camera
            assert station_coordinates != null;
            LatLng sydney = new LatLng(Double.parseDouble(res.getString(1)), Double.parseDouble(res.getString(2)));
            mMap.addMarker(new MarkerOptions().position(sydney).title(station_name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }












    }
}
