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

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MapView_Fragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Train> current_incoming_trains;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_view_frag1_layout, container, false);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Message message = ((MainActivity)getActivity()).message;
        current_incoming_trains = message.getOld_trains();
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<ListItem> arrayList = new ArrayList<>();
        if (current_incoming_trains!=null) {
            for (int i = 0; i < current_incoming_trains.size(); i++) {
                Train current_live_train =current_incoming_trains.get(i);
                ListItem listItem = new ListItem();
                listItem.setTitle("#"+ current_live_train.getRn());
                listItem.setSubtitle( current_live_train.getStatus());
                listItem.setImage(new Chicago_Transits().getTrainImage(current_live_train.getTrain_type()));
                listItem.setLat(current_live_train.getLat());
                listItem.setLon(current_live_train.getLon());
                arrayList.add(listItem);
            }
            recyclerView.setAdapter(new RecyclerView_Adapter_frag1(arrayList));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
