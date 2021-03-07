package com.example.cta_map.Displayers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//import com.example.cta_map.Activities.Navigation.MapsActivity;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapMarker  {
    GoogleMap mMap;
    Context context;
    Message message;
    public MapMarker(GoogleMap mMap, Context context, Message message){
        this.mMap = mMap;
        this.message = message;
        this.context = context;
    }


    public Marker addMarker(Double lat, Double lon, String snippet,String color, Float alpha, boolean isTrain, boolean isTarget, boolean isStation, String main_title, Boolean UseIcon) {
        float opacity = alpha;
        HashMap<String, Integer> colors = new HashMap<>();
        colors.put("blue", R.drawable.blue);
        colors.put("purple", R.drawable.purple);
        colors.put("pink", R.drawable.pink);
        colors.put("main", R.drawable.mainstation);
        colors.put("green", R.drawable.green);
        colors.put("brown", R.drawable.brown);
        colors.put("orange", R.drawable.orange);
        colors.put("red", R.drawable.red);
        colors.put("target", R.drawable.target);
        colors.put("yellow", R.drawable.yellow);
        LatLng train_marker = new LatLng(lat, lon);
        int height = 140;
        int width = 140;
        Bitmap b = BitmapFactory.decodeResource(this.context.getResources(), colors.get(color));
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        HashMap<String, Integer> train_colors = new HashMap<>();
        train_colors.put("blue", (int) BitmapDescriptorFactory.HUE_BLUE);
        train_colors.put("purple", (int) BitmapDescriptorFactory.HUE_VIOLET);
        train_colors.put("pink", (int) BitmapDescriptorFactory.HUE_ROSE);
        train_colors.put("green", (int) BitmapDescriptorFactory.HUE_GREEN);
        train_colors.put("brown",(int) BitmapDescriptorFactory.HUE_CYAN);
        train_colors.put("orange", (int) BitmapDescriptorFactory.HUE_ORANGE);
        train_colors.put("red", (int) BitmapDescriptorFactory.HUE_RED);
        train_colors.put("yellow", (int) BitmapDescriptorFactory.HUE_YELLOW);

        if (isTrain) {

            try {
                String TRAIN_ID = snippet.replaceAll("Train#", "");

                if (!this.message.getSendingNotifications()) {
                    return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet("Train# " + TRAIN_ID).icon(BitmapDescriptorFactory.defaultMarker(train_colors.get(color.toLowerCase()))).alpha(opacity));
                }else{
                    if (!UseIcon){
                        return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet("Train# "+TRAIN_ID).icon(BitmapDescriptorFactory.defaultMarker(train_colors.get(color.toLowerCase()))).alpha(opacity));
                    }else {
                        return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet(snippet).icon(smallMarkerIcon));
                    }
                    }
            }catch (Exception e){
                e.printStackTrace();
                return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(train_colors.get(color.toLowerCase()))).alpha(opacity));

            }
        }

        else if (isTarget) {
            return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet(snippet).icon(smallMarkerIcon));
        }
        else if (isStation){
            return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(train_colors.get(color.toLowerCase()))).alpha(opacity));
        }
        else {
            return null;
        }
    }




}
