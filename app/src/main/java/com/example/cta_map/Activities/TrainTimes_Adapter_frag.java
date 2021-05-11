package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import java.util.HashMap;

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

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class TrainTimes_Adapter_frag extends RecyclerView.Adapter<TrainTimes_Adapter_frag.ItemHolder>  {
    ArrayList<Train> current_incoming_trains;
    Message message;
    GoogleMap mMap;
    Context context;
    Fragment fragment;
    RecyclerView recyclerView;
    Alarm alarm;
    ArrayList<Station> stationList;
    public TrainTimes_Adapter_frag(Context context, Message message, ArrayList<Train> TrainList, ArrayList<Station> StationList, RecyclerView recyclerView, Alarm alarm){
        /*
        Our adapters is what will allow us to interact with each tab + actions

         */
        this.mMap = MainActivity.mMap;
        this.alarm = alarm;
        this.stationList = StationList;
        this.current_incoming_trains=TrainList;
        this.fragment = MainActivity.frg;
        this.context = context;
        this.message = message;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public TrainTimes_Adapter_frag.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.train_times_card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TrainTimes_Adapter_frag.ItemHolder holder, int position){
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        Chicago_Transits chicago_transits = new Chicago_Transits();

        holder.item.setCardBackgroundColor(Color.parseColor("#FFFFFF")); //Each time fragment gets 'refreshed', we are resetting background color to white

        // if we are building train cards...
        if (this.current_incoming_trains!=null) {
            Train train = this.current_incoming_trains.get(position); // We are plotting all trains in list with iteration

            createTrainCard(holder, train); // Creates our main title, image, and subtitle
            // if one of our trains is being notified - change the background color
            if (train.getIsNotified()) {
                holder.item.setCardBackgroundColor(Color.parseColor("#F44336"));
            }

            holder.item.setOnLongClickListener(v -> {
                if (!train.getIsNotified()) { // if this train is not currently being notified - notify it!
                    chicago_transits.reset(this.current_incoming_trains, message); // Resets all trains + its notifications handler
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


                } else {
                    chicago_transits.reset(this.current_incoming_trains, message); // Resets all trains + its notifications handler
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
                } else {
                    // if the train selected IS already selected, deselect train
                    train.setSelected(false);
                }
                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                if (!train.getIsSch()) {
                    chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
                }
            });
        }
        else if (this.stationList!=null){
            // if we are building station cards
            Station station= this.stationList.get(position); // We are plotting all trains in list with iteration
            createStationCard(holder, station); // Creates our main title
            if (this.alarm.getAlarm_id()!=null && station.getMap_id().equals(alarm.getMap_id())){
                holder.item.setCardBackgroundColor(Color.parseColor("#F44336"));
            }else{
                holder.item.setCardBackgroundColor(Color.parseColor("#FFFFFF")); //Each time fragment gets 'refreshed', we are resetting background color to white
            }

           holder.item.setOnClickListener(v -> {
               if (alarm!=null){
                   alarm.setMap_id(station.getMap_id());
                   if (alarm.getAlarm_id() == null) { // determines if we are editing or creating an alarm
                       alarm.setStation_name(station.getStation_name());
                       alarm.setWeekLabel(NewAlarmSetUp.getWeeklyLabel(alarm));
                       cta_dataBase.commit(alarm, CTA_DataBase.ALARMS);
                       ArrayList<Object> alarm_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, null,null,null);

                       if (alarm.getIsRepeating()==1){
                           HashMap<String, String> r = (HashMap<String, String>) alarm_record.get(alarm_record.size()-1); // retrieve last record added
                           alarm.setAlarm_id(r.get(CTA_DataBase.ALARM_ID));
                           String[] days = alarm.getWeekLabel().split(",");
                           int count = 0;
                           for (String day_of_week : days){
                               count+=1;
                               Integer day_to_int = chicago_transits.getDayOfWeekNum(day_of_week);
                               if (day_to_int != null) {
                                   chicago_transits.scheduleAlarm(context, day_to_int, alarm, alarm.getAlarm_id() + count, alarm.getAlarm_id());
                               }
                           }
                       }
                       context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                   }else{
                       cta_dataBase.update("ALARMS", "STATION_TYPE", station.getStation_type(), "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                       cta_dataBase.update("ALARMS", "MAP_ID", station.getMap_id(), "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                       notifyDataSetChanged();
                       NewAlarmSetUp.bar.setTitle(station.getStation_name());
                   }



               }
           });




        }
        cta_dataBase.close();
    }

    private void createStationCard(ItemHolder holder, Station station) {
        holder.main_title.setText(station.getStation_name()); // Set its main title
         holder.isSch.setVisibility(View.INVISIBLE);
         if (station.getStation_type()!=null) {
             holder.imageView.setImageResource(new Chicago_Transits().getTrainImage(station.getStation_type()));
         }

    }


    @Override
    public int getItemCount() {
        if (this.current_incoming_trains !=null) {

            return this.current_incoming_trains.size();
        }else{
            return this.stationList.size();

        }
    }


    @SuppressLint("SetTextI18n")
    private void createTrainCard(ItemHolder holder, Train train) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        holder.main_title.setText("To "+ train.getDestNm()); // Set its main title
        holder.train_line_subtitle.setText((train.getRt() !=null ? chicago_transits.train_line_code_to_regular(train.getRt()) : "N/A"));
        if (train.getIsSch()){
            if (train.getIsDly().equals("1")) {
                holder.isSch.setText("Delayed");
            }else{
                holder.isSch.setText("Scheduled");
            }
        }
        else if (train.getIsDly().equals("1")){
            holder.isSch.setText("Delayed");
        }
        else{
            holder.isSch.setVisibility(View.INVISIBLE);
        }



        if (train.getIsApp().equals("1")){
            holder.subtitle.setText("Due");
        }else{
            holder.subtitle.setText(train.getTarget_eta() +"m");
        }
        holder.imageView.setImageResource(new Chicago_Transits().getTrainImage(train.getRt()));

    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView main_title, subtitle, isSch, train_line_subtitle;
        ImageView imageView;
        CardView item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            train_line_subtitle = (TextView) itemView.findViewById(R.id.train_line_subtitle);
            isSch = (TextView) itemView.findViewById(R.id.isSch);
            item = (CardView) itemView.findViewById(R.id.list_item);
            main_title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.title_eta);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }

}

