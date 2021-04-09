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
        Fragment fragment1 = new MapView_Fragment();
        Fragment fragment2 = new AllStationView_Fragment();
        Fragment fragment3 = new Alarms_Fragment();
        Fragment fragment4 = new TrainTimes_Fragment();
        if (position ==0){
            return fragment1;
        }else if (position ==1){
            return fragment4;
        }else if (position == 2){
            return fragment2;
        }else{
            return fragment3;
        }

    }

    @Override
    public int getItemCount() {
        return TAB_PAGES;
    }
}
