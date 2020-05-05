package com.example.cta_map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

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

        mMap = googleMap;
        EditText station_name = (EditText) findViewById(R.id.station_name);
        Bundle bb;
        bb=getIntent().getExtras();
        assert bb != null;
        String [] station_coordinates = bb.getStringArray("station_coordinates");
        String train_dir = bb.getString("train_direction");






        // Add a marker in Sydney and move the camera
        assert station_coordinates != null;
        LatLng sydney = new LatLng(Double.parseDouble(station_coordinates[0]), Double.parseDouble(station_coordinates[1]));
        mMap.addMarker(new MarkerOptions().position(sydney).title("GRANVILLE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }
}
