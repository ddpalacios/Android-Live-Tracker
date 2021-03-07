package com.example.cta_map.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.ChooseStationActivity;
import com.example.cta_map.Activities.ChooseTrainDirectionActivity;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TrainDirAdapter extends RecyclerView.Adapter<TrainDirAdapter.ItemHolder>  {
    ArrayList<ListItem> contactsList;
    Context context;
    HashMap<String, String> tracking_station;
    public TrainDirAdapter(Context context, ArrayList<ListItem> contactsList, HashMap<String, String> tracking_station){
        this.contactsList = contactsList;
        this.context = context;
        this.tracking_station = tracking_station;
    }

    @NonNull
    @Override
    public TrainDirAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tracking_station_card_view, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainDirAdapter.ItemHolder holder, int position) {
        final ListItem listItem = this.contactsList.get(position);
        holder.t1.setText(listItem.getTitle());
        holder.imageView.setImageResource(listItem.getImage());
        holder.item.setOnClickListener(v -> {
                    this.tracking_station.put("train_dir", listItem.getTrain_dir());
                    this.tracking_station.put("main_station_name", listItem.getTitle());
                    Intent intent = new Intent(this.context, ChooseStationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("tracking_station", this.tracking_station);
                    this.context.startActivity(intent);
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

