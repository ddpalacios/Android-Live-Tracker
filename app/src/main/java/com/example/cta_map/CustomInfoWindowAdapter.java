package com.example.cta_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private  final  View mWindow;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        this.mWindow = LayoutInflater.from(this.context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView stop_title = view.findViewById(R.id.nextStop);
        if (!title.equals("")){
            stop_title.setText(title);
        }

        String snipper = marker.getSnippet();
        TextView subSinnet = view.findViewById(R.id.subtitle);
        if (!title.equals("")){
            subSinnet.setText(snipper);
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
