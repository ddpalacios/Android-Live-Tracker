package com.example.cta_map.Activities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;

public class TrainTimes_Adapter_frag extends RecyclerView.Adapter<TrainTimes_Adapter_frag.ItemHolder>  {
    ArrayList<ListItem> contactsList;
    public TrainTimes_Adapter_frag(ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public TrainTimes_Adapter_frag.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainTimes_Adapter_frag.ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.t1.setText(contact.getTitle());
        holder.imageView.setImageResource(contact.getImage());
        Log.e("New", this.contactsList.size()+"");




        holder.t1.setOnClickListener(v -> {
            Log.e("CLICKED", contact.getTitle()+"");

        });
    }

    public void updateData(final ArrayList<ListItem> stationArrivalPOJO) {
       this.contactsList = new ArrayList<>();
       this.contactsList.addAll(stationArrivalPOJO);
       this.notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1;
        ImageView imageView;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.title_item);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }

}

