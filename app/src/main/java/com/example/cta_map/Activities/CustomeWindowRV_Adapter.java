package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomeWindowRV_Adapter extends RecyclerView.Adapter<CustomeWindowRV_Adapter.ItemHolder>  {
    ArrayList<TrainStops> TrainList;
    Message message;
    GoogleMap mMap;
    Context context;
    public CustomeWindowRV_Adapter(Context context, Message message, ArrayList<TrainStops> contactsList){
        this.TrainList= contactsList;
        this.context = context;
        this.message = message;
    }

    @NonNull
    @Override
    public CustomeWindowRV_Adapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.remaining_stops_card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomeWindowRV_Adapter.ItemHolder holder, int position) {
        TrainStops train = this.TrainList.get(position);
        holder.main_title.setText("To "+ train.getStaNm());
        holder.subtitle.setText(train.getNextStopEtA()+"m");
//        holder.imageView.setImageResource(new Chicago_Transits().getTrainImage(train.getRt()));
//
//        holder.item.setOnClickListener(v -> {
//            mMap.clear();
//            for (Train train1: this.message.getOld_trains()){
//                train1.setSelected(false);
//            }
//            train.setSelected(true);
//            CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
//            ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+train.getTarget_id()+"'", null,null);
//            HashMap<String, String> target_station = (HashMap<String, String>) record.get(0);
//
//            Chicago_Transits chicago_transits = new Chicago_Transits();
//            chicago_transits.plot_marker(context,this.message,mMap, null, target_station);
//            chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
//            for (Train train1 : this.message.getOld_trains()){
//                chicago_transits.plot_marker(context,this.message,mMap, train1, null);
//                Log.e("TRAIN", "IS Selected: "+ train1.getSelected()+ " #"+train1.getRn());
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return this.TrainList.size();
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

