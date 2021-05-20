package com.example.cta_map.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.CustomeWindowRV_Adapter;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Optional;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private  final  View mWindow;
    private Context context;
    private Train train;
    private Boolean isTarget;
    private Message message;

    public CustomInfoWindowAdapter(Context context, Message message,Boolean isTarget) {
        this.context = context;
        this.isTarget = isTarget;
        this.message = message;
        this.mWindow = LayoutInflater.from(this.context).inflate(R.layout.custom_info_window, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void renderWindowText(Marker marker, View view){
        RecyclerView recyclerView = view.findViewById(R.id.remaining_stops_rv);
        ArrayList<Train> all_trains = this.message.getOld_trains();
        TextView rn_textView = view.findViewById(R.id.train_rn);


        String rn = marker.getTitle();
        Train found_train = null;
        if (all_trains != null) {
            for (Train train : all_trains) {
                if (train.getRn().equals(rn)) {
                    found_train = train;
                    break;
                }
            }
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if (found_train!=null){
            rn_textView.setText("Run# "+ found_train.getRn());
            ArrayList<TrainStops> remaining_stops = found_train.getRemaining_stops();
            ArrayList<TrainStops> inDisplay = new ArrayList<>();
            int count =0;
            if (remaining_stops.size() > 0){
                for (TrainStops stop : remaining_stops){
                    if (stop.getStaId().equals(found_train.getTarget_id())){
                        inDisplay.add(stop);
                        break;
                    }
                    if (count > 3){
                        break;
                    }
                    inDisplay.add(stop);
                    count+=1;
                }
                // populating our recycler view (CUSTOM WINDOW)
                CustomeWindowRV_Adapter customeWindowRV_adapter = new CustomeWindowRV_Adapter(context, message, inDisplay);
                recyclerView.setAdapter(customeWindowRV_adapter);


            }else{
                // populating our recycler view (CUSTOM WINDOW)
                CustomeWindowRV_Adapter customeWindowRV_adapter = new CustomeWindowRV_Adapter(context, message, null);
                recyclerView.setAdapter(customeWindowRV_adapter);
            }




        }

//        TextView stop_title = view.findViewById(R.id.nextStop);
//        stop_title.setText(next_stop);
//        if (!stop_title.getText().toString().equals("Target")) {
//            String snippet = marker.getSnippet();
//            try {
//                String MAP_ID = StringUtils.substringBetween(snippet, "StationID#", "Train").trim();
//                String TRAIN_ID = StringUtils.substringBetween(snippet, "Train#", ".").trim();
//                TextView subSinnet = view.findViewById(R.id.subtitle);
//                subSinnet.setText("Train# " + TRAIN_ID);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, this.mWindow);
        return mWindow;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, this.mWindow);

        return mWindow;
    }
}
