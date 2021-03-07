package com.example.cta_map.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang3.StringUtils;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private  final  View mWindow;
    private Context context;
    private Train train;
    private String main_title;

    public CustomInfoWindowAdapter(Context context, Train train, String main_title) {
        this.context = context;
        this.main_title = main_title;
        this.train = train;
        this.mWindow = LayoutInflater.from(this.context).inflate(R.layout.custom_info_window, null);
    }

    @SuppressLint("SetTextI18n")
    private void renderWindowText(Marker marker, View view){
        String next_stop = marker.getTitle();
        TextView stop_title = view.findViewById(R.id.nextStop);
        stop_title.setText(next_stop);
        if (!stop_title.getText().toString().equals("Target")) {
            String snippet = marker.getSnippet();
            try {
                String MAP_ID = StringUtils.substringBetween(snippet, "StationID#", "Train").trim();
                String TRAIN_ID = StringUtils.substringBetween(snippet, "Train#", ".").trim();
                TextView subSinnet = view.findViewById(R.id.subtitle);
                subSinnet.setText("Train# " + TRAIN_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, this.mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, this.mWindow);

        return mWindow;
    }
}
