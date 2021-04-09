package com.example.cta_map.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;

public class RecyclerView_Adapter_frag1 extends RecyclerView.Adapter<RecyclerView_Adapter_frag1.ItemHolder>  {
    ArrayList<ListItem> contactsList;
    public RecyclerView_Adapter_frag1( ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public RecyclerView_Adapter_frag1.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.map_card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView_Adapter_frag1.ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.imageView.setImageResource(contact.getImage());
        holder.main_title.setText(contact.getTitle());
        holder.subtitle.setText(contact.getSubtitle());
        holder.imageView.setImageResource(contact.getImage());
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

