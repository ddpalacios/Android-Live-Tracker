package com.example.cta_map.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;

public class RecyclerView_Adapter_frag1 extends RecyclerView.Adapter<RecyclerView_Adapter_frag1.ItemHolder>  {
    ArrayList<ListItem> contactsList;
    FragmentManager context;
    public RecyclerView_Adapter_frag1(FragmentManager context, ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView_Adapter_frag1.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView_Adapter_frag1.ItemHolder holder, int position) {
        final ListItem contact = this.contactsList.get(position);
        holder.t1.setText(contact.getTitle());
        holder.imageView.setImageResource(contact.getImage());
        holder.t1.setOnClickListener(v -> {
//            ChooseDirection_Fragment chooseDirection_fragment = new ChooseDirection_Fragment(this.context);
//            Toast.makeText(this.context, "Clicked "+ contact.getTitle(), Toast.LENGTH_SHORT).show();
//            FragmentTransaction ft = this.context.beginTransaction();
//            ft.replace(R.id.user_frag, chooseDirection_fragment)
//                    .commit();







        });
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

