package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.LauncherActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;


// Instances of this class are fragments representing a single
// object in our collection.
public class MapView_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private FragmentManager context;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_view_frag1_layout, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<ListItem> arrayList = new ArrayList<>();
        for (int i=0; i<100; i++){
            ListItem listItem = new ListItem();
            listItem.setTitle("Item: "+i);
            arrayList.add(listItem);
        }
        recyclerView.setAdapter(new RecyclerView_Adapter_frag1(this.context,arrayList));

    }




}
