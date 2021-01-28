package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.TrainTrackingActivity;
import com.example.cta_map.Activities.mainactivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.DataBase.Database2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {
    ArrayList<Stops> stops;
    Context context;
    public StopAdapter(Context context, ArrayList<Stops> stops){
        this.stops = stops;
        this.context = context;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.station_row, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, final int position) {
        final Stops stops = this.stops.get(position);
        HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red",R.drawable.red );
        TrainLineKeyCodes.put("blue", R.drawable.blue);
        TrainLineKeyCodes.put("brown", R.drawable.brown);
        TrainLineKeyCodes.put("green", R.drawable.green);
        TrainLineKeyCodes.put("orange", R.drawable.orange);
        TrainLineKeyCodes.put("pink", R.drawable.pink);
        TrainLineKeyCodes.put("purple", R.drawable.purple);
        TrainLineKeyCodes.put("yellow", R.drawable.yellow);

        holder.t1.setText(stops.getName());
        holder.img.setImageResource(TrainLineKeyCodes.get(stops.getColor()));
        final Context ctx = this.context;
        holder.t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> tracking_station = stops.getTracking_station();

                Intent intent;
                if (stops.getDir() == null){
                    intent = new Intent(ctx, TrainStations.class);

                    if (position == 0){
                        tracking_station.put("station_dir", "1");

                    }else{
                        tracking_station.put("station_dir", "5");
                    }

                    intent.putExtra("tracking_station", (Serializable) tracking_station);


                }else {
                    intent = new Intent(ctx, TrainTrackingActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    CTA_DataBase cta_dataBase = new CTA_DataBase(ctx);
                    Chicago_Transits chicago_transits = new Chicago_Transits();
                    ArrayList<Object> record;
                    if (stops.getName().equals("O'Hare") ){
                        stops.getName().replaceAll("'", "\'" );
                    }
                    record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '" + stops.getName() + "' AND " + chicago_transits.TrainLineKeys(stops.getColor().toLowerCase()).toUpperCase() + " = '1'", null);

                    if (stops.getColor().toLowerCase().equals("purple")) {
                        record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '" + stops.getName() + "' AND " + chicago_transits.TrainLineKeys(stops.getColor().toLowerCase()).toUpperCase() + " = '1'", null);
                        if (record == null) {
                            record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '" + stops.getName() + "' AND PEXP = '1'", null);

                        }
                    }

                    HashMap<String, String> station_record = (HashMap<String, String>) record.get(0);

                    tracking_station.put("target_station_name", stops.getName());
                    tracking_station.put("station_id", station_record.get("STOP_ID"));
                    intent.putExtra("tracking_station", (Serializable) tracking_station);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
//                String target_station_id;
//                switch (stops.getName()) {
//                    case "O'Hare":
//                        target_station_id = "30171";
//                        break;
//                    case "Harlem (O'Hare Branch)":
//                        target_station_id = "30145";
//                        break;
//                    case "Western (O'Hare Branch)":
//                        target_station_id = "30130";
//                        break;
//                    default:
//                        CTA_DataBase cta_dataBase = new CTA_DataBase(ctx);
//                        ArrayList<Object> found_station = cta_dataBase.excecuteQuery("MAP_ID", "cta_stops",
//                                        "station_name = '"+stops.getName()+"' AND "+stops.getColor()+" = 'TRUE'",null);
//                        HashMap<String,String> found = (HashMap<String, String>) found_station.get(0);
//                        target_station_id = found.get("MAP_ID");
//                        cta_dataBase.close();
//
//                }


//                intent.putExtra("target_station_id", target_station_id);
//                intent.putExtra("target_station_name", stops.getName());
//                intent.putExtra("target_station_type", stops.getColor());
//                intent.putExtra("target_station_dir", stops.getDir());
//
//



//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                String station_name = stops.getName();
//                Database2 sqlite = new Database2(ctx);
//                String station_type = stops.getColor();
//                String station_dir = stops.getDir();
//                String query = "SELECT station_id FROM cta_stops WHERE station_name = '"+station_name+"' AND "+station_type+" ='true'";
//                String station_id = sqlite.getValue(query);
//
////                sqlite.addNewStation(station_name, station_type, Integer.parseInt(station_dir), Integer.parseInt(station_id));
//
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.stops.size();
    }

    public class StopViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView t1;

        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.station_img);
            t1 = (TextView) itemView.findViewById(R.id.station_name);
        }
    }
}
