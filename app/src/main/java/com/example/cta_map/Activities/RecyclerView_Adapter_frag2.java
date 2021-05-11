package com.example.cta_map.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerView_Adapter_frag2 extends RecyclerView.Adapter<RecyclerView_Adapter_frag2.ItemHolder> {
    ArrayList<ListItem> StationList;
    Context Maincontext;
    ListItem selected_station;
    Thread t1,t2;
    Handler handler;
    API_Caller_Thread api_caller;
//    Content_Parser_Thread content_parser;
    HashMap<String, Object> threadHashMap;
    Message message;
    FusedLocationProviderClient fusedLocationClient;
    ActionBar actionBar;
    GoogleMap mMap;


    public RecyclerView_Adapter_frag2(HashMap<String, Object> threadHashMap, Context Maincontext, ArrayList<ListItem> StationList, FusedLocationProviderClient fusedLocationClient, ActionBar actionBar, GoogleMap mMap){
        this.StationList = StationList;
        this.Maincontext = Maincontext;
        this.threadHashMap = threadHashMap;
        this.fusedLocationClient = fusedLocationClient;
        this.actionBar = actionBar;
        this.mMap = mMap;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
            view = inflater.inflate(R.layout.all_fav_stations_card_layout, parent, false);

        return new ItemHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        t1 =  (Thread) threadHashMap.get("t1");
        handler =  (Handler) threadHashMap.get("handler");
        message = (Message) threadHashMap.get("message");
        api_caller = (API_Caller_Thread) threadHashMap.get("api_caller");

        selected_station= this.StationList.get(position);
        holder.t1.setText(selected_station.getTitle());
        holder.imageView.setImageResource(selected_station.getImage());
        holder.list_item.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Maincontext);
            builder.setCancelable(true);
            builder.setTitle("Remove Station");
            selected_station = this.StationList.get(position);
            builder.setMessage("Delete "+ selected_station.getTitle()+"?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        CTA_DataBase cta_dataBase = new CTA_DataBase(Maincontext);
                        ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null,null);
                        if (user_tracking_record!=null){
                            HashMap<String, String> currentTrackingStation = (HashMap<String, String>) user_tracking_record.get(0);
                            if (currentTrackingStation.get("FAVORITE_MAP_ID").equals(selected_station.getMapID())){
                                if (t1 != null) {
                                   message.getApi_caller_thread().cancel();
                                    message.getT1().interrupt();
                                }
                            }
                        }
                        String[] values = new String[]{selected_station.getMapID(),selected_station.getTrainLine().toLowerCase().trim(), selected_station.getTrain_dir_label()};
                        cta_dataBase.delete_record("USER_FAVORITES",
                                "FAVORITE_MAP_ID = ? AND STATION_TYPE = ? AND STATION_DIR_LABEL = ?", values );
                        this.StationList.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        cta_dataBase.close();

                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });

        holder.list_item.setOnClickListener(v -> {
            Chicago_Transits chicago_transits = new Chicago_Transits();
            selected_station = this.StationList.get(position);
            CTA_DataBase cta_dataBase = new CTA_DataBase(Maincontext);
            ArrayList<Object> current_userFavorite_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null,null);

            if (current_userFavorite_tracking_record == null) {  // if there is NO station being tracked...
                cta_dataBase.update("USER_FAVORITES", "ISTRACKING", "1", "FAVORITE_MAP_ID = '"+selected_station.getMapID()+"'");

                chicago_transits.callThreads(Maincontext, handler, message, selected_station.getTrain_dir(), selected_station.getTrainLine(), selected_station.getMapID(),false);

            }else{
                // if there IS a station being tracked...
                // make sure the selected item its NOT the same item that's being tracked...
                HashMap<String, String> fav_station = (HashMap<String, String>)  current_userFavorite_tracking_record.get(0);
                if (!selected_station.getMapID().equals(fav_station.get(CTA_DataBase.FAVORITE_MAP_ID))){
                    // if we have selected a new station... cancel current running threads and run new threads for new station

                    //Switch boolean to OFF for current running station
                    cta_dataBase.update("USER_FAVORITES", "ISTRACKING", "0", "FAVORITE_MAP_ID = '" + fav_station.get(CTA_DataBase.FAVORITE_MAP_ID) + "'");
                    if (message.getT1() != null) {
                        int res = chicago_transits.cancelRunningThreads(message);
                        try {
                            message.getT1().join();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (res > 0){
                            MainActivity.LogMessage("Threads were stopped!");
                        }
                        //Switch boolean to ON for new running station
                        cta_dataBase.update("USER_FAVORITES", "ISTRACKING", "1", "FAVORITE_MAP_ID = " +
                                "'" + selected_station.getMapID() + "'"); // New station being tracked


                        chicago_transits.callThreads(Maincontext, handler, message, selected_station.getTrain_dir(), selected_station.getTrainLine(), selected_station.getMapID(),false);
                        MainActivity.LogMessage("New station tracking: "+ selected_station.getTitle());

                    }

                }

            }
            cta_dataBase.close();

        });
    }




    @Override
    public int getItemCount() {
        return this.StationList.size();
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1;
        ImageView imageView;
        CardView list_item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            list_item = (CardView) itemView.findViewById(R.id.list_item);
            t1 = (TextView) itemView.findViewById(R.id.card_title);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }
}
