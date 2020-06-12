package com.example.cta_map.Displayers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void display_marker_boundries(Intent intent, Context context, int current_train_eta, int user_to_target_eta, HashMap<String, String> train_info, String station_type, int track_from, int track_to){
        if (current_train_eta >=track_from && current_train_eta <=track_to){
            if (user_to_target_eta <= current_train_eta){
                Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), "green", 1f);
                int minutes_to_spare = current_train_eta - user_to_target_eta;
            }else if (user_to_target_eta > current_train_eta){
                int late_amount = user_to_target_eta - current_train_eta;
                if (late_amount >=0 && late_amount <4 ){
                    Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), "yellow", 1f);


                }else if (late_amount >=4){
                    Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), "pink", 1f);

                }
            }
        }else{

            Marker train_marker = addMarker(train_info.get("train_lat"), train_info.get("train_lon"), train_info.get("next_stop"), station_type, 1f);
        }


    }




}
