package com.example.cta_map.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.R;

import java.util.List;

public class PopUp  extends Activity {
    Spinner spinner, stopSpinner;
    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<String> stop_adapter;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_menu);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.6));
        spinner =(Spinner) findViewById(R.id.line_selection);
        stopSpinner =(Spinner) findViewById(R.id.stop_selection);

        adapter = ArrayAdapter.createFromResource(this, R.array.line_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position)+" ", Toast.LENGTH_SHORT).show();
                final Database2 sqlite = new Database2(getApplicationContext());
                final List<String> stops = sqlite.get_column_values("line_stops_table",parent.getItemAtPosition(position)+"".toLowerCase());
                sqlite.close();
                stop_adapter = new ArrayAdapter<>(
                        getApplicationContext(), android.R.layout.simple_spinner_item, stops);

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
