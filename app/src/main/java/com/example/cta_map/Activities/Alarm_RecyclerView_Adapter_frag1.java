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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.MainNotificationService;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Backend.Threading.MyBroadCastReciever;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Objects;

public class Alarm_RecyclerView_Adapter_frag1 extends RecyclerView.Adapter<Alarm_RecyclerView_Adapter_frag1.ItemHolder>  {
    ArrayList<Alarm> alarm_list;
    Context context;
    Alarm alarm;
    public Alarm_RecyclerView_Adapter_frag1(ArrayList<Alarm>alarm_list, Context myContext){
        this.alarm_list = alarm_list;
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
        alarm = this.alarm_list.get(position);
//        CTA_DataBase cta_dataBase = new CTA_DataBase(context.getApplicationContext());
        Chicago_Transits chicago_transits = new Chicago_Transits();
//        holder.t1.setText(alarm.getWeekLabel());
        holder.subtitle.setText(alarm.getWeekLabel()+" | "+alarm.getTime());
        holder.main_title.setText(alarm.getStationName());
        holder.imageView.setImageResource(chicago_transits.getTrainImage(alarm.getStationType()));
        if (alarm.getIsRepeating() == 1){
            holder.willRepeat.setChecked(true);
        }else{
            holder.willRepeat.setChecked(false);
        }



        holder.record_item.setOnClickListener(v -> {
           alarm = alarm_list.get(position);

            Message message = MainActivity.message;
            int res = new Chicago_Transits().cancelRunningThreads(message); // cancel current running threads
            if (res > 0) {
                if (message.getT1().isAlive()) {
                    message.getT1().interrupt();
                } else {
                    MainActivity.LogMessage("Thread not alive.");
                }
                try {
                    API_Caller_Thread.msg.getT1().join(MainActivity.TIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }}

            if (new Chicago_Transits().isMyServiceRunning(context,new MainNotificationService().getClass())){
                new Chicago_Transits().stopService(context);
            }

            Intent intent1 = new Intent(context, NewAlarmSetUp.class);
            intent1.putExtra("alarm", alarm);

            context.startActivity(intent1);

        });
//
        holder.record_item.setOnLongClickListener(v -> {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Remove Alarm");
            alarm = alarm_list.get(position);
            builder.setMessage("Delete "+ alarm.getStationName()+" for "+alarm.getTime() +" - "+ alarm.getWeekLabel()+ "?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        ArrayList<Object> alarm_record1 = cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, "ALARM_ID = '"+alarm.getAlarm_id()+"'", null,null);
                        if (alarm_record1 !=null){
                            String[] values = new String[]{alarm.getAlarm_id()};
                            cta_dataBase.delete_record(CTA_DataBase.ALARMS,
                                    "ALARM_ID = ?", values );
                            alarm_list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            cancelAlarm(alarm);
                        }


                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            cta_dataBase.close();
            return false;
        });
        holder.willRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            if (isChecked){
                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is ON");
                cta_dataBase.update("ALARMS", "WILL_REPEAT", "1", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                startAlarm(alarm);
            }else{
                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is OFF");
                cta_dataBase.update("ALARMS", "WILL_REPEAT", "0", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                cancelAlarm(alarm);
            }
        cta_dataBase.close();
        });


//        holder.willRepeat.setOnClickListener(v -> {
//            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
//            if (holder.willRepeat.isChecked()){
//                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is ON");
//                cta_dataBase.update("ALARMS", "WILL_REPEAT", "1", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
//                startAlarm(finalCurrent_alarm);
//
//            }else{
//                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is OFF");
//                cta_dataBase.update("ALARMS", "WILL_REPEAT", "0", "ALARM_ID = '"+alarmRecord.getAlarm_id()+"'");
//                cancelAlarm(finalCurrent_alarm);
//
//            }
//            cta_dataBase.close();
//
//        });
    }


    private void cancelAlarm(Alarm alarm_record){
        String[] weekLabels = Objects.requireNonNull(alarm_record.getWeekLabel()).split(",");
        int count = 0;
        for (String day_of_week : weekLabels) {
            count += 1;
            Intent intent = new Intent(context, MyBroadCastReciever.class);
            int pending_intent_id = Integer.parseInt(Objects.requireNonNull(alarm_record.getAlarm_id())+count);
            Log.e("ALARMS", "Cancelled: "+ pending_intent_id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                pending_intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAlarm(Alarm alarm_record){
        Chicago_Transits chicago_transits = new Chicago_Transits();
        String[] weekLabels = Objects.requireNonNull(alarm_record.getWeekLabel()).split(",");
        int count = 0;
        for (String day_of_week : weekLabels){
            count+=1;
            Integer day = chicago_transits.getDayOfWeekNum(day_of_week);
            String alarm_id = alarm_record.getAlarm_id()+count; // Create Uniqueness
            if (day!=null) {
                chicago_transits.scheduleAlarm(context, day, alarm_record, alarm_id, alarm_record.getAlarm_id());
            }
        }
    }






    @Override
    public int getItemCount() {
        return this.alarm_list.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1,subtitle, main_title;
        ImageView imageView;
        Switch willRepeat;
        CardView record_item;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.title_1);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            main_title = itemView.findViewById(R.id.main_title);
            willRepeat = itemView.findViewById(R.id.activate_alarm_switch);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
            record_item = (CardView) itemView.findViewById(R.id.list_item);
        }
    }
}

