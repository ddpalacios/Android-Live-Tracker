//package com.example.cta_map.Activities;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.SeekBar;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//
//import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.R;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Objects;
//
//public class PopUp  extends Activity {
//    Spinner spinner, stopSpinner;
//    ArrayAdapter<String> adapter;
//    SeekBar seekBar;
//    boolean showTarget=false, showAllStations=false, noTrains=false;
//    TextView progess;
//    int selected_station_type=0;
//    int selected_station=0;
//    Button exit_btn, submit_btn;
//    ArrayAdapter<String> stop_adapter;
//    CheckBox no_trains_box, north_box, south_box, target_box, all_station_box;
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.popup_menu);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        getWindow().setLayout((int)(width*.8), (int)(height*.6));
//        spinner =(Spinner) findViewById(R.id.line_selection);
//        progess = (TextView) findViewById(R.id.progress);
//        seekBar = (SeekBar) findViewById(R.id.n_trains);
//        stopSpinner =(Spinner) findViewById(R.id.stop_selection);
//        no_trains_box= findViewById(R.id.no_trains_box);
//        north_box = findViewById(R.id.north_box);
//        south_box = findViewById(R.id.south_box);
//        exit_btn = findViewById(R.id.exit_btn);
//        submit_btn = findViewById(R.id.apply_btn);
//        target_box = findViewById(R.id.target_box);
//        all_station_box = findViewById(R.id.all_stations_box);
//
//
//
//        final Database2 sqlite = new Database2(getApplicationContext());
//        String[] colors = {"blue", "red", "green", "orange", "purple", "yellow", "pink", "brown"};
//        ArrayList<String> line_names = new ArrayList(Arrays.asList(colors));
//        HashMap<String, String> tracking_record = sqlite.get_tracking_record();
//        sqlite.close();
//        if (Objects.equals(tracking_record.get("station_dir"), "1")){
//            north_box.setChecked(true);
//            south_box.setEnabled(false);
//        }else{
//            south_box.setChecked(true);
//            north_box.setEnabled(false);
//        }
//
//        north_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                south_box.setEnabled(true);
//                south_box.setChecked(true);
//                north_box.setEnabled(false);
//            }
//        });
//
//        south_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                north_box.setEnabled(true);
//                north_box.setChecked(true);
//                south_box.setEnabled(false);
//
//            }
//        });
//
//
//
//
//
//        no_trains_box.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View v) {
//                noTrains = no_trains_box.isChecked();
//
//        if (no_trains_box.isChecked()){
//            progess.setText("0/10");
//            seekBar.setProgress(0);
//            seekBar.setEnabled(false);
//            north_box.setEnabled(false);
//            south_box.setEnabled(false);
//
//        }else{
//            progess.setText("5/10");
//            seekBar.setProgress(5);
//
//            if (north_box.isChecked()){
//                north_box.setEnabled(true);
//
//            }
//            if (south_box.isChecked()){
//                south_box.setEnabled(true);
//
//            }
//            seekBar.setEnabled(true);
//
//
//
//
//        }
//
//            }
//        });
//
//
//
//
//        String original_train_line = Objects.requireNonNull(tracking_record.get("station_type")).trim();
//        if (line_names.contains(original_train_line.toLowerCase())){
//            line_names.remove(original_train_line.toLowerCase());
//            line_names.add(0, original_train_line);
//        }
//
//
//
//        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, line_names);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int pval = 0;
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                pval = progress;
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                //write custom code to on start progress
//            }
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                progess.setText(pval + "/" + seekBar.getMax());
//            }
//        });
//
//
//
//
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position)+" ", Toast.LENGTH_SHORT).show();
//                selected_station_type = position;
//
//                final Database2 sqlite = new Database2(getApplicationContext());
//
//                HashMap<String, String> tracking_record = sqlite.get_tracking_record();
//                String original_station_name = Objects.requireNonNull(tracking_record.get("station_name"));
//
//                final ArrayList<String> modified_stops = new ArrayList<>();
//                final ArrayList<String> stops = sqlite.get_column_values("line_stops_table", parent.getItemAtPosition(position)+"");
//                for (String each_stop : stops) { modified_stops.add(each_stop.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()); }
//                if (modified_stops.contains(original_station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())){
//                    int target_index = modified_stops.indexOf(original_station_name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
//                    String target_station = stops.get(target_index);
//                    stops.remove(target_index);
//                    stops.add(0, target_station);
//                }
//
//
//                sqlite.close();
//                stop_adapter = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_spinner_item, stops);
//                target_box.setChecked(true);
//                target_box.setEnabled(false);
//                showTarget=true;
//                stop_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                stopSpinner.setAdapter(stop_adapter);
//                stopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                        Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position)+" ", Toast.LENGTH_SHORT).show();
//                        selected_station = position;
//
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
//
//            }
//
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        exit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(PopUp.this, MapsActivity.class));
//
//            }
//        });
//
//
//
//
//        target_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showTarget = target_box.isChecked();
//
//
//
//
//            }
//        });
//
//
//        all_station_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAllStations = all_station_box.isChecked();
//
//
//            }
//        });
//
//
//
//
//
//submit_btn.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        String station_direction = null;
//        String station_type = adapter.getItem(selected_station_type);
//        String station_name = stop_adapter.getItem(selected_station);
//        if (north_box.isChecked()){station_direction = "1";}
//        if (south_box.isChecked()){station_direction = "5";}
//
//        String query1 = "SELECT station_id FROM cta_stops WHERE station_name = '" + station_name + "'" + " AND " + station_type + " = 'true'";
//        String main_query = "SELECT northbound FROM main_stations WHERE main_station_type = '"+station_type.toUpperCase().replaceAll(" ", "")+"'";
//        int num_of_trains = seekBar.getProgress();
//        Log.e("num", num_of_trains+" ");
//        String station_id = sqlite.getValue(query1);
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
//        String main_station = sqlite.getValue(main_query);
//        sqlite.add_tracking_station(station_name, station_type, station_direction, main_station, station_coord, station_id);
////        Intent intent = new Intent(PopUp.this, MapsActivity.class);
////        intent.putExtra("noTrains", noTrains);
////        intent.putExtra("num_of_trains", num_of_trains);
////        intent.putExtra("fromSettings", true);
////        intent.putExtra("showTarget", showTarget);
////        intent.putExtra("showAllStations", showAllStations);
////
////
////
////        startActivity(intent);
//
//
//
//        Toast.makeText(getApplicationContext(), station_type+" "+station_name+" "+station_direction, Toast.LENGTH_SHORT).show();
//
//
//
//
//
//    }
//});
//
//
//
//    }
//
//}
