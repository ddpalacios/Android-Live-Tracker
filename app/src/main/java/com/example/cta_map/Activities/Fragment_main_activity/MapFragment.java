package com.example.cta_map.Activities.Fragment_main_activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cta_map.Activities.Fragment_main_activity.FakeDataFragment;
import com.example.cta_map.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main_scrolling_fragment, null);
        initCollapsingToolbar(root);
        // Initialize map
        initFragment();
        return root;
    }


    private void initCollapsingToolbar(View root) {
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.myGreen));
    }

    private void initFragment() {
        FakeDataFragment fragment = new FakeDataFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }





}