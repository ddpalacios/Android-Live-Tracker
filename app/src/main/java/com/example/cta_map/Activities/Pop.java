package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Pop extends Activity {
    TimePicker timePicker;
    private String original_week;
    CheckBox mon, tues, wen, thur, fri, sat, sun;
    private ArrayAdapter<String> adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow_2);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        getWindow().setLayout((int) (dm.widthPixels*.85), (int) (dm.heightPixels ));
        Button close, save;
        Spinner line_selection = findViewById(R.id.line_selection);
        final ArrayAdapter<CharSequence>[] adapter = new ArrayAdapter[]{ArrayAdapter.createFromResource(this,
                R.array.line_names, android.R.layout.simple_spinner_item)};
        adapter[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        line_selection.setAdapter(adapter[0]);
        AutoCompleteTextView station_lookup = findViewById(R.id.autoCompleteTextView);
        timePicker = findViewById(R.id.timePicker1);
        mon = findViewById(R.id.monday);
        tues = findViewById(R.id.tuesday);
        wen = findViewById(R.id.wensday);
        thur = findViewById(R.id.thursday);
        fri = findViewById(R.id.friday);
        sat = findViewById(R.id.saterday);
        sun = findViewById(R.id.sunday);
        Alarm new_alarm  = new Alarm();
        Intent intent = getIntent();

        final int[] count = {0};
        HashMap<String, String> selected_alarm = (HashMap<String, String>) intent.getSerializableExtra("alarm");
        if (selected_alarm != null){
            new_alarm.setMin(selected_alarm.get("MIN"));
            new_alarm.setHour(selected_alarm.get("HOUR"));
            new_alarm.setIsRepeating(Integer.parseInt(selected_alarm.get("WILL_REPEAT")));
            new_alarm.setAlarm_id(selected_alarm.get("ALARM_ID"));
            new_alarm.setMap_id(selected_alarm.get("MAP_ID"));
            new_alarm.setStation_name(selected_alarm.get("STATION_NAME"));
            new_alarm.setStationType(selected_alarm.get("STATION_TYPE"));
            new_alarm.setTime(selected_alarm.get("TIME"));
            new_alarm.setWeekLabel(selected_alarm.get("WEEK_LABEL"));
            original_week = selected_alarm.get("WEEK_LABEL");
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+new_alarm.getMap_id()+"'", null,null);
            HashMap<String, String> target_station = (HashMap<String, String>) record.get(0);


            timePicker.setMinute(Integer.parseInt(new_alarm.getMin()));
            timePicker.setHour(Integer.parseInt(new_alarm.getHour()));
            String[] weekLabels = Objects.requireNonNull(selected_alarm.get("WEEK_LABEL")).split(",");
            for (String day_of_week : weekLabels) {

                if (day_of_week.equals("mon")) {
                    mon.setChecked(true);
                    new_alarm.setMon(1);
                }
                if (day_of_week.equals("tues")) {
                    tues.setChecked(true);
                    new_alarm.setTues(1);
                }
                if (day_of_week.equals("wen")) {
                    wen.setChecked(true);
                    new_alarm.setWens(1);
                }
                if (day_of_week.equals("thu")) {
                    thur.setChecked(true);
                    new_alarm.setThur(1);
                }
                if (day_of_week.equals("fri")) {
                    fri.setChecked(true);
                    new_alarm.setFri(1);
                }
                if (day_of_week.equals("sat")) {
                    sat.setChecked(true);
                    new_alarm.setSat(1);
                }
                if (day_of_week.equals("sun")) {
                    sun.setChecked(true);
                    new_alarm.setSun(1);
                }
            }
            Chicago_Transits chicago_transits = new Chicago_Transits();
            line_selection.setSelection(chicago_transits.getSpinnerPosition(selected_alarm.get("STATION_TYPE")));
            if (target_station!=null){
            station_lookup.setText(target_station.get("STOP_NAME"));
        }
            cta_dataBase.close();
    }else{
            line_selection.setSelection(0);

        }





        line_selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String station_type = parent.getItemAtPosition(position).toString();
                new_alarm.setStationType(station_type);
                if (count[0] > 0){
                    station_lookup.setText("");
                }
                count[0] +=1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ArrayAdapter<String>[] Textadapter = new ArrayAdapter[]{null};
        station_lookup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String station_type = line_selection.getSelectedItem().toString();
                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                Chicago_Transits chicago_transits = new Chicago_Transits();
                ArrayList<Object> target_stations = cta_dataBase.excecuteQuery("*", "CTA_STOPS", chicago_transits.TrainLineKeys(station_type).toUpperCase() + " = '1'" +" AND STATION_NAME",  s.toString(), null);
                if (target_stations != null){
                    List<String> items = new ArrayList<>();
                    for (Object record : target_stations) {
                        HashMap<String, String> station = (HashMap<String, String>) record;
                        Log.e("station", " " + station.get("STATION_NAME"));
                        items.add("#"+station.get("MAP_ID")+". "+station.get("STOP_NAME"));

                    }
                    String[] myArray = new String[items.size()];
                    items.toArray(myArray);
                    Textadapter[0] = new ArrayAdapter<String>(Pop.this,
                            android.R.layout.simple_list_item_1, myArray);

                    station_lookup.setAdapter(Textadapter[0]);
                    Textadapter[0].notifyDataSetChanged();



                }



                cta_dataBase.close();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        station_lookup.setOnItemClickListener((parent, view, position, id) -> {
            String item  = Textadapter[0].getItem(position);
            Toast.makeText(getApplicationContext(), item + " Selected", Toast.LENGTH_SHORT).show();
            String map_id = StringUtils.substringBetween(item, "#", ".").trim();
            new_alarm.setMap_id(map_id);
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            ArrayList<Object> station_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+new_alarm.getMap_id()+"'", null,null);
            HashMap<String,String> target_station = (HashMap<String, String>) station_record.get(0);
            new_alarm.setStation_name(target_station.get("STATION_NAME"));
            cta_dataBase.close();
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(v -> {
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            Log.e("HOUR", timePicker.getHour()+"");

            String weekLabel ="";

            if (sun.isChecked()) {
                new_alarm.setSun(1);
                weekLabel = weekLabel +" sun ";
            }else{
                new_alarm.setSun(0);
            }
            if (mon.isChecked()){
                new_alarm.setMon(1);
                weekLabel = weekLabel +" mon ";
            }else{
                new_alarm.setMon(0);
            }if (tues.isChecked()){
                new_alarm.setTues(1);
                weekLabel = weekLabel +" tues ";
            }else{
                new_alarm.setTues(0);
            }if (wen.isChecked()){
                new_alarm.setWens(1);
                weekLabel = weekLabel +" wen ";
            }else{
                new_alarm.setWens(0);
            }   if (thur.isChecked()){
                new_alarm.setThur(1);
                weekLabel = weekLabel +" thu ";
            }else{
                new_alarm.setThur(0);
            }   if (fri.isChecked()){
                new_alarm.setFri(1);
                weekLabel = weekLabel +" fri ";
            }else{
                new_alarm.setFri(0);
            }   if (sat.isChecked()){
                new_alarm.setSat(1);
                weekLabel = weekLabel +" sat ";
            } else{
                new_alarm.setSat(0);
            }

            int hour;
            String hour_of_day, min;
            if (timePicker.getHour() == 0){
                hour = 12;
                hour_of_day = "AM";
            }else if (timePicker.getHour() == 12){
                hour = timePicker.getHour();
                hour_of_day = "PM";
            }

            else if (timePicker.getHour() > 12)
            {
                hour = timePicker.getHour()-12;
                hour_of_day = "PM";

            }
            else{
                hour = timePicker.getHour();
                hour_of_day = "AM";
            }
            if (timePicker.getMinute() < 10){
                min = "0"+timePicker.getMinute();
            }else{
                min = timePicker.getMinute()+"";
            }


            new_alarm.setTime(hour +":"+min+" "+ hour_of_day);
            new_alarm.setHour(timePicker.getHour()+"");
            new_alarm.setMin(timePicker.getMinute()+"");
            weekLabel = weekLabel.trim().replaceAll(" ", ",");
            weekLabel = weekLabel.replaceAll(",,", ",");
            new_alarm.setWeekLabel(weekLabel);


            if (selected_alarm !=null) {
                ArrayList<Object> pending_alarm = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '" + new_alarm.getAlarm_id() + "'", null, null);
                if (pending_alarm !=null){
                    cta_dataBase.update("ALARMS", "HOUR", new_alarm.getHour(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    cta_dataBase.update("ALARMS", "MIN", new_alarm.getMin(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    cta_dataBase.update("ALARMS", "TIME", new_alarm.getTime(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    cta_dataBase.update("ALARMS", "MAP_ID", new_alarm.getMap_id(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    cta_dataBase.update("ALARMS", "STATION_TYPE", new_alarm.getStationType(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    cta_dataBase.update("ALARMS", "STATION_NAME", new_alarm.getStationName(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    if (!original_week.equals(new_alarm.getWeekLabel())){
                        cancelAlarm((HashMap<String, String>) pending_alarm.get(0));
                    }
                    cta_dataBase.update("ALARMS", "WEEK_LABEL", new_alarm.getWeekLabel(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
                    pending_alarm = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '" + new_alarm.getAlarm_id() + "'", null, null);
                    HashMap<String, String> updated_alarm = (HashMap<String, String>) pending_alarm.get(0);
                    if (Objects.equals(updated_alarm.get("WILL_REPEAT"), "1")){
                        startAlarm(updated_alarm);
                    }



                }
                startActivity(new Intent(Pop.this, MainActivity.class));


            }else{
           new_alarm.setIsRepeating(0);
            cta_dataBase.commit(new_alarm, "ALARMS");
                cta_dataBase.close();
                startActivity(new Intent(Pop.this, MainActivity.class));
            }
            cta_dataBase.close();
        });
        close = findViewById(R.id.cancel_button);
        close.setOnClickListener(v -> finish());
    }

    private void cancelAlarm(HashMap<String, String > alarm_record){
        String[] weekLabels = Objects.requireNonNull(alarm_record.get("WEEK_LABEL")).split(",");
        int count = 0;
        for (String day_of_week : weekLabels) {
            count += 1;
            Intent intent = new Intent(getApplicationContext(), MyBroadCastReciever.class);
            int pending_intent_id = Integer.parseInt(Objects.requireNonNull(alarm_record.get("ALARM_ID"))+count);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    pending_intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
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

            Intent intent = new Intent(getApplicationContext(), MyBroadCastReciever.class);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    Integer.parseInt(Objects.requireNonNull(alarm_record.get("ALARM_ID"))+count), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7, pendingIntent);

        }


    }

}
