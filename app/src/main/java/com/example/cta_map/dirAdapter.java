package com.example.cta_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class dirAdapter extends RecyclerView.Adapter<dirAdapter.dirViewHolder> {
        Context context;
        int[] color;
    public dirAdapter(Context context, int[] color){
        this.context = context;
        this.color = color;
    }

    @NonNull
    @Override
    public dirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dirlist, parent, false);

        return new dirViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull dirViewHolder holder, int position) {
        int color = this.color[position];
        holder.img.setImageResource(color);
    }

    @Override
    public int getItemCount() {
        return this.color.length;
    }

    public class dirViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        public dirViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imageDir);
        }
    }
}
