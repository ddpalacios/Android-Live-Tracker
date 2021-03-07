package com.example.cta_map.Activities;

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

public class RecyclerView_Adapter_frag2 extends RecyclerView.Adapter<RecyclerView_Adapter_frag2.ItemHolder> {
    ArrayList<ListItem> contactsList;
    FragmentManager context;
    public RecyclerView_Adapter_frag2(FragmentManager context, ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_view_layout, parent, false);

        return new ItemHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.direction_id.setText(contact.getDirection_id());
        holder.t1.setText(contact.getTitle());
        holder.imageView.setImageResource(contact.getImage());
        holder.list_item.setOnClickListener(v -> {
            // Navigation to TrainTracking Fragment

            //  Create a fragment layout to train tracking (times)
            TrainTracking_Fragment trainTracking_fragment = new TrainTracking_Fragment();
//            ChooseDirection_Fragment chooseDirection_fragment = new ChooseDirection_Fragment(this.context);
//            Toast.makeText(this.context, "Clicked "+ contact.getTitle(), Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = this.context.beginTransaction();
            ft.replace(R.id.user_frag, trainTracking_fragment)
                    .commit();

        });

    }

    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1, direction_id;
        ImageView imageView;
        CardView list_item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            direction_id = itemView.findViewById(R.id.direction_id);
            list_item = (CardView) itemView.findViewById(R.id.list_item);
            t1 = (TextView) itemView.findViewById(R.id.title_item);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }
}
