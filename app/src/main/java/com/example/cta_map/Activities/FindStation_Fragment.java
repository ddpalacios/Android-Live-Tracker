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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;


// Instances of this class are fragments representing a single
// object in our collection.
public class FindStation_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private FragmentActivity myContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity){
            myContext= (FragmentActivity) context;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.pick_train_line_frag3_layout, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<ListItem> arrayList = new ArrayList<>();
        int[] images = {
                R.drawable.red,
                R.drawable.blue,
                R.drawable.brown,
                R.drawable.green,
                R.drawable.orange,
                R.drawable.purple,
                R.drawable.pink,
                R.drawable.yellow
        };


        String[] line_names = {"RED", "BLUE", "BROWN", "GREEN", "ORANGE", "PURPLE", "PINK", "YELLOW"};
        for (int i=0; i<images.length; i++){
            ListItem listItem = new ListItem();
            listItem.setImage(images[i]);
            listItem.setTitle(line_names[i]);

            arrayList.add(listItem);
        }
        FragmentManager fragManager = myContext.getSupportFragmentManager();
        HashMap<String, String> tracking_station = new HashMap<>();

        recyclerView.setAdapter(new RecyclerView_Adapter_frag3(tracking_station, fragManager, arrayList));

    }




}
