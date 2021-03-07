package com.example.cta_map.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;

public class TrainTracking_Adapter_frag5 extends RecyclerView.Adapter<TrainTracking_Adapter_frag5.ItemHolder> {
    ArrayList<ListItem> contactsList;
    FragmentManager context;
    public TrainTracking_Adapter_frag5(FragmentManager context, ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.train_tracking_card_view_layout, parent, false);

        return new ItemHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.t1.setText(contact.getTitle());
        holder.imageView.setImageResource(contact.getImage());
        holder.train_eta.setText(contact.getSubtitle());
        holder.list_item.setOnClickListener(v -> {
//            // Options to click train line + train direction
//            String[] train_lines = new String[]{"red", "blue", "green", "orange", "purple", "brown", "pink", "yellow"}; // Replace with DB Retrieval
//            List<String> list = Arrays.asList(train_lines);
//            if (list.contains(contact.getTitle().toLowerCase().trim())){
//                // Navigate to choice of direction
//                ChooseDirection_Fragment chooseDirection_fragment = new ChooseDirection_Fragment(this.context);
//                FragmentTransaction ft = this.context.beginTransaction();
//                ft.replace(R.id.user_frag, chooseDirection_fragment)
//                        .commit();
//            }else{
//                // Navigate to stations based on previous line selection
//                AllStationView_Fragment findStation_fragment = new AllStationView_Fragment(this.context);
//                FragmentTransaction ft = this.context.beginTransaction();
//                ft.replace(R.id.user_frag,findStation_fragment)
//                        .commit();
//            }
        });

    }

    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1, train_eta;
        CardView list_item;
        ImageView imageView;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            train_eta = (TextView) itemView.findViewById(R.id.train_eta);
            list_item = (CardView) itemView.findViewById(R.id.list_item);
            t1 = (TextView) itemView.findViewById(R.id.title_item);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }
}
