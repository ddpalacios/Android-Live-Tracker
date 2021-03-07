package com.example.cta_map.Activities.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cta_map.Activities.Fragment_main_activity.MapFragment;
import com.example.cta_map.Activities.Fragment_main_activity.ScrollFragment;

public class SampleViewPagerAdapter extends FragmentStateAdapter {
    public SampleViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new ScrollFragment();
            case 2:
                return new ScrollFragment();
            default:
                return new ScrollFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }



}