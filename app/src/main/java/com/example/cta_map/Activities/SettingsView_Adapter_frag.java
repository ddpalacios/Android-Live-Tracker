package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsView_Adapter_frag extends RecyclerView.Adapter<SettingsView_Adapter_frag.ItemHolder>  {
    ArrayList<UserSettings> setting_cards;
    GoogleMap mMap;
    Context context;
    Fragment fragment;
    Message message;
    public SettingsView_Adapter_frag(Message message, Context context, ArrayList<UserSettings> contactsList, Fragment fragment){
        this.setting_cards = contactsList;
        this.fragment = fragment;
        this.context = context;
        this.message = message;
    }

    @NonNull
    @Override
    public SettingsView_Adapter_frag.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_settings_card_view, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull SettingsView_Adapter_frag.ItemHolder holder, int position) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            Chicago_Transits chicago_transits = new Chicago_Transits();
            UserSettings user_settings = this.setting_cards.get(position);

            createTrainCard(holder, user_settings); // Creates our main title, image, and subtitle






    }

//        Train train = this.current_incoming_trains.get(position); // We are plotting all trains in list with iteration
//        holder.cardViewitem.setCardBackgroundColor(Color.parseColor("#FFFFFF")); //Each time fragment gets 'refreshed', we are resetting background color to white
//
//        // if one of our trains is being notified - change the background color
//        if (train.getIsNotified()){
//            holder.cardViewitem.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
//        }
//
//        holder.cardViewitem.setOnLongClickListener(v -> {
//            if (!train.getIsNotified()){ // if this train is not currently being notified - notify it!
//                chicago_transits.reset(this.current_incoming_trains,message); // Resets all trains + its notifications handler
//                holder.cardViewitem.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
//                // Setting selected train for notifications
//                train.setSelected(true);
//                train.setNotified(true);
//
//
//                // TODO: Do we need to do this? ///
//                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
//                chicago_transits.refresh(fragment);
//                /////////////////////////////////
//
//
//                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
//                cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
//                cta_dataBase.close();
//
//
//
//            }else{
//                chicago_transits.reset(this.current_incoming_trains,message); // Resets all trains + its notifications handler
//                // if we reselect our train tracking train then turn it off!
//                train.setNotified(false);
//                train.setSelected(false);
////                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
//
//                chicago_transits.refresh(fragment);
//                cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
//
//                //TODO: Add condition to check if there is a service running before stopping a service
//                chicago_transits.stopService(context);
//                cta_dataBase.close();
//
//            }
//
//            if (!train.getIsSch()) {
//                chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
//            }
//            message.getT1().interrupt();
//
//            return false;
//
//        });
//
//        holder.cardViewitem.setOnClickListener(v -> {
//            if (train== null || train.getLat() ==null || train.getLon() == null){
//                return;
//            }
//            if (train.getIsApp().equals("1")){
//                chicago_transits.ZoomIn(mMap, 20f, train.getLat(), train.getLon());
//            }
//            if (!train.getSelected()) {
//                // if the train is NOT selected - reset all trains
//                for (Train train1 : this.current_incoming_trains) {
//                    if (train1.getIsNotified() && train1.getSelected()) {
//                        continue;
//                    }
//                    train1.setSelected(false);
//                }
//                // set the chosen train as selected
//                train.setSelected(true);
//            }else{
//                // if the train selected IS already selected, deselect train
//                train.setSelected(false);
//            }
//            chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
//            if (!train.getIsSch()) {
//                chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
//            }
//
//
//        });
//        cta_dataBase.close();


    @SuppressLint("SetTextI18n")
    private void createTrainCard(ItemHolder holder, UserSettings train) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        holder.train_line.setText(train.getStatus());
        holder.train_line.setTextColor(Color.parseColor(getColor(chicago_transits.TrainLineKeys(train.getStatus()))));
        holder.status_image.setImageResource(chicago_transits.getStatusColor(train.getStatus()));
        holder.status_label.setText(chicago_transits.getStatusMessage(train.getStatus()));
        holder.status_label.setTextColor(Color.parseColor(getColor(chicago_transits.TrainLineKeys(train.getStatus()))));
        holder.imageView.setImageResource(chicago_transits.getTrainImage(train.getStatus()));
//        if (train.getAsMinutes()!= null && train.getAsMinutes().equals("1")){
//            holder.min_txt.setText("Minutes");
//        }else{
//            holder.min_txt.setText("Stops");
//
//        }


        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, CTA_DataBase.USER_SETTINGS_ID +" = '1'",null,null);
        UserSettings userSettings;
        if (record!= null) {
            userSettings = (UserSettings) record.get(0);
        }else{
            userSettings = new UserSettings();
            cta_dataBase.commit(userSettings, CTA_DataBase.USER_SETTINGS);
        }

        if (userSettings.getGreen_limit()!=null){
            if (train.getStatus().toLowerCase().equals("green")) {
                holder.status_bar.setProgress(Integer.parseInt(userSettings.getGreen_limit()));
                holder.train_eta.setText("> "+userSettings.getGreen_limit());
            }
        }

        if (userSettings.getYellow_limit()!=null){
            if (train.getStatus().toLowerCase().equals("green")) {
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.GREEN_LIMIT, userSettings.getGreen_limit(), CTA_DataBase.USER_SETTINGS_ID + " = '1'");
            }else if (train.getStatus().toLowerCase().equals("yellow")) {
                holder.status_bar.setProgress(Integer.parseInt(userSettings.getYellow_limit()));
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.YELLOW_LIMIT, userSettings.getYellow_limit(), CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                holder.train_eta.setText("> "+userSettings.getYellow_limit());

            }


        }




        if (train.getStatus().toLowerCase().equals("red")){
            holder.status_bar.setVisibility(View.INVISIBLE);
            holder.isSch.setVisibility(View.VISIBLE);
            holder.isSch.setText("Anything less than 'Yellow'");
            holder.train_eta.setText("");
            holder.min_txt.setText("");

        }else if (train.getStatus().toLowerCase().equals("gray")){

            holder.status_bar.setVisibility(View.INVISIBLE);
            holder.train_eta.setText("");
            holder.min_txt.setText("");

        }


        holder.status_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (train.getStatus().toLowerCase().equals("green")) {
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.GREEN_LIMIT, progress+"", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    holder.train_eta.setText("> "+progress);

                }else if (train.getStatus().toLowerCase().equals("yellow")) {
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.YELLOW_LIMIT, progress+"", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    holder.train_eta.setText("> "+progress);

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (train.getStatus().toLowerCase().equals("green")) {
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.GREEN_LIMIT, progress+"", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    holder.train_eta.setText("> "+progress);

                }else if (train.getStatus().toLowerCase().equals("yellow")) {
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.YELLOW_LIMIT, progress+"", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    holder.train_eta.setText("> "+progress);
                }


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (train.getStatus().toLowerCase().equals("green")) {
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.GREEN_LIMIT, progress+"", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    holder.train_eta.setText("> "+progress);

                }else if (train.getStatus().toLowerCase().equals("yellow")) {
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.YELLOW_LIMIT, progress+"", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    holder.train_eta.setText("> "+progress);

                }
                if (MainActivity.message.getT1() !=null){
                    MainActivity.message.getT1().interrupt();
                }

            }
        });

    cta_dataBase.close();

    }
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        final float scale = context.getResources().getDisplayMetrics().density;
//        holder.main_title.setText("To "+ train.getDestNm()); // Set its main title
//        holder.train_line.setText((train.getRt() !=null ? chicago_transits.train_line_code_to_regular(train.getRt()) +" line" : "N/A"));
//        holder.train_line.setTextColor(Color.parseColor(getColor(train.getRt())));
//        holder.imageView.setImageResource(chicago_transits.getTrainImage(train.getRt()));
//        holder.isSch.setTextSize((int) (7 * scale + 0.5f));
//        holder.train_eta.setTextSize(7 * scale + 0.5f);
//
//
//        if (train.getIsSch()){ // if its scheduled and delayed
//            holder.main_title.setWidth((int) (100 * scale + 0.5f));
//            if (train.getIsDly().equals("1")) {
//                holder.isSch.setText("Delayed");
//                holder.train_eta.setTextColor(Color.parseColor("#FF0000"));
//
//            }else{
//                holder.isSch.setText("Scheduled");
//                holder.train_eta.setTextColor(Color.parseColor("#3367D6"));
//
//            }
//        }
//        else if (train.getIsDly().equals("1")){ // if its just delayed
//            holder.main_title.setWidth((int) (100 * scale + 0.5f));
//            holder.isSch.setText("Delayed");
//            holder.train_eta.setTextColor(Color.parseColor("#FF0000"));
//        }
//        else{
//            holder.isSch.setVisibility(View.INVISIBLE); // if neither, make invisible
//        }
//
//        if (train.getIsApp().equals("1")){
//            holder.train_eta.setText("Due");
//        }else{
//            holder.train_eta.setText(train.getTarget_eta() +"m");
//        }
//
//
//        holder.status_image.setImageResource(chicago_transits.getStatusColor(train.getStatus()));
//        holder.status_label.setText(chicago_transits.getStatusMessage(train.getStatus()));
//
//        if (train.getStatus() == null){
//            holder.status_label.setText("");
//            holder.status_image.setVisibility(View.INVISIBLE);
//
//        }else{
//            holder.status_image.setVisibility(View.VISIBLE);
//            holder.status_label.setTextColor(Color.parseColor(getColor(chicago_transits.TrainLineKeys(train.getStatus()))));
//        }
//
//
//         if (train.getIsDly().equals("1") || train.getIsApp().equals("1")) {
//            holder.isSch.setTextColor(Color.parseColor("#FF0000"));
//        }



    @Override
    public int getItemCount() {
        return this.setting_cards.size();
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView main_title, isSch, train_line, train_eta, status_label, min_txt;
        SeekBar status_bar;
        ImageView imageView, status_image;
        CardView cardViewitem, loc_settings;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            min_txt = (TextView) itemView.findViewById(R.id.min_txt);
            isSch = (TextView) itemView.findViewById(R.id.isSch);
            train_eta = (TextView) itemView.findViewById(R.id.title_eta);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
            status_bar = (SeekBar) itemView.findViewById(R.id.seekBar_green);
            train_line = (TextView) itemView.findViewById(R.id.train_line_subtitle);
            status_image = (ImageView) itemView.findViewById(R.id.StatusImage);
            status_label = (TextView) itemView.findViewById(R.id.status_label);
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
        TrainLineKeyCodes.put("gray", "#808080");
        TrainLineKeyCodes.put("pink","#ff66ed");
        TrainLineKeyCodes.put("p","#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());

    }


}

