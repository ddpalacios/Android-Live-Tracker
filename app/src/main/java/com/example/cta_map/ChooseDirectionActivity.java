package com.example.cta_map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
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
        final String target_station_type = bb.getString("target_station_type").toLowerCase();
        list = (ListView) findViewById(R.id.direction_choice);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
        ArrayList<String> record = sqlite.get_table_record("main_stations_table", "WHERE train_line ='"+target_station_type+"'");
        arrayList.add("To "+record.get(2));
        arrayList.add("To "+record.get(3));
        adapter.notifyDataSetChanged();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseDirectionActivity.this,TrainStationActivity.class);
                if (position == 0){


                    intent.putExtra("train_direction", String.valueOf(1));
                    intent.putExtra("train_direction_name", String.valueOf(list.getItemAtPosition(position)));
                }else{
                    intent.putExtra("train_direction", String.valueOf(5));
                    intent.putExtra("train_direction_name", String.valueOf(list.getItemAtPosition(position)));
                }




                intent.putExtra("target_station_type", target_station_type);
                startActivity(intent);
            }
        });
    }
}
