package com.example.cta_map.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecyclerView_Adapter_frag3 extends RecyclerView.Adapter<RecyclerView_Adapter_frag3.ItemHolder> {
    ArrayList<ListItem> contactsList;
    FragmentManager context;
    HashMap<String, String> tracking_station;
    public RecyclerView_Adapter_frag3(HashMap<String, String>  tracking_station, FragmentManager context, ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
        this.context = context;
        this.tracking_station = tracking_station;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.train_times_card_view_layout, parent, false);

        return new ItemHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.t1.setText(contact.getTitle());
        holder.imageView.setImageResource(contact.getImage());


        holder.list_item.setOnClickListener(v -> {
            String[] train_lines = new String[]{"red", "blue", "green", "orange", "purple", "brown", "pink", "yellow"}; // Replace with DB Retrieval
            List<String> list = Arrays.asList(train_lines);
            if (list.contains(contact.getTitle().toLowerCase().trim())){
                ChooseDirection_Fragment chooseDirection_fragment = new ChooseDirection_Fragment();
                Bundle bundle = new Bundle();
                this.tracking_station.put("train_line", contact.getTitle());
                bundle.putSerializable("tracking_station",tracking_station);
                chooseDirection_fragment.setArguments(bundle);
                FragmentTransaction ft = this.context.beginTransaction();
                ft.replace(R.id.user_frag, chooseDirection_fragment)
                        .commit();
            }else{
                AllStationView_Fragment findStation_fragment = new AllStationView_Fragment();
                tracking_station.put("train_dir", contact.getTrain_dir());
                Bundle bundle = new Bundle();
                bundle.putSerializable("tracking_station",tracking_station);
                findStation_fragment.setArguments(bundle);
                FragmentTransaction ft = this.context.beginTransaction();
                ft.replace(R.id.user_frag,findStation_fragment)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1;
        CardView list_item;
        ImageView imageView;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            list_item = (CardView) itemView.findViewById(R.id.list_item);
            t1 = (TextView) itemView.findViewById(R.id.title_item);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }
}
