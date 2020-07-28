package com.example.cta_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {
    ArrayList<Stops> stops;
    public StopAdapter(Context context, ArrayList<Stops> stops){
        this.stops = stops;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.station_row, parent, false);

        return new StopViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        Stops stops = this.stops.get(position);
        holder.t1.setText(stops.getName());
        holder.img.setImageResource(stops.getColor());

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
