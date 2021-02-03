package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.ChooseLineActivity;
import com.example.cta_map.Activities.MapsActivity;
import com.example.cta_map.Activities.TrainTrackingActivity;
import com.example.cta_map.Activities.ViewDataActivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;

import java.util.ArrayList;
import java.util.HashMap;

public class FavStationAdapter extends RecyclerView.Adapter<FavStationAdapter.LineViewHolder>{
    ArrayList<Stops> lines;
    Context context;
    public FavStationAdapter(Context context, ArrayList<Stops> lines){
        this.lines = lines;
        this.context = context;
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.linelist_original, parent, false);

        return new FavStationAdapter.LineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LineViewHolder holder, int position) {
            final Stops lines = this.lines.get(position);
            HashMap<String, Integer> images =  new HashMap<>();
                images.put("red", R.drawable.red);
                images.put("blue",R.drawable.blue);
                images.put("brown", R.drawable.brown);
                images.put("green",R.drawable.green);
                images.put("orange", R.drawable.orange);
                images.put("purple",R.drawable.purple);
                images.put("pink",R.drawable.pink);
                images.put("choose_station", R.drawable.mainstation);
                images.put("data_view", R.drawable.data_view);
                images.put("favorite", R.drawable.ic_launcher_foreground);
                images.put("to_maps", R.drawable.map);
                images.put("yellow", R.drawable.yellow);
            holder.t1.setText(lines.getName());
            holder.img1.setImageResource(images.get(lines.getColor().toLowerCase()));
            final Context ctx = this.context;
            holder.t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> tracking_station = new HashMap<>();
                    if (lines.getName().equals("Choose Station")){
                        Intent intent = new Intent(ctx, ChooseLineActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }
                    else if (lines.getName().equals("View Data")){
                        Intent intent = new Intent(ctx, ViewDataActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }
                    else if (lines.getName().equals("To Maps")){
                        Intent intent = new Intent(ctx, MapsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        CTA_DataBase cta_dataBase = new CTA_DataBase(ctx);
//                        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS","MAP_ID = '41450'", null,null);
//                        HashMap<String, String> default_tracking =(HashMap<String, String>) record.get(0);
//                        tracking_station = new HashMap<>();
//                        tracking_station.put("station_dir", "1");
//                        tracking_station.put("target_station_name", default_tracking.get("STATION_NAME"));
//                        tracking_station.put("station_type", "blue");
//                        intent.putExtra("tracking_station", tracking_station);
                        ctx.startActivity(intent);
                    }else if (!lines.getName().equals("Favorites")){
                        Intent intent = new Intent(ctx, TabbedMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Chicago_Transits chicago_transits = new Chicago_Transits();
                        CTA_DataBase cta_dataBase = new CTA_DataBase(ctx);
                        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+lines.getName()+"' AND "+chicago_transits.TrainLineKeys(lines.getColor().toLowerCase()).toUpperCase() +" = '1'", null,null);
                        if (lines.getColor().toLowerCase().equals("purple" )){
                            record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+lines.getName()+"' AND "+chicago_transits.TrainLineKeys(lines.getColor().toLowerCase()).toUpperCase() +" = '1'", null,null);
                            if (record == null){
                                record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+lines.getName()+"' AND PEXP = '1'", null,null);

                            }
                        }
                        HashMap<String,String> target_station_record  = (HashMap<String, String>) record.get(0);
                        tracking_station.put("target_station_name", target_station_record.get("STATION_NAME"));
                        tracking_station.put("station_type", lines.getColor());
                        tracking_station.put("station_id", target_station_record.get("STOP_ID"));
                        tracking_station.put("station_dir", "1");

                        intent.putExtra("tracking_station", tracking_station);
                        ctx.startActivity(intent);


                    }



//                    tracking_station.put("station_type",lines.getLine());
//                    intent.putExtra("tracking_station", (Serializable) tracking_station);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    ctx.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return lines.size();
    }


    public class LineViewHolder extends RecyclerView.ViewHolder {
        TextView t1;
        ImageView img1;
        public LineViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.train_line);
            img1 = (ImageView) itemView.findViewById(R.id.train_image);


        }
    }
}
