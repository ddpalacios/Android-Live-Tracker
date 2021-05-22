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

import com.example.cta_map.Activities.Adapters.CustomInfoWindowAdapter;
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
        View view = inflater.inflate(R.layout.tracking_station_card_view, parent, false);

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
                holder.item.setBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
            }

            holder.item.setOnLongClickListener(v -> {
                if (train.getIsNotified()) { // Resets all trains + its notifications handler
                    chicago_transits.reset(this.current_incoming_trains, message);
                    chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                    chicago_transits.refresh(fragment);
                    cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                    chicago_transits.stopService(context);
                    chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());


                }else{
                    chicago_transits.reset(this.current_incoming_trains, message); // Resets all trains + its notifications handler
                    holder.item.setCardBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
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


            holder.item.setOnClickListener(v -> {
                if (train== null || train.getLat() ==null || train.getLon() == null){
                    return;
                }
                ArrayList<Train> all_trains = message.getOld_trains();

                CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context, message,false);
                mMap.setInfoWindowAdapter(adapter);
                // set the chosen train as selected
                if (!train.getSelected()) {
                    for (Train train1: all_trains){ // resets all trains

                        train1.setSelected(false);
                    }
                    train.setSelected(true);
                }else{
                    if (!train.getIsNotified()) {
                        train.setSelected(false);
                    }

                }
                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                chicago_transits.ZoomIn(mMap, 13f, train.getLat(), train.getLon());





//                if (train.getIsApp().equals("1")){
//                    chicago_transits.ZoomIn(mMap, 20f, train.getLat(), train.getLon());
//                }
//                if (!train.getSelected()) {
//                    // if the train is NOT selected - reset all trains
//                    for (Train train1 : this.current_incoming_trains) {
//                        if (train1.getIsNotified() && train1.getSelected()) {
//                            continue;
//                        }
//                        train1.setSelected(false);
//                    }
//                    // set the chosen train as selected
//                    train.setSelected(true);
//                } else {
//                    // if the train selected IS already selected, deselect train
//                    train.setSelected(false);
//                }
//                chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
//                if (!train.getIsSch()) {
//                    chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());
//                }
            });
        }
        else if (this.stationList!=null){
            // if we are building station cards
            Station station= this.stationList.get(position); // We are plotting all trains in list with iteration
            createStationCard(holder, station); // Creates our main title
            if (this.alarm.getAlarm_id()!=null && station.getMap_id().equals(alarm.getMap_id())){
                holder.item.setBackgroundColor(Color.parseColor(MainActivity.BACKGROUND_COLOR_STRING));
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
        final float scale = context.getResources().getDisplayMetrics().density;
        holder.main_title.setText("To "+ train.getDestNm()); // Set its main title
        holder.train_line.setText((train.getRt() !=null ? chicago_transits.train_line_code_to_regular(train.getRt()) +" line" : "N/A"));
        holder.train_line.setTextColor(Color.parseColor(getColor(train.getRt())));
        holder.imageView.setImageResource(chicago_transits.getTrainImage(train.getRt()));
        holder.isSch.setTextSize((int) (7 * scale + 0.5f));
        holder.train_eta.setTextSize(7 * scale + 0.5f);

        if (train.getIsApp().equals("1")){
            holder.train_eta.setText("Due");
            holder.train_eta.setTextColor(Color.parseColor("#FF0000"));

        }else{
            holder.train_eta.setText(train.getTarget_eta() +"m");
        }


        if (train.getIsSch()){ // if its scheduled and delayed
            holder.main_title.setWidth((int) (100 * scale + 0.5f));
            if (train.getIsDly().equals("1")) {
                holder.isSch.setText("Delayed");
                holder.train_eta.setTextColor(Color.parseColor("#FF0000"));

            }else{
                holder.isSch.setText("Scheduled");
                holder.train_eta.setTextColor(Color.parseColor("#3367D6"));

            }
        }  else if (train.getIsDly().equals("1")){ // if its just delayed
            holder.main_title.setWidth((int) (100 * scale + 0.5f));
            holder.isSch.setText("Delayed");
        }
        else{
            holder.isSch.setVisibility(View.INVISIBLE); // if neither, make invisible
        }
        if (train.getIsApp().equals("1")){
            holder.train_eta.setText("Due");
        }else{
            holder.train_eta.setText(train.getTarget_eta() +"m");
        }


        if (train.getIsDly().equals("1") || train.getIsApp().equals("1")) {
            holder.isSch.setTextColor(Color.parseColor("#FF0000"));
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

        TrainLineKeyCodes.put("pink","#ff66ed");
        TrainLineKeyCodes.put("p","#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());

    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView main_title, train_eta, isSch, train_line;
        ImageView imageView;
        CardView item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            train_line = (TextView) itemView.findViewById(R.id.train_line_subtitle);
            isSch = (TextView) itemView.findViewById(R.id.isSch);
            item = (CardView) itemView.findViewById(R.id.list_item);
            main_title = (TextView) itemView.findViewById(R.id.title_item);
            train_eta = (TextView) itemView.findViewById(R.id.title_eta);
            train_eta.setVisibility(View.VISIBLE);
            train_line.setVisibility(View.VISIBLE);
            isSch.setVisibility(View.VISIBLE);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);

        }
    }

}

