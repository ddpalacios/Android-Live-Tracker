package com.example.cta_map.Activities;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Backend.Threading.MyBroadCastReciever;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NewAlarmSetUp extends AppCompatActivity {
    TimePicker timePicker;
    private String original_week;
    public static ActionBar bar;
    CheckBox mon, tues, wen, thur, fri, sat, sun;
    RecyclerView recyclerView;
    Spinner main_dest_spinner;
    String main_train_line;
    CheckBox isrepeating;
    Menu menu;
    public static HashMap<String, String> tracking_station;
    ArrayAdapter<String> adapter1;
    public static Alarm alarm;
//    Button main_save_btn;
    TrainTimes_Adapter_frag trainTimes_adapter_frag;
    Boolean isNew = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = (Alarm) getIntent().getSerializableExtra("alarm");
        setContentView(R.layout.alarm_setup_popup_layout3);
        isrepeating = findViewById(R.id.willRepeat);
        bar = getSupportActionBar();
        TextView dest_name = findViewById(R.id.dest_name);
        bar.setDisplayHomeAsUpEnabled(true);
        mon = findViewById(R.id.monday);
        tues = findViewById(R.id.tuesday);
        wen = findViewById(R.id.wensday);
        thur = findViewById(R.id.thursday);
        fri = findViewById(R.id.friday);
        sat = findViewById(R.id.saterday);
        sun = findViewById(R.id.sunday);
        Button delete = findViewById(R.id.delete_button);
        tracking_station = (HashMap<String, String>) getIntent().getSerializableExtra("tracking_station");
        LocalDateTime now = LocalDateTime.now();
        TextView currentTime = findViewById(R.id.currentTime);
        Calendar rightNow = Calendar.getInstance();
        Intent intent = new Intent(NewAlarmSetUp.this, Timer.class);
        intent.putExtra("alarm", alarm);
        TextView changeTimeText = findViewById(R.id.changeTimeText); // Alarm set up
        changeTimeText.setOnClickListener(v -> startActivity(intent));
        currentTime.setOnClickListener(v -> startActivity(intent));
        String time_format;


        ActionBar bar = getSupportActionBar();
        assert bar != null;
        Chicago_Transits chicago_transits = new Chicago_Transits();

        if (alarm == null){
            setTitle("Finish New Alarm");

            int hour, min;
            alarm = new Alarm();
            alarm.setIsOn("1");
            alarm.setIsRepeating(1);
            dest_name.setText("To "+tracking_station.get("main_station_name"));
            alarm.setMap_id(tracking_station.get("target_map_id"));
            alarm.setStation_name(tracking_station.get("target_station_name"));
            alarm.setStationType(tracking_station.get("train_line"));
            alarm.setDirection(tracking_station.get("train_dir"));
            bar.setBackgroundDrawable(new ColorDrawable(chicago_transits.GetBackgroundColor(tracking_station.get("train_line"), getApplicationContext())));
            hour = rightNow.get(Calendar.HOUR_OF_DAY);
            min = rightNow.get(Calendar.MINUTE);
            time_format = getTimeFormat(hour, min); // Formats our current time
            currentTime.setText(time_format);
            alarm.setHour(hour+"");
            alarm.setMin(min+"");
            alarm.setTime(time_format);
            delete.setVisibility(View.GONE);

            alarm.setWeek_label_list(new ArrayList<Integer>());
        }else{
            setTitle("Edit Alarm");
            dest_name.setText("");
            bar.setBackgroundDrawable(new ColorDrawable(chicago_transits.GetBackgroundColor(alarm.getStationType(), getApplicationContext())));
            delete.setVisibility(View.VISIBLE);
            currentTime.setText(alarm.getTime());
            alarm.setWeek_label_list(new ArrayList<Integer>());

        }
        if (alarm.getIsRepeating()==1){
            isrepeating.setChecked(true);
        }else{
            isrepeating.setChecked(false);
        }




        int day = getDayNumberNew(now);
        if (alarm.getAlarm_id() == null) {
            if (day == 1) {
                mon.setChecked(true);
                addDay("mon", alarm, false);


            } else if (day == 2) {
                tues.setChecked(true);
                addDay("tue", alarm, false);


            } else if (day == 3) {
                wen.setChecked(true);
                addDay("wen", alarm, false);

            } else if (day == 4) {
                thur.setChecked(true);
                addDay("thu", alarm, false);

            } else if (day == 5) {
                fri.setChecked(true);
                addDay("fri", alarm, false);

            } else if (day == 6) {
                sat.setChecked(true);
                addDay("sat", alarm, false);


            } else if (day == 7) {
                sun.setChecked(true);
                addDay("sun", alarm, false);


            }
        }
        PopulateCardBuilder(tracking_station, alarm.getTime());



    }





    @RequiresApi(api = Build.VERSION_CODES.O)
    private void PopulateCardBuilder(HashMap<String, String> tracking_station, String time ) {


        TextView main_title, train_eta, isSch, train_line, status_label;
        ImageView imageView;
        Switch alarm_switch;
        CardView item;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        final float scale = getResources().getDisplayMetrics().density;
        train_line = (TextView) findViewById(R.id.train_line_subtitle);
        isSch = (TextView) findViewById(R.id.isSch);
        status_label = (TextView) findViewById(R.id.status_label);
        alarm_switch = (Switch) findViewById(R.id.switch1);


        item = (CardView) findViewById(R.id.list_item);
        main_title = (TextView) findViewById(R.id.title_item);
        imageView = (ImageView) findViewById(R.id.train_image);
        main_title.setWidth((int) (100 * scale + 0.5f));
        if (tracking_station!= null){
            main_title.setText(tracking_station.get("target_station_name")); // Set its main title
            train_line.setText(tracking_station.get("train_line") + " line");
            imageView.setImageResource(chicago_transits.getTrainImage(tracking_station.get("train_line")));
            train_line.setTextColor(Color.parseColor(getColor(tracking_station.get("train_line"))));
            isSch.setTextColor(Color.parseColor(getColor(tracking_station.get("train_line"))));
        }else{
            main_title.setText(alarm.getStationName()); // Set its main title
            train_line.setText(alarm.getStationType() + " line");
            imageView.setImageResource(chicago_transits.getTrainImage(alarm.getStationType()));
            train_line.setTextColor(Color.parseColor(getColor(alarm.getStationType())));
            isSch.setTextColor(Color.parseColor(getColor(alarm.getStationType())));
        }
        isSch.setText(time);


        if (alarm.getIsOn().equals("1")){
            alarm_switch.setChecked(true);
            alarm_switch.setText("On");

        }else{
            alarm_switch.setChecked(false);
            alarm_switch.setText("Off");
        }

        if (alarm.getWeekLabel() != null){
            String[] week = alarm.getWeekLabel().split(",");
            for (String day : week){
                day = day.toLowerCase().trim();
                if (day.equals("mon")){
                    alarm.getWeek_label_list().add(1);
                    mon.setChecked(true);
                }else if (day.equals("tue")){
                    alarm.getWeek_label_list().add(2);
                    tues.setChecked(true);
                }else if (day.equals("wen")){
                    alarm.getWeek_label_list().add(3);
                    wen.setChecked(true);
                }else if (day.equals("thu")){
                    alarm.getWeek_label_list().add(4);
                    thur.setChecked(true);
                }else if (day.equals("fri")){
                    alarm.getWeek_label_list().add(5);
                    fri.setChecked(true);
                }else if (day.equals("sat")){
                    alarm.getWeek_label_list().add(6);
                    sat.setChecked(true);
                }else if (day.equals("sun")){
                    alarm.getWeek_label_list().add(0);
                    sun.setChecked(true);
                }
            }
        }

        isrepeating.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                alarm.setIsRepeating(1);
            }else{
                alarm.setIsRepeating(0);
            }
        });
        sun.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("sun", alarm, false);
            }else{
                addDay("sun", alarm, true);
            }
        });
        mon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("mon", alarm, false);
            }else{
                addDay("mon", alarm,true);

            }
        });
        tues.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("tue", alarm, false);
            }else{
                addDay("tue", alarm,true);

            }
        });
        alarm_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CTA_DataBase  cta_dataBase = new CTA_DataBase(getApplicationContext());
            if (isChecked){
                alarm_switch.setText("On");
                alarm.setIsOn("1");
                cta_dataBase.update(CTA_DataBase.ALARMS,  CTA_DataBase.WILL_REPEAT, "1", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                cta_dataBase.update(CTA_DataBase.ALARMS, CTA_DataBase.ALARM_IS_ON, "1", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
            }else{
                alarm_switch.setText("Off");
                alarm.setIsOn("0");
                cta_dataBase.update(CTA_DataBase.ALARMS,  CTA_DataBase.WILL_REPEAT, "0", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
                cta_dataBase.update(CTA_DataBase.ALARMS, CTA_DataBase.ALARM_IS_ON, "0", "ALARM_ID = '"+alarm.getAlarm_id()+"'");
            }
            cta_dataBase.close();
        });

        wen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("wen", alarm,false);
            }else{
                addDay("wen", alarm,true);

            }
        });
        thur.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("thu", alarm,false);
            }else{
                addDay("thu", alarm,true);

            }
        });
        fri.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("fri", alarm,false);
            }else{
                addDay("fri", alarm,true);

            }
        });
        sat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("sat", alarm,false);
            }else{
                addDay("sat", alarm,true);
            }
        });

        Button save_button = findViewById(R.id.main_save_button);
        Button delete_button = findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NewAlarmSetUp.this);

                builder.setCancelable(true);
                builder.setTitle("Remove Alarm");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Confirm",
                        (dialog, which) -> {
                            ArrayList<Object> alarm_record1 = cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, "ALARM_ID = '"+alarm.getAlarm_id()+"'", null,null);
                            if (alarm_record1 !=null){
                                String[] values = new String[]{alarm.getAlarm_id()};
                                cta_dataBase.delete_record(CTA_DataBase.ALARMS, "ALARM_ID = ?", values );
                                cancelAlarm(alarm);
                            }
                            startActivity(new Intent(NewAlarmSetUp.this, MainActivity.class));

                        });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                cta_dataBase.close();
            }
        });

        save_button.setOnClickListener(v -> {
            if (alarm.getWeekLabel() == null || alarm.getWeekLabel().replaceAll(",","").trim().equals("")){
                Toast.makeText(getApplicationContext(), "A day of the week MUST be selected", Toast.LENGTH_LONG).show();
                return;
            }

            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            if (alarm.getAlarm_id() == null){ // if it is a new alarm
                cta_dataBase.commit(alarm, CTA_DataBase.ALARMS);
                Toast.makeText(getApplicationContext(), "Alarm was saved!", Toast.LENGTH_LONG).show();
                ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.ALARMS, null,null,null);
                alarm = (Alarm) record.get(record.size()-1);

            }else{
                cta_dataBase.update("ALARMS", "HOUR", alarm.getHour(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
                cta_dataBase.update("ALARMS", "MIN", alarm.getMin(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
                cta_dataBase.update("ALARMS", "TIME", alarm.getTime(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
                cta_dataBase.update("ALARMS", "MAP_ID", alarm.getMap_id(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
                cta_dataBase.update("ALARMS", CTA_DataBase.WILL_REPEAT, alarm.getIsRepeating()+"", "ALARM_ID = '" + alarm.getAlarm_id() + "'");
                cta_dataBase.update("ALARMS", CTA_DataBase.WEEK_LABEL, getWeeklyLabel(alarm)+"", "ALARM_ID = '" + alarm.getAlarm_id() + "'");
                Toast.makeText(getApplicationContext(), "Alarm to "+alarm.getStationName() +" for "+ alarm.getTime() + " was updated!", Toast.LENGTH_LONG).show();


            }

            String[] days = alarm.getWeekLabel().split(",");
                int count = 0;
                for (String day_of_week : days){
                    count+=1;
                    Integer day_to_int = chicago_transits.getDayOfWeekNum(day_of_week);
                    if(day_to_int!=null) {
                        chicago_transits.scheduleAlarm(getApplicationContext(), day_to_int, alarm, alarm.getAlarm_id() + count, alarm.getAlarm_id());
                    }
                }
            startActivity(new Intent(NewAlarmSetUp.this, MainActivity.class));

            cta_dataBase.close();






        });






    }

    private void cancelAlarm(Alarm alarm_record){
        String[] weekLabels = Objects.requireNonNull(alarm_record.getWeekLabel()).split(",");
        int count = 0;
        for (String day_of_week : weekLabels) {
            count += 1;
            Intent intent = new Intent(getApplicationContext(), MyBroadCastReciever.class);
            int pending_intent_id = Integer.parseInt(Objects.requireNonNull(alarm_record.getAlarm_id())+count);
            Log.e("ALARMS", "Cancelled: "+ pending_intent_id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    pending_intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();

        }

    }



    public static String getColor(String train_line) {
        HashMap<String, String> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red", "#F44336");
        TrainLineKeyCodes.put("blue", "#384cff");
        TrainLineKeyCodes.put("brown", "#a34700");
        TrainLineKeyCodes.put("green", "#0B8043");
        TrainLineKeyCodes.put("orange", "#ffad33");
        TrainLineKeyCodes.put("yellow", "#b4ba0b");

        TrainLineKeyCodes.put("pink", "#ff66ed");
        TrainLineKeyCodes.put("purple", "#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());
    }


        @RequiresApi(api = Build.VERSION_CODES.O)
    public static Integer getDayNumberNew(LocalDateTime date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getValue();
    }

//        mon = findViewById(R.id.monday);
//        tues = findViewById(R.id.tuesday);
//        wen = findViewById(R.id.wensday);
//        thur = findViewById(R.id.thursday);
//        fri = findViewById(R.id.friday);
//        sat = findViewById(R.id.saterday);
//        sun = findViewById(R.id.sunday);
//        alarm = (Alarm) getIntent().getSerializableExtra("alarm");
//        TextView currentTime = findViewById(R.id.currentTime);
//        bar = getSupportActionBar();
//        bar.setDisplayHomeAsUpEnabled(true);
////        main_save_btn = findViewById(R.id.main_save_button);
//
//        if (alarm != null && alarm.getAlarm_id() ==null){
//            setTitle("New Alarm");
//            buildView(alarm);
////            main_save_btn.setVisibility(View.GONE);
//
//        } else if (alarm != null && alarm.getAlarm_id() !=null){
//            bar.setTitle(alarm.getStationName());
//            buildView(alarm);
////            main_save_btn.setVisibility(View.VISIBLE);
//
//        } else{
//            // sets our text view
//            setTitle("New Alarm");
//            alarm = new Alarm();
//            alarm.setWeek_label_list(new ArrayList<>());
//            Calendar rightNow = Calendar.getInstance();
//            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
//            int min = rightNow.get(Calendar.MINUTE);
//            String time_format = getTimeFormat(hour, min); // Formats our current time
//            currentTime.setText(time_format);
//            bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor("Blue",getApplicationContext())));
////            main_save_btn.setVisibility(View.GONE);
//
//        }
//        initializeView(); // Sets up out intractable views
//        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, "BLUE = '1'",null,null);
//        ArrayList<Station> non_duplicated_station_list = new Chicago_Transits().removeDuplicates(record);
//        trainTimes_adapter_frag = new TrainTimes_Adapter_frag(getApplicationContext(),null,null, non_duplicated_station_list,recyclerView, alarm);
//        recyclerView.setAdapter(trainTimes_adapter_frag);
//        cta_dataBase.close();
//

    public static String getTimeFormat(int hour, int min) {


        String minutue_string = min + "";
        String hour_of_day;
        if (hour == 0){
            hour = 12;
            hour_of_day = "AM";
        }else if (hour == 12){
            hour = hour;
            hour_of_day = "PM";
        }

        else if (hour > 12)
        {
            hour = hour-12;
            hour_of_day = "PM";

        }
        else{
            hour = hour;
            hour_of_day = "AM";
        }
        if (min < 10){
           minutue_string = "0"+min;
        }else{
            minutue_string = min+"";
        }
        return hour+":"+minutue_string + " "+ hour_of_day;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeView() {
        TextView currentTime = findViewById(R.id.currentTime);
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        recyclerView = findViewById(R.id.all_station_lookup_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        main_train_line = "Blue";
        TextView changeTimeText = findViewById(R.id.changeTimeText);
        changeTimeText.setOnClickListener(v -> startActivity(
                new Intent(NewAlarmSetUp.this, Timer.class)
        ));

        CheckBox willRepeat = findViewById(R.id.willRepeat);
        Intent intent = new Intent(NewAlarmSetUp.this, Timer.class);
        intent.putExtra("alarm", alarm);
        currentTime.setOnClickListener(v -> startActivity(intent));
        String[] time_ = currentTime.getText().toString().split(":");
        String hour = time_[0];
        String min = time_[1].replaceAll("[a-zA-Z]", "").trim();

        alarm.setTime(currentTime.getText().toString());
        alarm.setHour(hour);
        alarm.setMin(min);
        alarm.setStationType("Blue"); // Changes on spinner change.
        alarm.setIsRepeating((willRepeat.isChecked() ? 1:0));




        cta_dataBase.close();

        willRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                alarm.setIsRepeating(1);
            }else{
                alarm.setIsRepeating(0);
            }
        });
        sun.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("sun", alarm, false);
            }else{
                addDay("sun", alarm, true);
            }
        });

        mon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("mon", alarm, false);
            }else{
                addDay("mon", alarm,true);

            }
        });

        tues.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("tue", alarm, false);
            }else{
                addDay("tue", alarm,true);

            }
        });

        wen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("wen", alarm,false);
            }else{
                addDay("wen", alarm,true);

            }
        });

        thur.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("thu", alarm,false);
            }else{
                addDay("thu", alarm,true);

            }
        });

        fri.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("fri", alarm,false);
            }else{
                addDay("fri", alarm,true);

            }
        });

        sat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                addDay("sat", alarm,false);
            }else{
                addDay("sat", alarm,true);
            }
        });
//
//
//        main_save_btn.setOnClickListener(v -> {
//            cta_dataBase.update("ALARMS", "HOUR", alarm.getHour(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
//            cta_dataBase.update("ALARMS", "MIN", alarm.getMin(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
//            cta_dataBase.update("ALARMS", "TIME", alarm.getTime(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
//            cta_dataBase.update("ALARMS", "MAP_ID", alarm.getMap_id(), "ALARM_ID = '" + alarm.getAlarm_id() + "'");
//            cta_dataBase.update("ALARMS", CTA_DataBase.WILL_REPEAT, alarm.getIsRepeating()+"", "ALARM_ID = '" + alarm.getAlarm_id() + "'");
//            cta_dataBase.update("ALARMS", CTA_DataBase.WEEK_LABEL, getWeeklyLabel(alarm)+"", "ALARM_ID = '" + alarm.getAlarm_id() + "'");
//            if (willRepeat.isChecked()){
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                String[] days = alarm.getWeekLabel().split(",");
//                int count = 0;
//                for (String day_of_week : days){
//                    count+=1;
//                    Integer day_to_int = chicago_transits.getDayOfWeekNum(day_of_week);
//                    if(day_to_int!=null) {
//                        chicago_transits.scheduleAlarm(getApplicationContext(), day_to_int, alarm, alarm.getAlarm_id() + count, alarm.getAlarm_id());
//                    }
//                }
//            }
//            Intent intent1 = new Intent(NewAlarmSetUp.this, MainActivity.class);
//            startActivity(intent1);
//
//
//
//        });


    }

    public static String getWeeklyLabel(Alarm alarm){
        ArrayList<Integer> week_label = alarm.getWeek_label_list();
        String label = "";
        if (week_label !=null){
        for (Integer day_int : week_label){
            if (day_int == 0){label = "sun, ";}
            if (day_int == 1){label = label+"mon, ";}
            if (day_int == 2){label = label+"tue, ";}
            if (day_int == 3){label = label+"wen, ";}
            if (day_int == 4){label = label+"thu, ";}
            if (day_int == 5){label = label+"fri, ";}
            if (day_int == 6){label = label+"sat";}
        }
        if (week_label.size() ==1){
            label.replaceAll(",", "");
        }
        }


        return label;


    }

    private void addDay(String day_of_week, Alarm alarm, boolean remove) {
        TextView status_label = (TextView) findViewById(R.id.status_label);

        HashMap<String, Integer> day_to_int = new HashMap<>();
        day_to_int.put("sun", 0);
        day_to_int.put("mon", 1);
        day_to_int.put("tue", 2);
        day_to_int.put("wen", 3);
        day_to_int.put("thu", 4);
        day_to_int.put("fri", 5);
        day_to_int.put("sat", 6);

            ArrayList<Integer> week_label = alarm.getWeek_label_list();
            if (!remove) {
                week_label.add(day_to_int.get(day_of_week));
            }else{
                week_label.remove(day_to_int.get(day_of_week));
            }

            Set<Integer> set = new HashSet<>(week_label);
            week_label.clear();
            week_label.addAll(set);

        Collections.sort(week_label);
        alarm.setWeek_label_list(week_label);
        String label = getWeeklyLabel(alarm);
        status_label.setText(label.replaceFirst(",", ""));
        String[] l = status_label.getText().toString().split(",");
        if (l[l.length-1].equals(",")){
            l[l.length-1] = "";

        }

        StringBuilder formatted_label = new StringBuilder();
        for (String day : l){
            formatted_label.append(day);

        }

        status_label.setText(formatted_label);
        alarm.setWeekLabel(label);
    }


//    private  ArrayList<Station>  removeDuplicates(ArrayList<Object> record) {
//        ArrayList<Station> stationArrayList = new ArrayList<>();
//        ArrayList<Station> non_duplicated_station_list = new ArrayList<>();
//        if (record!=null){
//            for (Object r : record){
//                Station station = (Station) r;
//                stationArrayList.add(station);
//            }
//            for (Station station : stationArrayList){
//                Station found_station = find(non_duplicated_station_list, station);
//                if (found_station!=null){
//                    continue;
//                }else{
//                    non_duplicated_station_list.add(station);
//                }
//            }
//
//        }
//        return non_duplicated_station_list;
//    }

    private void buildView(Alarm alarm) {
        // Editing existing alarm

        String time = alarm.getTime();
        boolean isRepeating = alarm.getIsRepeating() == 1;
        if (alarm.getWeekLabel()!=null) {
            String[] week_label = alarm.getWeekLabel().split(",");
            ArrayList<Integer> week = new ArrayList<>();
            for (String day_of_week : week_label){
                day_of_week = day_of_week.trim().toLowerCase();
                if (day_of_week.equals("mon")) {
                    mon.setChecked(true);
                    week.add(1);

                }
                else if (day_of_week.equals("tue")) {
                    tues.setChecked(true);
                    week.add(2);

                }
                else if (day_of_week.equals("wen")) {
                    wen.setChecked(true);
                    week.add(3);

                }
                else if (day_of_week.equals("thu")) {
                    thur.setChecked(true);
                    week.add(4);
                }
                else if (day_of_week.equals("fri")) {
                    fri.setChecked(true);
                    week.add(5);
                }
                else if (day_of_week.equals("sat")) {
                    sat.setChecked(true);
                    week.add(6);
                }
                else if (day_of_week.equals("sun")) {
                    sun.setChecked(true);
                    week.add(0);
                }
            }
            Collections.sort(week);
            alarm.setWeek_label_list(week);
        }
        String station_name = alarm.getStationName();
        String station_type = alarm.getStationType();
        TextView currentTime = findViewById(R.id.currentTime);
        CheckBox willRepeat = findViewById(R.id.willRepeat);
        mon = findViewById(R.id.monday);
        tues = findViewById(R.id.tuesday);
        wen = findViewById(R.id.wensday);
        thur = findViewById(R.id.thursday);
        fri = findViewById(R.id.friday);
        sat = findViewById(R.id.saterday);
        sun = findViewById(R.id.sunday);
        currentTime.setText(alarm.getTime());

        currentTime.setText(time);
        willRepeat.setChecked(isRepeating);
        bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor(station_type,getApplicationContext())));
    }


    private Station find(ArrayList<Station> stationArrayList,Station station) {
        for (Station station1 : stationArrayList){
            if (station1.getMap_id().equals(station.getMap_id())){
                return station;
            }
        }
        return null;

    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menu = menu;
//        getMenuInflater().inflate(R.menu.alarm_menu, menu);
//        MenuItem searchView_item =  menu.findItem(R.id.app_bar_search);
//        SearchView searchView = (SearchView) searchView_item.getActionView();
//        MenuItem item = menu.findItem(R.id.spinner);
//        Spinner spinner = (Spinner) item.getActionView();
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.line_names, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//        ArrayList<Object> main_station_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.MAIN_STATIONS, CTA_DataBase.MAIN_STATION_TYPE +" = '"+main_train_line.toUpperCase()+"'", null,null);
//        if (main_station_record!=null) {
//            // Changes our main destinations
//            main_dest_spinner = findViewById(R.id.spinner2);
//            HashMap<String, String> main_station = (HashMap<String, String>) main_station_record.get(0);
//            List<String> spinnerArray = new ArrayList<>();
//            spinnerArray.add(main_station.get(CTA_DataBase.NORTHBOUND));
//            spinnerArray.add(main_station.get(CTA_DataBase.SOUTHBOUND));
//            adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
//            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            main_dest_spinner.setAdapter(adapter1);
//        }
//        cta_dataBase.close();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS",
//                        chicago_transits.TrainLineKeys(main_train_line) + " = '1' AND STATION_NAME",
//                        newText,
//                        null);
//                ArrayList<Station> non_duplicated_station_list = chicago_transits.removeDuplicates(record);
//                cta_dataBase.close();
//                trainTimes_adapter_frag = new TrainTimes_Adapter_frag(getApplicationContext(),MainActivity.message,null, non_duplicated_station_list,recyclerView, alarm);
//                recyclerView.setAdapter(trainTimes_adapter_frag);
//
//
//
//                cta_dataBase.close();
//                return false;
//            }
//        });
//
//
//        main_dest_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0){
//                    alarm.setDirection("1");
//                }else{
//                    alarm.setDirection("5");
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//                recyclerView.setAdapter(null);
//                String train_line  = parent.getSelectedItem().toString();
//                main_train_line = train_line;
//                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, chicago_transits.TrainLineKeys(train_line).toUpperCase()+" = '1'",null,null);
//                ArrayList<Station> stationArrayList = new ArrayList<>();
//                ArrayList<Station> non_duplicated_station_list = new ArrayList<>();
//                alarm.setStationType(main_train_line);
//                for (Object r : record){
//                    Station station = (Station) r;
//                    station.setStation_type(train_line);
//                    stationArrayList.add(station);
//                }
//                for (Station station : stationArrayList){
//                    Station found_station = find(non_duplicated_station_list, station);
//                    if (found_station!=null){
//                        continue;
//                    }else{
//                        non_duplicated_station_list.add(station);
//                    }
//                }
//
//                cta_dataBase.close();
//                ActionBar bar = getSupportActionBar();
//                bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor(main_train_line,getApplicationContext())));
//                trainTimes_adapter_frag = new TrainTimes_Adapter_frag(getApplicationContext(),MainActivity.message,null, non_duplicated_station_list,recyclerView, alarm);
//                recyclerView.setAdapter(trainTimes_adapter_frag);
//                // Changes our main destinations
//                ArrayList<Object> main_station_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.MAIN_STATIONS, CTA_DataBase.MAIN_STATION_TYPE +" = '"+main_train_line.toUpperCase()+"'", null,null);
//                if (main_station_record!=null) {
//                    HashMap<String, String> main_station = (HashMap<String, String>) main_station_record.get(0);
//                    List<String> spinnerArray = new ArrayList<>();
//                    spinnerArray.add(main_station.get(CTA_DataBase.NORTHBOUND));
//                    spinnerArray.add(main_station.get(CTA_DataBase.SOUTHBOUND));
//                    adapter1.clear();
//                    adapter1.addAll(spinnerArray);
//                    adapter1.notifyDataSetChanged(); // optional, as the dataset change should trigger this by default
//                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    main_dest_spinner.setAdapter(adapter1);
//                }
////
//
//
//
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//
//
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//        setContentView(R.layout.alarm_setup_popup_layout2);
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        getWindow().setLayout((int) (dm.widthPixels), (int) (dm.heightPixels ));
//        Button close, save;
//        Spinner line_selection = findViewById(R.id.line_selection);
//        final ArrayAdapter<CharSequence>[] adapter = new ArrayAdapter[]{ArrayAdapter.createFromResource(this,
//                R.array.line_names, android.R.layout.simple_spinner_item)};
//        adapter[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        line_selection.setAdapter(adapter[0]);
//        AutoCompleteTextView station_lookup = findViewById(R.id.autoCompleteTextView);
//        timePicker = findViewById(R.id.timePicker1);
//        mon = findViewById(R.id.monday);
//        tues = findViewById(R.id.tuesday);
//        wen = findViewById(R.id.wensday);
//        thur = findViewById(R.id.thursday);
//        fri = findViewById(R.id.friday);
//        sat = findViewById(R.id.saterday);
//        sun = findViewById(R.id.sunday);
//        Alarm new_alarm  = new Alarm();
//        Intent intent = getIntent();
//
//        final int[] count = {0};
//        HashMap<String, String> selected_alarm = (HashMap<String, String>) intent.getSerializableExtra("alarm");
//        if (selected_alarm != null){
//            new_alarm.setMin(selected_alarm.get("MIN"));
//            new_alarm.setHour(selected_alarm.get("HOUR"));
//            new_alarm.setIsRepeating(Integer.parseInt(selected_alarm.get("WILL_REPEAT")));
//            new_alarm.setAlarm_id(selected_alarm.get("ALARM_ID"));
//            new_alarm.setMap_id(selected_alarm.get("MAP_ID"));
//            new_alarm.setStation_name(selected_alarm.get("STATION_NAME"));
//            new_alarm.setStationType(selected_alarm.get("STATION_TYPE"));
//            new_alarm.setTime(selected_alarm.get("TIME"));
//            new_alarm.setWeekLabel(selected_alarm.get("WEEK_LABEL"));
//            original_week = selected_alarm.get("WEEK_LABEL");
//            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//            ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+new_alarm.getMap_id()+"'", null,null);
//           Station target_station = (Station) record.get(0);
//
//
//            timePicker.setMinute(Integer.parseInt(new_alarm.getMin()));
//            timePicker.setHour(Integer.parseInt(new_alarm.getHour()));
//            String[] weekLabels = Objects.requireNonNull(selected_alarm.get("WEEK_LABEL")).split(",");
//            for (String day_of_week : weekLabels) {
//
//                if (day_of_week.equals("mon")) {
//                    mon.setChecked(true);
//                    new_alarm.setMon(1);
//                }
//                if (day_of_week.equals("tues")) {
//                    tues.setChecked(true);
//                    new_alarm.setTues(1);
//                }
//                if (day_of_week.equals("wen")) {
//                    wen.setChecked(true);
//                    new_alarm.setWens(1);
//                }
//                if (day_of_week.equals("thu")) {
//                    thur.setChecked(true);
//                    new_alarm.setThur(1);
//                }
//                if (day_of_week.equals("fri")) {
//                    fri.setChecked(true);
//                    new_alarm.setFri(1);
//                }
//                if (day_of_week.equals("sat")) {
//                    sat.setChecked(true);
//                    new_alarm.setSat(1);
//                }
//                if (day_of_week.equals("sun")) {
//                    sun.setChecked(true);
//                    new_alarm.setSun(1);
//                }
//            }
//            Chicago_Transits chicago_transits = new Chicago_Transits();
//            line_selection.setSelection(chicago_transits.getSpinnerPosition(selected_alarm.get("STATION_TYPE")));
//            if (target_station!=null){
//            station_lookup.setText(target_station.getStop_name());
//        }
//            cta_dataBase.close();
//    }else{
//            line_selection.setSelection(0);
//
//        }
//
//
//
//
//
//        line_selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String station_type = parent.getItemAtPosition(position).toString();
//                new_alarm.setStationType(station_type);
//                if (count[0] > 0){
//                    station_lookup.setText("");
//                }
//                count[0] +=1;
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        final ArrayAdapter<String>[] Textadapter = new ArrayAdapter[]{null};
//        station_lookup.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String sta = s.toString();
//
//
//                String station_type = line_selection.getSelectedItem().toString();
//                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//                Chicago_Transits chicago_transits = new Chicago_Transits();
//                ArrayList<Object> target_stations = cta_dataBase.excecuteQuery("*", "CTA_STOPS", chicago_transits.TrainLineKeys(station_type).toUpperCase() + " = '1'" +" AND STATION_NAME",  s.toString(), null);
//                if (target_stations != null){
//                    List<String> items = new ArrayList<>();
//                    for (Object record : target_stations) {
//                       Station station = (Station) record;
//                        Log.e("station", " " + station.getStation_name());
//                        items.add("#"+station.getMap_id()+". "+station.getStop_name());
//
//                    }
//                    String[] myArray = new String[items.size()];
//                    items.toArray(myArray);
//                    Textadapter[0] = new ArrayAdapter<String>(Pop.this,
//                            android.R.layout.simple_list_item_1, myArray);
//
//                    station_lookup.setAdapter(Textadapter[0]);
//                    Textadapter[0].notifyDataSetChanged();
//
//
//
//                }
//
//
//
//                cta_dataBase.close();
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        station_lookup.setOnItemClickListener((parent, view, position, id) -> {
//            String item  = Textadapter[0].getItem(position);
//            Toast.makeText(getApplicationContext(), item + " Selected", Toast.LENGTH_SHORT).show();
//            String map_id = StringUtils.substringBetween(item, "#", ".").trim();
//            new_alarm.setMap_id(map_id);
//            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//            ArrayList<Object> station_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+new_alarm.getMap_id()+"'", null,null);
//            Station target_station = (Station) station_record.get(0);
//            new_alarm.setStation_name(target_station.getStation_name());
//            cta_dataBase.close();
//        });
//
//        save = findViewById(R.id.save_button);
//        save.setOnClickListener(v -> {
//            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//            Log.e("HOUR", timePicker.getHour()+"");
//
//            String weekLabel ="";
//
//            if (sun.isChecked()) {
//                new_alarm.setSun(1);
//                weekLabel = weekLabel +" sun ";
//            }else{
//                new_alarm.setSun(0);
//            }
//            if (mon.isChecked()){
//                new_alarm.setMon(1);
//                weekLabel = weekLabel +" mon ";
//            }else{
//                new_alarm.setMon(0);
//            }if (tues.isChecked()){
//                new_alarm.setTues(1);
//                weekLabel = weekLabel +" tues ";
//            }else{
//                new_alarm.setTues(0);
//            }if (wen.isChecked()){
//                new_alarm.setWens(1);
//                weekLabel = weekLabel +" wen ";
//            }else{
//                new_alarm.setWens(0);
//            }   if (thur.isChecked()){
//                new_alarm.setThur(1);
//                weekLabel = weekLabel +" thu ";
//            }else{
//                new_alarm.setThur(0);
//            }   if (fri.isChecked()){
//                new_alarm.setFri(1);
//                weekLabel = weekLabel +" fri ";
//            }else{
//                new_alarm.setFri(0);
//            }   if (sat.isChecked()){
//                new_alarm.setSat(1);
//                weekLabel = weekLabel +" sat ";
//            } else{
//                new_alarm.setSat(0);
//            }
//
//            int hour;
//            String hour_of_day, min;
//            if (timePicker.getHour() == 0){
//                hour = 12;
//                hour_of_day = "AM";
//            }else if (timePicker.getHour() == 12){
//                hour = timePicker.getHour();
//                hour_of_day = "PM";
//            }
//
//            else if (timePicker.getHour() > 12)
//            {
//                hour = timePicker.getHour()-12;
//                hour_of_day = "PM";
//
//            }
//            else{
//                hour = timePicker.getHour();
//                hour_of_day = "AM";
//            }
//            if (timePicker.getMinute() < 10){
//                min = "0"+timePicker.getMinute();
//            }else{
//                min = timePicker.getMinute()+"";
//            }
//
//
//            new_alarm.setTime(hour +":"+min+" "+ hour_of_day);
//            new_alarm.setHour(timePicker.getHour()+"");
//            new_alarm.setMin(timePicker.getMinute()+"");
//            weekLabel = weekLabel.trim().replaceAll(" ", ",");
//            weekLabel = weekLabel.replaceAll(",,", ",");
//            new_alarm.setWeekLabel(weekLabel);
//
//
//            if (selected_alarm !=null) {
//                ArrayList<Object> pending_alarm = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '" + new_alarm.getAlarm_id() + "'", null, null);
//                if (pending_alarm !=null){
//                    cta_dataBase.update("ALARMS", "HOUR", new_alarm.getHour(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    cta_dataBase.update("ALARMS", "MIN", new_alarm.getMin(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    cta_dataBase.update("ALARMS", "TIME", new_alarm.getTime(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    cta_dataBase.update("ALARMS", "MAP_ID", new_alarm.getMap_id(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    cta_dataBase.update("ALARMS", "STATION_TYPE", new_alarm.getStationType(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    cta_dataBase.update("ALARMS", "STATION_NAME", new_alarm.getStationName(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    if (!original_week.equals(new_alarm.getWeekLabel())){
//                        cancelAlarm((HashMap<String, String>) pending_alarm.get(0));
//                    }
//                    cta_dataBase.update("ALARMS", "WEEK_LABEL", new_alarm.getWeekLabel(), "ALARM_ID = '"+new_alarm.getAlarm_id()+"'");
//                    pending_alarm = cta_dataBase.excecuteQuery("*", "ALARMS", "ALARM_ID = '" + new_alarm.getAlarm_id() + "'", null, null);
//                    HashMap<String, String> updated_alarm = (HashMap<String, String>) pending_alarm.get(0);
//                    if (Objects.equals(updated_alarm.get("WILL_REPEAT"), "1")){
//                        startAlarm(updated_alarm);
//                    }
//
//
//
//                }
//                MainActivity.message.getApi_caller_thread().cancel();
//                MainActivity.message.getT1().interrupt();
//                startActivity(new Intent(Pop.this, MainActivity.class));
//
//
//            }else{
//           new_alarm.setIsRepeating(0);
//            cta_dataBase.commit(new_alarm, "ALARMS");
//             cta_dataBase.close();
//             MainActivity.message.getApi_caller_thread().cancel();
//             MainActivity.message.getT1().interrupt();
//             startActivity(new Intent(Pop.this, MainActivity.class));
//            }
//            cta_dataBase.close();
//        });
//        close = findViewById(R.id.cancel_button);
//        close.setOnClickListener(v -> finish());
//    }
//
//    private void cancelAlarm(HashMap<String, String > alarm_record){
//        String[] weekLabels = Objects.requireNonNull(alarm_record.get("WEEK_LABEL")).split(",");
//        int count = 0;
//        for (String day_of_week : weekLabels) {
//            count += 1;
//            Intent intent = new Intent(getApplicationContext(), MyBroadCastReciever.class);
//            int pending_intent_id = Integer.parseInt(Objects.requireNonNull(alarm_record.get("ALARM_ID"))+count);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                    pending_intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//            alarmManager.cancel(pendingIntent);
//            pendingIntent.cancel();
//
//        }
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void startAlarm(HashMap<String, String > alarm_record){
//
//        int hours =  Integer.parseInt(Objects.requireNonNull(alarm_record.get("HOUR")));
//        int minutes = Integer.parseInt(Objects.requireNonNull(alarm_record.get("MIN")));
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        String[] weekLabels = Objects.requireNonNull(alarm_record.get("WEEK_LABEL")).split(",");
//        int count = 0;
//        for (String day_of_week : weekLabels){
//            count+=1;
//            Calendar cal = Calendar.getInstance();
//            int day = chicago_transits.getDayOfWeekNum(day_of_week);
//
//            cal.setTimeInMillis(System.currentTimeMillis());
//            cal.set(Calendar.DAY_OF_WEEK, day);
//            cal.set(Calendar.HOUR_OF_DAY, hours);
//            cal.set(Calendar.MINUTE, minutes);
//            cal.set(Calendar.SECOND, 0);
//
//            if(cal.before(Calendar.getInstance())) {
//                cal.add(Calendar.DATE,7 );
//                Log.e("Alarm", "Day: "+day_of_week +" Will first fire on: "+cal.get(Calendar.DATE)+ " at "+ hours + ":"+minutes+ ". Then every 7 days after");
//
//
//            }else{
//                Log.e("Alarm", "Day: "+day_of_week +" Will first fire on: "+cal.get(Calendar.DATE)+ " at "+ hours + ":"+minutes+ ". Then every 7 days after");
//
//
//            }
//
//            Intent intent = new Intent(getApplicationContext(), MyBroadCastReciever.class);
//            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                    Integer.parseInt(Objects.requireNonNull(alarm_record.get("ALARM_ID"))+count), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7, pendingIntent);
//
//        }

//
//    }

}
