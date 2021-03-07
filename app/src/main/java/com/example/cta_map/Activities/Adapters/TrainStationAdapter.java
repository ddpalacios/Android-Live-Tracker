package com.example.cta_map.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.ChooseStationActivity;
import com.example.cta_map.Activities.Classes.FavoriteStation;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TrainStationAdapter extends RecyclerView.Adapter<TrainStationAdapter.ItemHolder>  {
    ArrayList<ListItem> contactsList;
    Context context;
    HashMap<String, String> tracking_station;
    public TrainStationAdapter(Context context, ArrayList<ListItem> contactsList, HashMap<String, String> tracking_station){
        this.contactsList = contactsList;
        this.context = context;
        this.tracking_station = tracking_station;
    }

    @NonNull
    @Override
    public TrainStationAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tracking_station_card_view, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull TrainStationAdapter.ItemHolder holder, int position) {
        final ListItem listItem = this.contactsList.get(position);
        holder.t1.setText(listItem.getTitle());
        holder.imageView.setImageResource(listItem.getImage());
        holder.item.setOnClickListener(v -> {
                    this.tracking_station.put("target_station_name", listItem.getTitle());
                    this.tracking_station.put("target_map_id", listItem.getMapID());
                    CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
                    FavoriteStation favoriteStation = new FavoriteStation(listItem.getMapID(), listItem.getTitle(), tracking_station.get("train_line").toLowerCase());
                    favoriteStation.setStation_dir_label(listItem.getTrain_dir_label());
                    favoriteStation.setStation_dir(this.tracking_station.get("train_dir"));
                    ArrayList<Object> userStation = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "FAVORITE_MAP_ID = '"+listItem.getMapID()+"' AND STATION_DIR_LABEL = '"+listItem.getTrain_dir_label()+"' AND STATION_TYPE = '"+this.tracking_station.get("train_line").toLowerCase()+"'",null,null);
                    Intent intent = new Intent(this.context, MainActivity.class);

                    if (userStation!=null){
                        HashMap<String, String> station = (HashMap<String, String>) userStation.get(0);
                        if (Objects.equals(station.get("STATION_DIR_LABEL"), listItem.getTrain_dir_label())){
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.context.startActivity(intent);
                        }else{
                            cta_dataBase.commit(favoriteStation, "USER_FAVORITES");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.context.startActivity(intent);
                        }

                    }else{
                        cta_dataBase.commit(favoriteStation, "USER_FAVORITES");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.context.startActivity(intent);

                    }

        });
    }

    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1;
        CardView item;
        ImageView imageView;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.list_item);
            t1 = (TextView) itemView.findViewById(R.id.title_item);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }
}

