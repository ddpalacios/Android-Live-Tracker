package com.example.cta_map.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.cta_map.Displayers.Chicago_Transits;

public class MainPlaceHolder_Fragment extends Fragment implements Serializable  {
    ViewHolder_Adapter ViewHolder_Adapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;

    /*
    This is our viewpager - this sets up our tabbed layout which is held using our Viewholder adapter


     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tab_layout);
        ViewHolder_Adapter = new ViewHolder_Adapter(this);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(ViewHolder_Adapter);
        viewPager.setCurrentItem(3);
        Message message = MainActivity.message;
        FloatingActionButton switch_dir = view.findViewById(R.id.switch_dir_button);
        FloatingActionButton addStation = view.findViewById(R.id.AddStationFloatingButton);

        addStation.setOnClickListener(v -> {
            Chicago_Transits chicago_transits = new Chicago_Transits();
            chicago_transits.StopThreads(message, MainActivity.context);
            Intent intent = new Intent(MainActivity.context1, ChooseTrainLineActivity.class);
            startActivity(intent);
        });

        switch_dir.setOnClickListener(v -> {
            MainActivity.bar.setTitle("Switching Directions...");
            message.getT1().interrupt();
            String dir = message.getDir();
            message.setGreenNotified(false);
            message.setYellowNotified(false);
            message.setRedNotified(false);
            message.setApproachingNotified(false);
            if (dir !=null) {
                if (dir.equals("1")) {
                    message.setDir("5");

                } else {
                    message.setDir("1");
                }
            }


        });



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0){
                tab.setText("Info");
            }
            else if(position == 1)
                tab.setText("Status");
            else if (position ==2) {
                tab.setText("ETA");
            }else if (position == 3){
                tab.setText("Home");
            }else{
                tab.setText("Alarm");
            }

        }).attach();

    }

}
