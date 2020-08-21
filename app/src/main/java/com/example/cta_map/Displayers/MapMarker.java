package com.example.cta_map.Displayers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;

public class MapMarker {
    GoogleMap mMap;
    Context context;
    public MapMarker(GoogleMap mMap, Context context){
        this.mMap = mMap;
        this.context = context;
    }


    public Marker addMarker(String lat, String lon, String title, String color, Float alpha, boolean isStation) {
        float opacity = alpha;
        HashMap<String, Integer> colors = new HashMap<>();
        colors.put("blue", R.drawable.blue);
        colors.put("purple", R.drawable.purple);
        colors.put("pink", R.drawable.pink);
        colors.put("green", R.drawable.green);
        colors.put("brown", R.drawable.brown);
        colors.put("orange", R.drawable.orange);
        colors.put("red", R.drawable.red);
        colors.put("target", R.drawable.target);
        colors.put("yellow", R.drawable.yellow);
        LatLng train_marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        int height = 120;
        int width = 120;
        Bitmap b = BitmapFactory.decodeResource(this.context.getResources(), colors.get(color));
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        if (isStation) {
            return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).alpha(opacity));
        }

        return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(smallMarkerIcon));



    }




}
