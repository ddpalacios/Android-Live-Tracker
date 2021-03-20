package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.RecyclerView_Adapter_frag1;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainTimes_Fragment extends Fragment {
    ArrayList<ListItem> arrayList;
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
        current_incoming_trains = message.getIncoming_trains();
        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
         arrayList = new ArrayList<>();
        if (current_incoming_trains!=null) {
            for (int i = 0; i < current_incoming_trains.size(); i++) {
                Train current_live_train =current_incoming_trains.get(i);
                ListItem listItem = new ListItem();
                listItem.setTitle("RN# "+current_live_train.getRn()+" ETA: " + current_live_train.getTarget_eta()+"m");

                arrayList.add(listItem);
            }
            trainTimes_adapter_frag = new TrainTimes_Adapter_frag(arrayList);
            recyclerView.setAdapter(trainTimes_adapter_frag);


        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
