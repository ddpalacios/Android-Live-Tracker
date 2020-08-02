package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BottomViewHolder holder, int position) {
            Train_info train = this.list.get(position);
            holder.next_stop_name.setText(train.getNext_stop_name());
            holder.next_stop_eta_txt.setText(train.getNext_stop_eta());
            holder.next_stop_distance.setText(train.getNext_stop_dist());
            Log.e("ddd", train.getTrain_id()+" ");
            holder.train_id_txt.setText(train.getTrain_id()+" ");
//            holder.target_eta_txt.setText(train.getTarget_eta());

//            holder.target_distance_txt.setText(train.getTarget_distance());
//            holder.main_station_txt.setText(train.getTarget_name());
//            holder.train_id_txt.setText(train.getTrain_id());
//
//            holder.notifySwitch.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int user_eta = 5;
//                    if (5 <= 15){
//                        int time_to_spare = 10-5;
//                        if (time_to_spare == 0){
//                            Log.e("Leave now", "Leave now before you are late");
//                        }else{
//                            Log.e("time to spare", "You have "+ time_to_spare+" time to spare");
//                        }
//                    }else{
//                        int late = (5- 10) *-1;
//                        Log.e("LATE","You are "+late+" m Late, Check out next trains or try to make this one.");
//                    }
//
//
//
//                }
//            });
//
//
//


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
//            target_eta_txt = (TextView)itemView.findViewById(R.id.target_eta_txt);
//            target_distance_txt = (TextView)itemView.findViewById(R.id.target_distance_txt);
//            main_station_txt = (TextView) itemView.findViewById(R.id.main_station_txt);
            train_id_txt = (TextView) itemView.findViewById(R.id.train_id_txt);
//            notifySwitch = (Switch) itemView.findViewById(R.id.notify_switch);
        }
    }


}
