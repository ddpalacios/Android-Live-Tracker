package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TrainTimes_Fragment extends Fragment {
    RecyclerView recyclerView;
    TrainTimes_Adapter_frag trainTimes_adapter_frag;
    Message message;
    ArrayList<Train> current_incoming_trains;

    private FragmentActivity myContext;
    private Context main_context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity){
            myContext= (FragmentActivity) context;
            main_context = context;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.train_times_frag_layout, container, false);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        message = ((MainActivity)getActivity()).message;

        Context context = ((MainActivity)getActivity()).context;
        FloatingActionButton switch_dir = view.findViewById(R.id.switch_dir_button);
        current_incoming_trains = message.getOld_trains();
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        GoogleMap mMap = ((MainActivity)getActivity()).mMap;
        if (current_incoming_trains!=null) {
            trainTimes_adapter_frag = new TrainTimes_Adapter_frag(context,message,current_incoming_trains, mMap);
            recyclerView.setAdapter(trainTimes_adapter_frag);


        }
        switch_dir.setOnClickListener(v -> {
            String dir = message.getDir();
            if (dir !=null) {
                if (dir.equals("1")) {
                    message.setDir("5");

                } else {
                    message.setDir("1");
                }
                message.getT1().interrupt();
            }


        });



    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
