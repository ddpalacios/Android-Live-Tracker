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

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MyViewHolder> {
    Context context;
    ArrayList<Stations> stations;
    public StationAdapter(Context context, ArrayList<Stations> stations){
        this.stations = stations;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.row, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Stations station = this.stations.get(position);
        holder.title.setText(station.getName());
        holder.image.setImageResource(R.drawable.red);

        holder.track_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TRACK", "Track clicked "+" Data: "+station.getName());
            }
        });

        holder.station_dir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DIR", "DIR clicked "+" Data: "+station.getName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.stations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        Button track_btn;
        Button station_dir_btn;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.imageView3);
            station_dir_btn = (Button) itemView.findViewById(R.id.dir);
            track_btn = (Button) itemView.findViewById(R.id.track);



        }

    }
}
