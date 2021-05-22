package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
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

import com.example.cta_map.Activities.Adapters.CustomInfoWindowAdapter;
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
        View view = inflater.inflate(R.layout.tracking_station_card_view, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull MapView_Adapter_frag.ItemHolder holder, int position) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            Chicago_Transits chicago_transits = new Chicago_Transits();

        Train train = this.current_incoming_trains.get(position); // We are plotting all trains in list with iteration
        holder.cardViewitem.setCardBackgroundColor(Color.parseColor("#FFFFFF")); //Each time fragment gets 'refreshed', we are resetting background color to white
        createTrainCard(holder, train); // Creates our main title, image, and subtitle

        // if one of our trains is being notified - change the background color
        if (train.getIsNotified()){
            holder.cardViewitem.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
        }

        holder.cardViewitem.setOnLongClickListener(v -> {
            if (train.getIsNotified()) { // Resets all trains + its notifications handler
                chicago_transits.reset(this.current_incoming_trains, message);
                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                chicago_transits.refresh(fragment);
                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                chicago_transits.stopService(context);

            }else{
                chicago_transits.reset(this.current_incoming_trains, message); // Resets all trains + its notifications handler
                holder.cardViewitem.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
                // Setting selected train for notifications
                train.setSelected(true);
                train.setNotified(true);
                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                chicago_transits.refresh(fragment);
                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
                cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
                chicago_transits.ZoomIn(mMap, 13f, train.getLat(), train.getLon());


            }

            message.getT1().interrupt();
            cta_dataBase.close();
            return false;
        });

        holder.cardViewitem.setOnClickListener(v -> {
            if (train== null || train.getLat() ==null || train.getLon() == null){
                return;
            }
            ArrayList<Train> all_trains = message.getOld_trains();

            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context, message,false);
            mMap.setInfoWindowAdapter(adapter);
            // set the chosen train as selected
            if (!train.getSelected()) {
                for (Train train1: all_trains){ // resets all trains
                    if (train1.getIsNotified() && train1.getSelected()){
                        continue;
                    }
                    train1.setSelected(false);
                }
                train.setSelected(true);
            }else{
                train.setSelected(false);

            }
            chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
            chicago_transits.ZoomIn(mMap, 13f, train.getLat(), train.getLon());


        });
        cta_dataBase.close();

    }

    @SuppressLint("SetTextI18n")
    private void createTrainCard(ItemHolder holder, Train train) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        final float scale = context.getResources().getDisplayMetrics().density;
        holder.main_title.setText("To "+ train.getDestNm()); // Set its main title
        holder.train_line.setText((train.getRt() !=null ? chicago_transits.train_line_code_to_regular(train.getRt()) +" line" : "N/A"));
        holder.train_line.setTextColor(Color.parseColor(getColor(train.getRt())));
        holder.imageView.setImageResource(chicago_transits.getTrainImage(train.getRt()));
        holder.isSch.setTextSize((int) (7 * scale + 0.5f));
        holder.train_eta.setTextSize(7 * scale + 0.5f);


        if (train.getIsSch()){ // if its scheduled and delayed
            holder.main_title.setWidth((int) (100 * scale + 0.5f));
            if (train.getIsDly().equals("1")) {
                holder.isSch.setText("Delayed");
                holder.train_eta.setTextColor(Color.parseColor("#FF0000"));

            }else{
                holder.isSch.setText("Scheduled");
                holder.train_eta.setTextColor(Color.parseColor("#3367D6"));
                holder.status_image.setVisibility(View.VISIBLE);
                holder.status_image.setImageResource(chicago_transits.getTrainImage("gray"));
                holder.status_label.setTextColor(Color.parseColor(getColor("gray")));
                holder.status_label.setVisibility(View.VISIBLE);

            }
        }
        else if (train.getIsDly().equals("1")){ // if its just delayed
            holder.main_title.setWidth((int) (100 * scale + 0.5f));
            holder.isSch.setText("Delayed");
            holder.train_eta.setTextColor(Color.parseColor("#FF0000"));
        }
        else{
            holder.isSch.setVisibility(View.INVISIBLE); // if neither, make invisible
        }

        if (train.getIsApp().equals("1")){
            holder.train_eta.setText("Due");
        }else{
            holder.train_eta.setText(train.getTarget_eta() +"m");
        }

        holder.status_image.setImageResource(chicago_transits.getStatusColor(train.getStatus()));
        holder.status_label.setText(chicago_transits.getStatusMessage(train.getStatus()));

        if (train.getStatus() == null){
            holder.status_label.setText("");
            holder.status_image.setVisibility(View.INVISIBLE);

        }else{
            holder.status_image.setVisibility(View.VISIBLE);
            holder.status_label.setTextColor(Color.parseColor(getColor(chicago_transits.TrainLineKeys(train.getStatus()))));
        }


         if (train.getIsDly().equals("1") || train.getIsApp().equals("1")) {
            holder.isSch.setTextColor(Color.parseColor("#FF0000"));
        }

    }


    @Override
    public int getItemCount() {
        return this.current_incoming_trains.size();
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView main_title, isSch, train_line, train_eta, status_label;
        ImageView imageView, status_image;
        CardView cardViewitem;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
            main_title = (TextView) itemView.findViewById(R.id.title_item);
            train_line = (TextView) itemView.findViewById(R.id.train_line_subtitle);
            train_eta = (TextView) itemView.findViewById(R.id.title_eta);
            status_image = (ImageView) itemView.findViewById(R.id.StatusImage);
            status_label = (TextView) itemView.findViewById(R.id.status_label);
            isSch = (TextView) itemView.findViewById(R.id.isSch);

            train_line.setVisibility(View.VISIBLE);
            train_eta.setVisibility(View.VISIBLE);
            status_image.setVisibility(View.VISIBLE);
            status_label.setVisibility(View.VISIBLE);
            isSch.setVisibility(View.VISIBLE);

            cardViewitem = (CardView) itemView.findViewById(R.id.list_item);

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


    private String getColor(String train_line){
        HashMap<String, String> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red","#F44336");
        TrainLineKeyCodes.put("blue","#384cff");
        TrainLineKeyCodes.put("brn", "#a34700");
        TrainLineKeyCodes.put("g", "#0B8043");
        TrainLineKeyCodes.put("org", "#ffad33");
        TrainLineKeyCodes.put("y", "#b4ba0b");
        TrainLineKeyCodes.put("gray", "#c7c7c7");

        TrainLineKeyCodes.put("pink","#ff66ed");
        TrainLineKeyCodes.put("p","#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());

    }


}

