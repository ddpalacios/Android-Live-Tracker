package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;


// Instances of this class are fragments representing a single
// object in our collection.
public class TrainTracking_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private FragmentManager context;

    private FragmentActivity myContext;

    // Later fix b/c of deprecation?
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(@NonNull Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.train_tracking_layout, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<ListItem> arrayList = new ArrayList<>();
        for (int i=0; i<5; i++){
            ListItem listItem = new ListItem();
            listItem.setTitle("Train#"+i);
            listItem.setImage(R.drawable.red);
            listItem.setSubtitle(i+"m");
            arrayList.add(listItem);
        }
        FragmentManager fragManager = myContext.getSupportFragmentManager();

        recyclerView.setAdapter(new TrainTracking_Adapter_frag5(fragManager, arrayList));

    }




}
