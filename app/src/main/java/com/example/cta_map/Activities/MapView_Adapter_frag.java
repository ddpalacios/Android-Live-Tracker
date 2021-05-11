package com.example.cta_map.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

public class MapView_Adapter_frag extends RecyclerView.Adapter<MapView_Adapter_frag.ItemHolder>  {
    ArrayList<Train>current_incoming_trains;
    GoogleMap mMap;
    Context context;
    Fragment fragment;
    Message message;
    public MapView_Adapter_frag(Message message, Context context, GoogleMap mMap, ArrayList<Train> contactsList, Fragment fragment){
        this.current_incoming_trains = contactsList;
        this.mMap = mMap;
        this.fragment = fragment;
        this.context = context;
        this.message = message;
    }

    @NonNull
    @Override
    public MapView_Adapter_frag.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.map_card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull MapView_Adapter_frag.ItemHolder holder, int position) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            Chicago_Transits chicago_transits = new Chicago_Transits();

        Train train = this.current_incoming_trains.get(position); // We are plotting all trains in list with iteration
        holder.item.setCardBackgroundColor(Color.parseColor("#FFFFFF")); //Each time fragment gets 'refreshed', we are resetting background color to white
        createTrainCard(holder, train); // Creates our main title, image, and subtitle

        // if one of our trains is being notified - change the background color
        if (train.getIsNotified()){
            holder.item.setCardBackgroundColor(Color.parseColor("#F44336"));
        }

        holder.item.setOnLongClickListener(v -> {
            if (!train.getIsNotified()){ // if this train is not currently being notified - notify it!
                chicago_transits.reset(this.current_incoming_trains,message); // Resets all trains + its notifications handler
                holder.item.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
                // Setting selected train for notifications
                train.setSelected(true);
                train.setNotified(true);


                // TODO: Do we need to do this? ///
                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                chicago_transits.refresh(fragment);
                /////////////////////////////////


                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
                cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
                cta_dataBase.close();



            }else{
                chicago_transits.reset(this.current_incoming_trains,message); // Resets all trains + its notifications handler
                // if we reselect our train tracking train then turn it off!
                train.setNotified(false);
                train.setSelected(false);
//                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());

                chicago_transits.refresh(fragment);
                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);

                //TODO: Add condition to check if there is a service running before stopping a service
                chicago_transits.stopService(context);
                cta_dataBase.close();

            }

            if (!train.getIsSch()) {
                chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
            }
            message.getT1().interrupt();

            return false;

        });

        holder.item.setOnClickListener(v -> {
            if (!train.getSelected()) {
                // if the train is NOT selected - reset all trains
                for (Train train1 : this.current_incoming_trains) {
                    if (train1.getIsNotified() && train1.getSelected()) {
                        continue;
                    }
                    train1.setSelected(false);
                }
                // set the chosen train as selected
                train.setSelected(true);
            }else{
                // if the train selected IS already selected, deselect train
                train.setSelected(false);
            }
            chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
            if (!train.getIsSch()) {
                chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
            }


        });
        cta_dataBase.close();

    }

    private void createTrainCard(ItemHolder holder, Train train) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        holder.imageView.setImageResource(chicago_transits.getTrainImage(train.getRt()));
        holder.main_title.setText("To "+ train.getDestNm());
        holder.subtitle.setText("Status: "+(train.getStatus()!=null ? train.getStatus() : "N/A"));
        holder.line.setText(train.getRt()+" Line");
        holder.train_eta.setText(train.getTarget_eta()+"m");
        holder.status_image.setImageResource(chicago_transits.getStatusColor(train.getStatus()));
    }


    @Override
    public int getItemCount() {
        return this.current_incoming_trains.size();
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView main_title, subtitle, line, train_eta;
        ImageView imageView, status_image;
        CardView item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            train_eta = (TextView) itemView.findViewById(R.id.train_eta);
            item = (CardView) itemView.findViewById(R.id.list_item);
            line = (TextView) itemView.findViewById(R.id.final_destination);
            main_title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.status);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
            status_image = (ImageView) itemView.findViewById(R.id.status_image);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void plot(Train train){
        mMap.clear();
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+train.getTarget_id()+"'", null,null);
        Station target_station = (Station) record.get(0);
        cta_dataBase.close();
        Chicago_Transits chicago_transits = new Chicago_Transits();
        chicago_transits.plot_marker(context,this.message,mMap, null, target_station);
        chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
        for (Train train1 : this.message.getOld_trains()){
            chicago_transits.plot_marker(context,this.message,mMap, train1, null);
        }
    }


}

