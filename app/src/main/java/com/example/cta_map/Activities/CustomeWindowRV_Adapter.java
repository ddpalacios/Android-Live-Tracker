package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomeWindowRV_Adapter extends RecyclerView.Adapter<CustomeWindowRV_Adapter.ItemHolder>  {
    ArrayList<TrainStops> StopsList;
    Message message;
    Context context;
    public CustomeWindowRV_Adapter(Context context, Message message, ArrayList<TrainStops> StopsList){
        this.StopsList= StopsList;
        this.context = context;
        this.message = message;
    }

    @NonNull
    @Override
    public CustomeWindowRV_Adapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.remaining_stops_card_view_layout, parent, false);
        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomeWindowRV_Adapter.ItemHolder holder, int position) {
        if (this.StopsList == null ){
            holder.main_title.setText("Unable to find predictions.");
        }else {
            TrainStops train = this.StopsList.get(position);
            holder.main_title.setText("To " + train.getStaNm());
            holder.subtitle.setText(train.getNextStopEtA() + "m");
            if (train.getNextStopEtA() < 2){
                holder.subtitle.setTextColor(Color.parseColor("#F44336"));
            }


        }

    }


    @Override
    public int getItemCount() {
        if (this.StopsList==null){
            return 0;
        }
        return this.StopsList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView main_title, subtitle;
        ImageView imageView;
        CardView item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            item = (CardView) itemView.findViewById(R.id.list_item);
            main_title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.title_eta);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }

}

