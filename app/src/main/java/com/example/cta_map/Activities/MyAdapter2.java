package com.example.cta_map.Activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.R;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {
    String[] name;
    Context context;
    public MyAdapter2(Context ctx , String[] name){
        this.name = name;
        this.context = ctx;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.my_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.t1.setText(this.name[position]);
        holder.mylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ff", "clicked");

            }
        });
//        holder.mylayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("ddd", "data "+ name[position]);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return name.length;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView t1;
        ConstraintLayout mylayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            t1 = itemView.findViewById(R.id.station_name);
            mylayout = itemView.findViewById(R.id.train_station_layout);





        }
    }
}
