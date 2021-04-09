package com.example.cta_map.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class TrainTimes_Adapter_frag extends RecyclerView.Adapter<TrainTimes_Adapter_frag.ItemHolder>  {
    ArrayList<ListItem> contactsList;
    GoogleMap mMap;
    public TrainTimes_Adapter_frag(ArrayList<ListItem> contactsList, GoogleMap mMap){
        this.contactsList = contactsList;
        this.mMap = mMap;
    }

    @NonNull
    @Override
    public TrainTimes_Adapter_frag.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.train_times_card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainTimes_Adapter_frag.ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.main_title.setText(contact.getTitle());
        holder.subtitle.setText(contact.getSubtitle());
        holder.imageView.setImageResource(contact.getImage());

        holder.item.setOnClickListener(v -> {
            Chicago_Transits chicago_transits = new Chicago_Transits();
            chicago_transits.ZoomIn(mMap, 12f, contact.getLat(), contact.getLon());

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

