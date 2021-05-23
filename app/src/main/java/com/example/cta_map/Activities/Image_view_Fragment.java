package com.example.cta_map.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Image_view_Fragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList< UserSettings> current_incoming_trains;
    SettingsView_Adapter_frag mapViewAdapter;
    public  static String STATIONS_ITEM = "Stations";
    public static String MINUTES_ITEM = "Minutes";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.train_times_frag_layout, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            RecyclerView recyclerView = view.findViewById(R.id.frag_rv);
//            recyclerView.setVisibility(View.GONE);
//            ImageView image = view.findViewById(R.id.LImage);
//            image.setVisibility(View.VISIBLE);
//            TextView main_title = view.findViewById(R.id.main_title);
//            main_title.setText("‘L’ system map");


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
