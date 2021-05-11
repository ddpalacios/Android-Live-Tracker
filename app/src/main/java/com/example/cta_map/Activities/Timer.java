package com.example.cta_map.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.R;

public class Timer extends Activity {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_widget);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        getWindow().setLayout((int) (dm.widthPixels * .9), (int) (dm.heightPixels *.6));
        Button save_button = findViewById(R.id.save_button);
        Button cancel_button = findViewById(R.id.cancel_button);
        TimePicker timePicker = findViewById(R.id.timePicker1);
        Alarm alarm = (Alarm) getIntent().getSerializableExtra("alarm");
        if (alarm!=null){
            timePicker.setMinute(Integer.parseInt(alarm.getMin()));
            timePicker.setHour(Integer.parseInt(alarm.getHour()));
        }

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int min = timePicker.getMinute();
                alarm.setHour(hour+"");
               alarm.setMin(min+"");
               String time_format = NewAlarmSetUp.getTimeFormat(hour, min);
               alarm.setTime(time_format);
               Intent intent = new Intent(Timer.this, NewAlarmSetUp.class);
               intent.putExtra("alarm", alarm);
               finish();
               startActivity(intent);
            }
        });

        cancel_button.setOnClickListener(v -> finish());





    }
}
