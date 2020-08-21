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


    public Marker addMarker(String lat, String lon, String title, String color, Float alpha){
        float opacity = alpha;
        HashMap<String, Integer> colors = new HashMap<>();
        colors.put("blue", R.drawable.blue);
        colors.put("purple", R.drawable.purple);
        colors.put("pink", R.drawable.pink);
        colors.put("green", R.drawable.green);
        colors.put("brown",R.drawable.brown);
        colors.put("orange", R.drawable.orange);
        colors.put("red", R.drawable.red);
        colors.put("target",R.drawable.target);
        colors.put("yellow", R.drawable.yellow);
        LatLng train_marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.blue);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);


        return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(title).icon(smallMarkerIcon));
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
