package com.example.cta_map.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.RecordView;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class Alarm_RecyclerView_Adapter_frag1 extends RecyclerView.Adapter<Alarm_RecyclerView_Adapter_frag1.ItemHolder>  {
    ArrayList<RecordView> contactsList;
    Context context;
    RecordView contact;
    public Alarm_RecyclerView_Adapter_frag1(ArrayList<RecordView> contactsList, Context myContext){
        this.contactsList = contactsList;
        this.context = myContext;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.alarm_card_view_layout, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        contact = this.contactsList.get(position);
        CTA_DataBase cta_dataBase = new CTA_DataBase(context.getApplicationContext());
        holder.t1.setText(contact.getTitle1());
        holder.t2.setText(contact.getTitle2());
        holder.main_title.setText(contact.getMain_title());
        holder.imageView.setImageResource(contact.getImage());
        ArrayList<Object> alarm_record = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '"+contact.getAlarm_id()+"'", null,null);
         HashMap<String, String> current_alarm = null;
        if (alarm_record != null){
            current_alarm = (HashMap<String, String>) alarm_record.get(0);
            if (current_alarm.get("WILL_REPEAT").equals("1")){
                holder.willRepeat.setChecked(true);
            }else{
                holder.willRepeat.setChecked(false);
            }

        }




        holder.record_item.setOnClickListener(v -> {
            RecordView record = contactsList.get(position);
            Intent intent = new Intent(context, MyBroadCastReciever.class);
            ArrayList<Object> alarm_record1 = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '"+record.getAlarm_id()+"'", null,null);
            HashMap<String, String > selected_alarm = ( HashMap<String, String> ) alarm_record1.get(0);
            String[] weekLabels = Objects.requireNonNull(selected_alarm.get("WEEK_LABEL")).split(",");
            int count =0;
            for (String day_of_week : weekLabels){
                count+=1;
            boolean alarmUp = (PendingIntent.getBroadcast(context, Integer.parseInt(record.getAlarm_id()+count),
                   intent,
                    PendingIntent.FLAG_NO_CREATE) != null);

            if (alarmUp)
            {
                Log.d("ALARM MANAGER", "Alarm: "+record.getAlarm_id()+count+" is already active");
            }else{
                Log.d("ALARM MANAGER", "No Alarm for: "+record.getAlarm_id()+count);

            }
         }
            Intent intent1 = new Intent(context, Pop.class);
            intent1.putExtra("alarm", selected_alarm);

            context.startActivity(intent1);

        });

        holder.record_item.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Remove Alarm");
            contact = contactsList.get(position);
            builder.setMessage("Delete "+ contact.getMain_title()+" for "+contact.getTitle1() +" - "+ contact.getTitle2()+ "?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        ArrayList<Object> alarm_record1 = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '"+contact.getAlarm_id()+"'", null,null);
                        if (alarm_record1 !=null){
                            String[] values = new String[]{contact.getAlarm_id()};
                            cta_dataBase.delete_record("ALARMS",
                                    "ALARM_ID = ?", values );
                            contactsList.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            cancelAlarm((HashMap<String, String>) alarm_record1.get(0));
                        }


                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });
        HashMap<String, String> finalCurrent_alarm = current_alarm;
        holder.willRepeat.setOnClickListener(v -> {
            RecordView alarmRecord = contactsList.get(position);
            if (holder.willRepeat.isChecked()){
                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is ON");
                cta_dataBase.update("ALARMS", "WILL_REPEAT", "1", "ALARM_ID = '"+alarmRecord.getAlarm_id()+"'");
                startAlarm(finalCurrent_alarm);

            }else{
                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is OFF");
                cta_dataBase.update("ALARMS", "WILL_REPEAT", "0", "ALARM_ID = '"+alarmRecord.getAlarm_id()+"'");
                cancelAlarm(finalCurrent_alarm);

            }



        });

    cta_dataBase.close();
    }


    private void cancelAlarm(HashMap<String, String > alarm_record){
        String[] weekLabels = Objects.requireNonNull(alarm_record.get("WEEK_LABEL")).split(",");
        int count = 0;
        for (String day_of_week : weekLabels) {
            count += 1;
            Intent intent = new Intent(context, MyBroadCastReciever.class);
            int pending_intent_id = Integer.parseInt(Objects.requireNonNull(alarm_record.get("ALARM_ID"))+count);
            Log.e("ALARMS", "Cancelled: "+ pending_intent_id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                pending_intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAlarm(HashMap<String, String > alarm_record){

        int hours =  Integer.parseInt(Objects.requireNonNull(alarm_record.get("HOUR")));
        int minutes = Integer.parseInt(Objects.requireNonNull(alarm_record.get("MIN")));
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String[] weekLabels = Objects.requireNonNull(alarm_record.get("WEEK_LABEL")).split(",");
        int count = 0;
        for (String day_of_week : weekLabels){
            count+=1;
            Calendar cal = Calendar.getInstance();
            int day = chicago_transits.getDayOfWeekNum(day_of_week);

            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.DAY_OF_WEEK, day);
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, 0);

            if(cal.before(Calendar.getInstance())) {
                cal.add(Calendar.DATE,7 );
                Log.e("Alarm", "Day: "+day_of_week +" Will first fire on: "+cal.get(Calendar.DATE)+ " at "+ hours + ":"+minutes+ ". Then every 7 days after");


            }else{
                Log.e("Alarm", "Day: "+day_of_week +" Will first fire on: "+cal.get(Calendar.DATE)+ " at "+ hours + ":"+minutes+ ". Then every 7 days after");
            }

            Intent intent = new Intent(context, MyBroadCastReciever.class);
            intent.putExtra("map_id", alarm_record.get("MAP_ID"));
            intent.putExtra("station_type", alarm_record.get("STATION_TYPE"));
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    Integer.parseInt(Objects.requireNonNull(alarm_record.get("ALARM_ID"))+count), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7, pendingIntent);

        }


    }




    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1,t2, main_title;
        ImageView imageView;
        Switch willRepeat;
        CardView record_item;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.title_1);
            t2 = (TextView) itemView.findViewById(R.id.title_2);
            main_title = itemView.findViewById(R.id.main_title);
            willRepeat = itemView.findViewById(R.id.activate_alarm_switch);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
            record_item = (CardView) itemView.findViewById(R.id.list_item);
        }
    }
}

