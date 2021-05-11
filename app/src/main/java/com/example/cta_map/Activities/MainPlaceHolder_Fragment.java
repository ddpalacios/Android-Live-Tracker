package com.example.cta_map.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cta_map.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.HashMap;

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
        viewPager.setCurrentItem(2);

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
            if(position == 0)
                tab.setText("Map");
            else if (position ==1) {
                tab.setText("ETA");
            }else if (position == 2){
                tab.setText("Stations");
            }else{
                tab.setText("Alarms");
            }

        }).attach();

    }

}
