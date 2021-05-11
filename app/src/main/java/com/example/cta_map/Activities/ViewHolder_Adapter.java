package com.example.cta_map.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewHolder_Adapter extends FragmentStateAdapter {
    private Integer TAB_PAGES = 4;
    public ViewHolder_Adapter(@NonNull Fragment fragment) {
        super(fragment);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment mapView_fragment = new MapView_Fragment();
        Fragment allStationView_fragment = new AllStationView_Fragment();
        Fragment alarms_fragment = new Alarms_Fragment();
        Fragment trainTimes_fragment = new TrainTimes_Fragment();
        if (position ==0){
            return mapView_fragment;
        }else if (position ==1){
            return trainTimes_fragment;
        }else if (position == 2){
            return allStationView_fragment;
        }else{
            return alarms_fragment;
        }

    }

    @Override
    public int getItemCount() {
        return TAB_PAGES;
    }
}
