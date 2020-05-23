package com.example.cta_map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;

public class MapMarker {
    GoogleMap mMap;
    public MapMarker(GoogleMap mMap){
        this.mMap = mMap;
    }


    public Marker addMarker(String lat, String lon, String title, String color, Float alpha){
        float opacity = alpha;
        HashMap<String, Float> colors = new HashMap<>();
        colors.put("default", BitmapDescriptorFactory.HUE_ROSE);
        colors.put("main", BitmapDescriptorFactory.HUE_AZURE);
        colors.put("blue", BitmapDescriptorFactory.HUE_BLUE);
        colors.put("cyan", BitmapDescriptorFactory.HUE_CYAN);
        colors.put("rose", BitmapDescriptorFactory.HUE_ROSE);
        colors.put("purple", BitmapDescriptorFactory.HUE_VIOLET);
        colors.put("pink", BitmapDescriptorFactory.HUE_MAGENTA+5);
        colors.put("green", BitmapDescriptorFactory.HUE_GREEN);
        colors.put("brown",BitmapDescriptorFactory.HUE_BLUE);
        colors.put("orange", BitmapDescriptorFactory.HUE_ORANGE);
        colors.put("red", BitmapDescriptorFactory.HUE_RED);
        colors.put("yellow", BitmapDescriptorFactory.HUE_YELLOW);
        LatLng train_marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        Float TRAIN_COLOR = colors.get(color);
        assert TRAIN_COLOR != null;
        return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(BitmapDescriptorFactory.defaultMarker(TRAIN_COLOR)).alpha(opacity));
    }
}
