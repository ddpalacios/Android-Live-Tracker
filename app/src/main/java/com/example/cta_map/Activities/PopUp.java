package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PopUp  extends Activity {
    Spinner spinner, stopSpinner;
    ArrayAdapter<String> adapter;
    SeekBar seekBar;
    TextView progess;
    ArrayAdapter<String> stop_adapter;
    CheckBox no_trains_box, north_box, south_box, target_box, all_station_box;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_menu);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.6));
        spinner =(Spinner) findViewById(R.id.line_selection);
        progess = (TextView) findViewById(R.id.progress);
        seekBar = (SeekBar) findViewById(R.id.n_trains);
        stopSpinner =(Spinner) findViewById(R.id.stop_selection);
        no_trains_box= findViewById(R.id.no_trains_box);
        north_box = findViewById(R.id.north_box);
        south_box = findViewById(R.id.south_box);
        target_box = findViewById(R.id.target_box);
        all_station_box = findViewById(R.id.all_stations_box);



        final Database2 sqlite = new Database2(getApplicationContext());
        String[] colors = {"blue", "red", "green", "orange", "purple", "yellow", "pink", "brown"};
        ArrayList<String> line_names = new ArrayList(Arrays.asList(colors));
        HashMap<String, String> tracking_record = sqlite.get_tracking_record();
        if (Objects.equals(tracking_record.get("station_dir"), "1")){
            north_box.setChecked(true);
            south_box.setEnabled(false);
        }else{
            south_box.setChecked(true);
            north_box.setEnabled(false);
        }

        north_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                south_box.setEnabled(true);
                south_box.setChecked(true);
                north_box.setEnabled(false);
            }
        });

        south_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                north_box.setEnabled(true);
                north_box.setChecked(true);
                south_box.setEnabled(false);

            }
        });





        no_trains_box.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

        if (no_trains_box.isChecked()){
            progess.setText("0/10");
            seekBar.setProgress(0);
            seekBar.setEnabled(false);
            north_box.setEnabled(false);
            south_box.setEnabled(false);

        }else{
            progess.setText("5/10");
            seekBar.setProgress(5);

            if (north_box.isChecked()){
                north_box.setEnabled(true);

            }
            if (south_box.isChecked()){
                south_box.setEnabled(true);

            }



            seekBar.setEnabled(true);




        }

            }
        });




        String original_train_line = Objects.requireNonNull(tracking_record.get("station_type")).trim();
        if (line_names.contains(original_train_line.toLowerCase())){
            line_names.remove(original_train_line.toLowerCase());
            line_names.add(0, original_train_line);
        }
        sqlite.close();



        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, line_names);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progess.setText(pval + "/" + seekBar.getMax());
            }
        });




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position)+" ", Toast.LENGTH_SHORT).show();
                final Database2 sqlite = new Database2(getApplicationContext());

                HashMap<String, String> tracking_record = sqlite.get_tracking_record();
                String original_station_name = Objects.requireNonNull(tracking_record.get("station_name"));

                final ArrayList<String> modified_stops = new ArrayList<>();
                final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", parent.getItemAtPosition(position)+"");
                for (String each_stop : stops) { modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }
                if (modified_stops.contains(original_station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())){
                    int target_index = modified_stops.indexOf(original_station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
                    String target_station = stops.get(target_index);
                    stops.remove(target_index);
                    stops.add(0, target_station);
                }


                sqlite.close();
                stop_adapter = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_spinner_item, stops);

                stop_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stopSpinner.setAdapter(stop_adapter);
                stopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position)+" ", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    }

}
