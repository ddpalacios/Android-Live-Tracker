package com.example.cta_map.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewHolder_Adapter extends FragmentStateAdapter {
    private Integer TAB_PAGES = 3;
    public ViewHolder_Adapter(@NonNull Fragment fragment) {
        super(fragment);

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment1 = new MapView_Fragment();
        Fragment fragment2 = new AllStationView_Fragment();
        Fragment fragment3 = new FindStation_Fragment();
        if (position ==0){
            return fragment1;
        }else if (position ==1){
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
