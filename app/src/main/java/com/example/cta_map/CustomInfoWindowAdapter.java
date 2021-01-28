package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cta_map.Displayers.Train;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
//        TextView window_title = view.findViewById(R.id.notification_title);
//        LinearLayout linearLayout = view.findViewById(R.id.main_notification_window);
//        TextView textView = new TextView(this.context);
//        textView.setTextColor(Color.BLACK);
//        textView.setTextSize(2);
//        textView.setText(next_stop);
//        linearLayout.addView(textView);

        stop_title.setText(next_stop);
        String snipper = marker.getSnippet();
        TextView subSinnet = view.findViewById(R.id.subtitle);
        subSinnet.setText(snipper);


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
