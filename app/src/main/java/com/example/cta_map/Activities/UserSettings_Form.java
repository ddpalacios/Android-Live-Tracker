package com.example.cta_map.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.Objects;

public class UserSettings_Form extends AppCompatActivity {
    boolean IsChecked_;
    boolean isNew = false;
    public  static String STATIONS_ITEM = "Stations";
    public static String MINUTES_ITEM = "Minutes";
    boolean has_sound_default = true;
    boolean has_vibrate_default = true;
    boolean isMute_default = false;
    public static int green_default_limit = 5;
    public static int yellow_default_limit = 3;
    CTA_DataBase cta_dataBase;

    UserSettings userSettings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);
        setTitle("Settings");
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        Button save_button = findViewById(R.id.main_save_button);
        cta_dataBase = new CTA_DataBase(getApplicationContext());

        final boolean isNewFinal;
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null,null,null);

        if (record!= null){
            userSettings = (UserSettings) record.get(0);
            isNewFinal = isNew;

        }else{
            userSettings = new UserSettings();
            userSettings.setGreen_limit(green_default_limit+"");
            userSettings.setYellow_limit(yellow_default_limit+"");
            int IsSharingLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);
            if (UserLocation != null && IsSharingLocation ==0){
                userSettings.setIs_sharing_loc("1");
            }else {
                userSettings.setIs_sharing_loc("0");
            }
            isNew = true;
            isNewFinal= isNew;
        }
        initilizeViews();
        cta_dataBase.close();


        save_button.setOnClickListener(v -> {
            if (isNewFinal){
                // commits new settings
                cta_dataBase.commit(userSettings, CTA_DataBase.USER_SETTINGS);
            }else{
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", userSettings.getIs_sharing_loc(), "STOP_ID = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, userSettings.getIs_sharing_loc(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.GREEN_LIMIT, userSettings.getGreen_limit(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.YELLOW_LIMIT, userSettings.getYellow_limit(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_STATIONS, userSettings.getAsStations(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.AS_MINUTES, userSettings.getAsMinutes(), CTA_DataBase.USER_SETTINGS_ID +" = '1'");

            }




            cta_dataBase.close();

            Intent intent1 = new Intent(UserSettings_Form.this, MainActivity.class);
            startActivity(intent1);
        });

    }

    @SuppressLint("SetTextI18n")
    private void initilizeViews() {
        Activity context = this;
        TextView green_description = findViewById(R.id.green_description);
        TextView yellow_description = findViewById(R.id.yellow_description);
        TextView red_description = findViewById(R.id.red_description);

        Button back_to_default = findViewById(R.id.back_to_default_button);
        Switch is_sharing_loc_switch = findViewById(R.id.location_switch);
        SeekBar green_limit_bar = findViewById(R.id.seekBar_green);
        SeekBar yellow_limit_bar = findViewById(R.id.seekBar_yellow);
        Spinner min_or_stations = findViewById(R.id.stations_or_min_spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(STATIONS_ITEM);
        arrayList.add(MINUTES_ITEM);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        min_or_stations.setAdapter(arrayAdapter);


        min_or_stations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option  = parent.getSelectedItem().toString();
                if (option.equals(STATIONS_ITEM)){
                    userSettings.setAsStations("1");
                    userSettings.setAsMinutes("0");
                }else{
                    userSettings.setAsMinutes("1");
                    userSettings.setAsStations("0");

                }

                if (userSettings.getAsMinutes().equals("1")){
                    option = "minutes";
                }else{
                    option = "stops";

                }


                green_description.setText("(Still have time!) Notify when train > "+userSettings.getGreen_limit()  + " "+ option);
                yellow_description.setText("(Warning!) Will Notify when train > "+userSettings.getYellow_limit() + " and < "+userSettings.getGreen_limit() + " "+ option);
                red_description.setText("(Must Leave!) Will notify when train < "+userSettings.getYellow_limit()  + " "+ option);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (userSettings.getAsStations()!=null) {
            if (userSettings.getAsStations().equals("1")) {
                min_or_stations.setSelection(0);
            } else {
                min_or_stations.setSelection(1);
            }
        }

        int IsSharingLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);
        if (UserLocation != null && IsSharingLocation ==0 && userSettings.getIs_sharing_loc().equals("1")){
        is_sharing_loc_switch.setChecked(true);
        userSettings.setIs_sharing_loc("1");
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
        }else {
            is_sharing_loc_switch.setChecked(false);
            userSettings.setIs_sharing_loc("0");
            cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");


        }
        if (!isNew){
            green_limit_bar.setProgress(Integer.parseInt(userSettings.getGreen_limit()));
            yellow_limit_bar.setProgress(Integer.parseInt(userSettings.getYellow_limit()));

        }else{
            // User default Values
            green_limit_bar.setProgress(green_default_limit);
            yellow_limit_bar.setProgress(yellow_default_limit);

        }



        back_to_default.setOnClickListener(v -> {
            // Resets items back to its default state
            setToDefault();

        });

    if (isNew) {
        green_description.setText("(Still have time!) Notify when train > "+green_default_limit + " stops away.");
        yellow_description.setText("(Warning!) Will Notify when train > "+yellow_default_limit + " and < "+green_default_limit + " stops away.");
        red_description.setText("(Must Leave!) Will notify when train < "+yellow_default_limit +" stops away.");
    }else{
        String option = null;
        if (userSettings.getAsMinutes().equals("1")){
            option = "minutes";
        }else{
            option = "stops";

        }


        green_description.setText("(Still have time!) Notify when train > "+userSettings.getGreen_limit()  + " "+ option);
        yellow_description.setText("(Warning!) Will Notify when train > "+userSettings.getYellow_limit() + " and < "+userSettings.getGreen_limit() + " "+ option);
        red_description.setText("(Must Leave!) Will notify when train < "+userSettings.getYellow_limit()  + " "+ option);


    }
        green_limit_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("Progress", "Green: "+progress+"");
                userSettings.setGreen_limit(progress+"");
                String option = null;
                if (userSettings.getAsMinutes().equals("1")){
                    option = "minutes";
                }else{
                    option = "stops";

                }


                green_description.setText("(Still have time!) Notify when train > "+userSettings.getGreen_limit()  + " "+ option);
                yellow_description.setText("(Warning!) Will Notify when train > "+userSettings.getYellow_limit() + " and < "+userSettings.getGreen_limit() + " "+ option);
                red_description.setText("(Must Leave!) Will notify when train < "+userSettings.getYellow_limit()  + " "+ option);




            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        yellow_limit_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("Progress", "Yellow: "+progress+"");
                userSettings.setYellow_limit(progress+"");
                String option = null;
                if (userSettings.getAsMinutes().equals("1")){
                    option = "minutes";
                }else{
                    option = "stops";

                }


                green_description.setText("(Still have time!) Notify when train > "+userSettings.getGreen_limit()  + " "+ option);
                yellow_description.setText("(Warning!) Will Notify when train > "+userSettings.getYellow_limit() + " and < "+userSettings.getGreen_limit() + " "+ option);
                red_description.setText("(Must Leave!) Will notify when train < "+userSettings.getYellow_limit()  + " "+ option);




            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        is_sharing_loc_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CTA_DataBase cta_database = new CTA_DataBase(getApplicationContext());

            if (isChecked){
                cta_database.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
                userSettings.setIs_sharing_loc("1");

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)){

                        ActivityCompat.requestPermissions(context,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }else{
                        ActivityCompat.requestPermissions(context,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    }
                }

            }else{
                userSettings.setIs_sharing_loc("0");
                cta_database.update("USER_LOCATION", "HAS_LOCATION", "0", "HAS_LOCATION = '1'");
                cta_database.update("USER_LOCATION", "USER_LAT", "", "HAS_LOCATION= '0'");
                cta_database.update("USER_LOCATION", "USER_LON", "", "HAS_LOCATION = '0'");
                Toast.makeText(getApplicationContext(), "Location is turned off!", Toast.LENGTH_SHORT).show();
            }
            cta_database.close();
        });
    }


    private void setToDefault(){
        // User default Values
        SeekBar green_limit_bar = findViewById(R.id.seekBar_green);
        SeekBar yellow_limit_bar = findViewById(R.id.seekBar_yellow);
        green_limit_bar.setProgress(green_default_limit);
        yellow_limit_bar.setProgress(yellow_default_limit);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:


                Station target_station = MainActivity.message.getTarget_station();
                if (target_station != null) {
                    new Chicago_Transits().callThreads(getApplicationContext(),
                            MainActivity.message.getHandler(),
                            MainActivity.message,
                            MainActivity.message.getDir(),
                            MainActivity.message.getTarget_type(),
                            target_station.getMap_id(),
                            false);
                }
                cta_dataBase.close();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
