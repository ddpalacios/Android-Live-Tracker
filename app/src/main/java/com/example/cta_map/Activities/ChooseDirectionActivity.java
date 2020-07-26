package com.example.cta_map.Activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.R;

import java.util.ArrayList;

@SuppressLint("Registered")
public class ChooseDirectionActivity  extends AppCompatActivity {
    ListView list;
    Bundle bb; // Retrieve data from main screen
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_direction);
//        SharedPreferences GET_TRAIN_SELECTION_VALUES = getSharedPreferences("Train_Selection_Values", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor EDIT_TRAIN_SELECTION_VALUES =  getSharedPreferences("Train_Selection_Values", MODE_PRIVATE).edit();





        Bundle bb; // Retrieve data from main screen
        bb=getIntent().getExtras();
        assert bb != null;
        bb = getIntent().getExtras();
        final Integer position1 = bb.getInt("position");



        final String target_station_type = bb.getString("target_station_type").toLowerCase();
        list = (ListView) findViewById(R.id.direction_choice);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);

                // Set the item text style to bold
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

                // Change the item text size
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                return view;
            }

        };
        list.setAdapter(adapter);
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        ArrayList<String> record = sqlite.get_table_record("main_stations_table", "WHERE train_line ='"+target_station_type+"'");
        arrayList.add("To "+record.get(2));
        arrayList.add("To "+record.get(3));
        adapter.notifyDataSetChanged();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseDirectionActivity.this, TrainStationActivity.class);
                if (position == 0){
                    intent.putExtra("train_direction", String.valueOf(1));
                }else{
                    intent.putExtra("train_direction", String.valueOf(5));
                }


                intent.putExtra("main_station", String.valueOf(list.getItemAtPosition(position)));
                intent.putExtra("target_station_type", target_station_type);
                intent.putExtra("position", position1);

                startActivity(intent);
            }
        });
    }
}
