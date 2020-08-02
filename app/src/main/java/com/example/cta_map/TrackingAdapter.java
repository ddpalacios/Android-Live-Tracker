package com.example.cta_map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder> {
    ArrayList<Object> list;
    Context ctx;
    public TrackingAdapter(Context ctx, ArrayList<Object> list){
        this.ctx = ctx;
        this.list = list;
    }


    @NonNull
    @Override
    public TrackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.ctx);
        View view = inflater.inflate(R.layout.tracking_row, parent, false);

        return new TrackingAdapter.TrackingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingViewHolder holder, int position) {
        Tracking_Station train = (Tracking_Station) this.list.get(position);
        holder.min.setText(train.getEta()+"m");
        holder.title.setText(train.getName());
//        Log.e("fff", train.getName()+"fff");
//        Log.e("fff", train.getEta()+"fff");

//        holder.title.setText(train.getName()+"");
//        holder.min.setText(train.getEta()+"m");

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class TrackingViewHolder extends RecyclerView.ViewHolder {
    TextView title, min;

    public TrackingViewHolder(@NonNull View itemView){
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.target_station_name);
        min = (TextView) itemView.findViewById(R.id.target_station_eta);





    }

}
}
