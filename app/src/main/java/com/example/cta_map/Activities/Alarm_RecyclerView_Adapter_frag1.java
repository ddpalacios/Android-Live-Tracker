package com.example.cta_map.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    int position1;
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
        position1 = position;
        alarm = this.alarm_list.get(position);
        final float scale = context.getResources().getDisplayMetrics().density;



        Chicago_Transits chicago_transits = new Chicago_Transits();
        holder.train_image.setImageResource(chicago_transits.getTrainImage(alarm.getStationType()));
        holder.main_title.setText(alarm.getStationName());
        holder.main_title.setWidth((int) (100 * scale + 0.5f));
        holder.isSch.setText(alarm.getTime());
        if (alarm.getIsOn().equals("1")) {
            holder.alarm_switch.setChecked(true);
            holder.alarm_switch.setText("On");
        }else{
            holder.alarm_switch.setChecked(false);
            holder.alarm_switch.setText("Off");
        }
        String[] l = alarm.getWeekLabel().split(",");
        if (l[l.length-1].equals(",")){
            l[l.length-1] = "";

        }

        StringBuilder formatted_label = new StringBuilder();
        for (String day : l){
            formatted_label.append(day);
        }
        holder.status_label.setText(formatted_label);
        holder.train_line.setText(alarm.getStationType()+" line");
        holder.train_line.setTextColor(Color.parseColor(NewAlarmSetUp.getColor(alarm.getStationType())));
        holder.isSch.setTextColor((Color.parseColor(NewAlarmSetUp.getColor(alarm.getStationType()))));






        holder.item.setOnClickListener(v -> {
           alarm = alarm_list.get(position);
            Message message = MainActivity.message;
            chicago_transits.StopThreads(message, context);
            Intent intent1 = new Intent(context, NewAlarmSetUp.class);
            intent1.putExtra("alarm", alarm);

            context.startActivity(intent1);

        });
        holder.item.setOnLongClickListener(v -> {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Remove Alarm");
            alarm = alarm_list.get(position);
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        ArrayList<Object> alarm_record1 = cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, "ALARM_ID = '"+alarm.getAlarm_id()+"'", null,null);
                        if (alarm_record1 !=null){
                            String[] values = new String[]{alarm.getAlarm_id()};
                            cta_dataBase.delete_record(CTA_DataBase.ALARMS, "ALARM_ID = ?", values );
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
        holder.alarm_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
            alarm = alarm_list.get(position);


            if (isChecked){
                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is ON");
                cta_dataBase.update(CTA_DataBase.ALARMS,  CTA_DataBase.WILL_REPEAT, "1", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                cta_dataBase.update(CTA_DataBase.ALARMS, CTA_DataBase.ALARM_IS_ON, "1", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                holder.alarm_switch.setText("On");
                startAlarm(alarm);
            }else{
                Log.e("RECORD ALARM", holder.main_title.getText().toString()+" is OFF");
                cta_dataBase.update(CTA_DataBase.ALARMS, CTA_DataBase.WILL_REPEAT, "0", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                cta_dataBase.update(CTA_DataBase.ALARMS, CTA_DataBase.ALARM_IS_ON, "0", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                holder.alarm_switch.setText("Off");

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
        TextView main_title, train_eta, isSch, train_line, status_label;
        ImageView train_image;
        Switch alarm_switch;
        CardView item;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            item = (CardView) itemView.findViewById(R.id.list_item);

            train_image = (ImageView) itemView.findViewById(R.id.train_image);
            main_title = (TextView) itemView.findViewById(R.id.title_item);
            train_line = (TextView) itemView.findViewById(R.id.train_line_subtitle);
            status_label = (TextView) itemView.findViewById(R.id.status_label);
            isSch = (TextView) itemView.findViewById(R.id.isSch);
            alarm_switch = (Switch) itemView.findViewById(R.id.switch1);


        }
    }
}

