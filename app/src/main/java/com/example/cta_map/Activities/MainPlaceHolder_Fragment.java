package com.example.cta_map.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cta_map.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainPlaceHolder_Fragment extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    ViewHolder_Adapter ViewHolder_Adapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    FragmentManager context;

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
        ViewHolder_Adapter = new ViewHolder_Adapter(this);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(ViewHolder_Adapter);
        tabLayout = view.findViewById(R.id.tab_layout);
        Button back_button = view.findViewById(R.id.back_button);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0 || tab.getPosition()==1){
                    back_button.setVisibility(View.GONE);
                }else{
                    back_button.setVisibility(View.VISIBLE);

                }
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
                tab.setText("Stations");
            }else{
                tab.setText("Find Station");
            }

        }).attach();


    }
}
