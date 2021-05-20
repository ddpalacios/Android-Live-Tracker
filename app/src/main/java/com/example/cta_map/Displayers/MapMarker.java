package com.example.cta_map.Displayers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//import com.example.cta_map.Activities.Navigation.MapsActivity;
import com.example.cta_map.Activities.Classes.Station;
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

    private  Integer getTrainMarkerColor(String status){

        if (status.toLowerCase().equals("p")){ // You should never use the actual code as a variable.
            // Make it easy for yourself and just use the actual name while decoding it
            status= "purple";

        }else if(status.toLowerCase().equals("org")){
            status = "orange";

        }else if(status.toLowerCase().equals("y")){
            status= "yellow";

        }else if(status.toLowerCase().equals("g")){
            status = "green";
        }else if(status.toLowerCase().equals("brn")){
            status = "brown";

        }
        HashMap<String, Integer> train_colors = new HashMap<>();
        train_colors.put("blue", (int) BitmapDescriptorFactory.HUE_BLUE);
        train_colors.put("purple", (int) BitmapDescriptorFactory.HUE_VIOLET);
        train_colors.put("pink", (int) BitmapDescriptorFactory.HUE_ROSE);
        train_colors.put("green", (int) BitmapDescriptorFactory.HUE_GREEN);
        train_colors.put("brown",(int) BitmapDescriptorFactory.HUE_CYAN);
        train_colors.put("orange", (int) BitmapDescriptorFactory.HUE_ORANGE);
        train_colors.put("red", (int) BitmapDescriptorFactory.HUE_RED);
        train_colors.put("yellow", (int) BitmapDescriptorFactory.HUE_YELLOW);


        return train_colors.get(status.toLowerCase());
    }



    public Marker addMarker(Train train, Station station, String snippet, Float opacity, String main_title) {
        if (train !=null && train.getIsSch()){ // Trains that are SCHEDULED do not have Lat and Lon coordinates
            return null;
        }

        String station_line = ((train == null) ?null : train.getRt());
        Bitmap b;
        int height = 140;
        int width = 140;
        if (station_line!=null) {
            b = BitmapFactory.decodeResource(this.context.getResources(), new Chicago_Transits().getTrainImage(station_line));
        }else{
            b = BitmapFactory.decodeResource(this.context.getResources(), new Chicago_Transits().getTrainImage("target"));
        }
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        if (train!=null){
            String TRAIN_ID = snippet.replaceAll("Train#", "");
            LatLng train_marker = new LatLng(train.getLat(), train.getLon());


            if (train.getIsNotified() && train.getSelected()){
                // if this is the train that is being notified - use icon
                return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet(snippet).icon(smallMarkerIcon));
            }else{
                // otherwise, plot regular marker
                Integer train_color = getTrainMarkerColor(station_line);
                return this.mMap.addMarker(new MarkerOptions().position(train_marker).title(main_title).snippet("Train# " + TRAIN_ID).icon(BitmapDescriptorFactory.defaultMarker(train_color)).alpha(opacity));
            }

        }else if (station!=null){
            LatLng station_marker = new LatLng(station.getLat(), station.getLon());
            return this.mMap.addMarker(new MarkerOptions().position(station_marker).title(main_title).snippet(snippet).icon(smallMarkerIcon));

//            if (!station.getIsTarget()){
//            return this.mMap.addMarker(new MarkerOptions().position(station_marker).title(main_title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(getTrainMarkerColor(line))).alpha(opacity));
//            }else{
            //            }

        }else{
            return null;
        }
    }

}
