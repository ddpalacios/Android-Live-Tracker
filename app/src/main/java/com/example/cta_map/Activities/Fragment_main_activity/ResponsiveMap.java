package com.example.cta_map.Activities.Fragment_main_activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cta_map.Activities.Adapters.SampleViewPagerAdapter;
import com.example.cta_map.R;
import com.google.android.material.tabs.TabLayout;

public class ResponsiveMap extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private SampleViewPagerAdapter mViewPagerAdapter;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main_layout);


        mTabLayout = findViewById(R.id.sliding_tabs);
        mViewPager = findViewById(R.id.viewpager);
//        mViewPagerAdapter = new SampleViewPagerAdapter();
//        mViewPager.setAdapter(mViewPagerAdapter);
//        mTabLayout.setupWithViewPager(mViewPager);


    }

}
