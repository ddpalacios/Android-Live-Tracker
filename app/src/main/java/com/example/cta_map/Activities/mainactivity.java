package com.example.cta_map.Activities;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.NotificationBuilder;
import com.example.cta_map.FavStationAdapter;
import com.example.cta_map.R;
import com.example.cta_map.Stops;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class mainactivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());

        ArrayList<Object> favorite_stations = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", null, null);
        ArrayList<Stops> list = new ArrayList<>();
        list.add(new Stops(null, "Choose Station", "choose_station", null));
        list.add(new Stops(null, "To Maps", "to_maps", null));
        list.add(new Stops(null, "View Data", "data_view", null));
        list.add(new Stops(null, "Favorites: ", "favorite", null));
        if (favorite_stations != null) {
            if (favorite_stations.size() > 0) {
                for (int i = 0; i < favorite_stations.size(); i++) {
                    HashMap<String,String> favorite_station = (HashMap<String,String>) favorite_stations.get(i);
                    list.add(new Stops(null, favorite_station.get("STATION_NAME"), favorite_station.get("STATION_TYPE"), null));
                }
            }
        }
        RecyclerView rv = findViewById(R.id.rv_user_favorites);
        FavStationAdapter adapter = new FavStationAdapter(getApplicationContext(), list);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final Button notify = findViewById(R.id.notify);

        notify.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent notificationIntent = new Intent(getApplicationContext() ,mainactivity.class);
                NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext(), notificationIntent );
                notificationBuilder.notificationDialog("CTA_map", "New Message");
            }
        });









    }
}