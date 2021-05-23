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
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
        Chicago_Transits chicago_transits = new  Chicago_Transits();
        TextView rn_textView = view.findViewById(R.id.train_rn);
        TextView train_status_info = view.findViewById(R.id.train_status_info);


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
            train_status_info.setText(chicago_transits.getStatusMessage(found_train.getStatus()));
            train_status_info.setTextColor(Color.parseColor(getColor(chicago_transits.TrainLineKeys(found_train.getStatus()))));



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

    private String getColor(String train_line){
        HashMap<String, String> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red","#F44336");
        TrainLineKeyCodes.put("blue","#384cff");
        TrainLineKeyCodes.put("brn", "#a34700");
        TrainLineKeyCodes.put("g", "#0B8043");
        TrainLineKeyCodes.put("org", "#ffad33");
        TrainLineKeyCodes.put("y", "#b4ba0b");
        TrainLineKeyCodes.put("gray", "#c7c7c7");

        TrainLineKeyCodes.put("pink","#ff66ed");
        TrainLineKeyCodes.put("p","#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());

    }
}
