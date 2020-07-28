package com.example.cta_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BottomTrackingAdapter extends RecyclerView.Adapter<BottomTrackingAdapter.BottomViewHolder> {
    Context context;
    ArrayList<Train_info> list;
    public BottomTrackingAdapter(Context context, ArrayList<Train_info> list)
    {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public BottomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.bottom_tracking_row, parent, false);

        return new BottomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomViewHolder holder, int position) {
            Train_info train = this.list.get(position);
            holder.next_stop_name.setText(train.getNext_stop_name());
            holder.next_stop_eta_txt.setText(train.getNext_stop_eta());
            holder.next_stop_distance.setText(train.getNext_stop_dist());

            holder.target_distance_txt.setText(train.getTarget_distance());
            holder.main_station_txt.setText(train.getTarget_name());
            holder.target_eta_txt.setText(train.getTarget_eta());
            holder.train_id_txt.setText(train.getTrain_id());





    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class BottomViewHolder extends RecyclerView.ViewHolder {
        TextView next_stop_name;
        TextView next_stop_eta_txt, target_eta_txt, target_distance_txt;
        TextView next_stop_distance, main_station_txt, train_id_txt;
        Switch notifySwitch;


        public BottomViewHolder(@NonNull View itemView) {
            super(itemView);
            next_stop_name  = (TextView) itemView.findViewById(R.id.next_stop_name);

            next_stop_distance = (TextView)itemView.findViewById(R.id.next_stop_distance);
            next_stop_eta_txt= (TextView)itemView.findViewById(R.id.next_stop_eta_txt);
            target_eta_txt = (TextView)itemView.findViewById(R.id.target_eta_txt);
            target_distance_txt = (TextView)itemView.findViewById(R.id.target_distance_txt);
            main_station_txt = (TextView) itemView.findViewById(R.id.main_station_txt);
            train_id_txt = (TextView) itemView.findViewById(R.id.train_id_txt);
            notifySwitch = (Switch) itemView.findViewById(R.id.notify_switch);
        }
    }


}
