package com.example.cta_map.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerView_Adapter_frag2 extends RecyclerView.Adapter<RecyclerView_Adapter_frag2.ItemHolder> {
    ArrayList<ListItem> contactsList;
    Context Maincontext;
    ListItem contact;
    Thread t1,t2;
    Handler handler;
    API_Caller_Thread api_caller;
    Content_Parser_Thread content_parser;
    HashMap<String, Object> threadHashMap;
    Message message;


    public RecyclerView_Adapter_frag2(HashMap<String, Object> threadHashMap, Context Maincontext, ArrayList<ListItem> contactsList){
        this.contactsList = contactsList;
        this.Maincontext = Maincontext;
        this.threadHashMap = threadHashMap;
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
        t1 =  (Thread) threadHashMap.get("t1");
        t2 =  (Thread) threadHashMap.get("t2");
        handler =  (Handler) threadHashMap.get("handler");
        message = (Message) threadHashMap.get("message");
        api_caller = (API_Caller_Thread) threadHashMap.get("api_caller");
        content_parser = (Content_Parser_Thread) threadHashMap.get("content_parser");

        contact = this.contactsList.get(position);
        holder.direction_id.setText(contact.getDirection_id());
        holder.t1.setText(contact.getTitle());
        holder.imageView.setImageResource(contact.getImage());
        holder.list_item.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Maincontext);
            builder.setCancelable(true);
            builder.setTitle("Remove Station");
            contact = this.contactsList.get(position);
            builder.setMessage("Delete "+ contact.getTitle()+"?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        CTA_DataBase cta_dataBase = new CTA_DataBase(Maincontext);
                        ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null,null);
                        if (user_tracking_record!=null){
                            HashMap<String, String> currentTrackingStation = (HashMap<String, String>) user_tracking_record.get(0);
                            if (currentTrackingStation.get("FAVORITE_MAP_ID").equals(contact.getMapID())){
                                if (t1 != null && t2 != null) {
                                    api_caller.cancel();
                                    t1.interrupt();
                                    content_parser.cancel();
                                    t2.interrupt();
                                }
                            }
                        }
                        String[] values = new String[]{contact.getMapID(),contact.getTrainLine().toLowerCase().trim(), contact.getTrain_dir_label()};
                        cta_dataBase.delete_record("USER_FAVORITES",
                                "FAVORITE_MAP_ID = ? AND STATION_TYPE = ? AND STATION_DIR_LABEL = ?", values );
                        this.contactsList.remove(position);
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
            contact = this.contactsList.get(position);
            CTA_DataBase cta_dataBase = new CTA_DataBase(Maincontext);
            ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null,null);
            ArrayList<Object> selected_station_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+contact.getMapID()+"'", null,null);
            if (selected_station_record !=null && user_tracking_record == null) {  // if there is no station being tracked...
                HashMap<String, String> target_station = (HashMap<String, String>) selected_station_record .get(0);
                cta_dataBase.update("USER_FAVORITES", "ISTRACKING", "1", "FAVORITE_MAP_ID = " + "'"+contact.getMapID()+"'");
                Log.e("TRACKING", target_station.get("STATION_NAME")+".");
                callThreads(target_station);
            } else{
                HashMap<String, String> currentTrackingStation = (HashMap<String, String>) user_tracking_record.get(0);
                if (!currentTrackingStation.get("FAVORITE_MAP_ID").equals(contact.getMapID())) { // As long as its not the same item that's being tracked...
                    cta_dataBase.update("USER_FAVORITES", "ISTRACKING", "0", "FAVORITE_MAP_ID = " +"'" + currentTrackingStation.get("FAVORITE_MAP_ID") + "'");
                    if (threadHashMap.get("t1") != null && threadHashMap.get("t2") != null) {
                        api_caller.cancel();
                        t1.interrupt();
                        content_parser.cancel();
                        t2.interrupt();
                    }
                    cta_dataBase.update("USER_FAVORITES", "ISTRACKING", "1", "FAVORITE_MAP_ID = " +
                            "'" + contact.getMapID() + "'");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+contact.getMapID()+"'", null,null);
                    if (record!=null) {
                        HashMap<String, String> target_station = (HashMap<String, String>) record.get(0);
                        Log.e("NEW TRACKING", target_station.get("STATION_NAME") + ".");
                        callThreads(target_station);
                    }
                }
            }
            cta_dataBase.close();
        });
    }




    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }


    private void callThreads(HashMap<String, String> target_station){
        message.setTARGET_MAP_ID(target_station.get("MAP_ID"));
        message.setDir(contact.getTrain_dir());
        message.setTarget_name(target_station.get("STATION_NAME"));
        message.setTarget_type(contact.getTrainLine());
        message.keepSending(true);
        message.setTarget_station(target_station);
        api_caller = new API_Caller_Thread(message);
        content_parser = new Content_Parser_Thread(Maincontext,handler,message);
        t1 = new Thread(api_caller);
        t2 = new Thread(content_parser);
        message.setT1(t1);
        message.setT2(t2);
        message.setContent_parser_thread(content_parser);
        message.setApi_caller_thread(api_caller);
        message.getT1().start();
        message.getT2().start();

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
