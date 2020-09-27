package com.example.cta_map;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChooseLine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_activity);
        ArrayList<StationLines> list = new ArrayList<>();
        RecyclerView line_layout = (RecyclerView) findViewById(R.id.main_line);
//        Log.e("context", getBaseContext()+" context");
//
//        int[] images = {
//                R.drawable.red,
//                R.drawable.blue,
//                R.drawable.green,
//                R.drawable.orange,
//                R.drawable.brown,
//                R.drawable.pink,
//                R.drawable.yellow,
//                R.drawable.purple};
//
//
//        String[] names = new String[]{"Red", "Blue", "Green", "Orange", "Brown", "Pink", "Yellow", "Purple"};
//        for (int i=0; i<names.length; i++){
//            list.add(new StationLines(names[i], images[i]));
//
//        }
//
//
//
//        LineAdapter adapter = new LineAdapter(getApplicationContext(), list);
//        line_layout.setAdapter(adapter);
//        line_layout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//






    }
}