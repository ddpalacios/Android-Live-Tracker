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
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class TrainTimes_Fragment extends Fragment {
    RecyclerView recyclerView;
    TrainTimes_Adapter_frag trainTimes_adapter_frag;
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
        /*
        Train times tab.
         */
        // We can grab these variables staticly because we can assume that the user is within the UI, meaning MainActivty static variables would not be null
        Message message = ((MainActivity)getActivity()).message; //get message object from running MainActivity()
        Context context = ((MainActivity)getActivity()).context;
        Fragment fragment = ((MainActivity)getActivity()).frg;


        current_incoming_trains = message.getOld_trains(); // Retrieves our most up-to-date trains

        recyclerView = view.findViewById(R.id.frag_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setClickable(true);

        FloatingActionButton switch_dir = view.findViewById(R.id.switch_dir_button);
        switch_dir.setOnClickListener(v -> {
            MainActivity.bar.setTitle("Switching Directions...");
            message.getT1().interrupt();
            String dir = message.getDir();

            message.setGreenNotified(false);
            message.setYellowNotified(false);
            message.setRedNotified(false);
            message.setApproachingNotified(false);
            if (dir !=null) {
                if (dir.equals("1")) {
                    message.setDir("5");

                } else {
                    message.setDir("1");
                }
            }


        });


        if (current_incoming_trains!=null) {
            trainTimes_adapter_frag = new TrainTimes_Adapter_frag(context,message,current_incoming_trains, null,recyclerView, null);
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
