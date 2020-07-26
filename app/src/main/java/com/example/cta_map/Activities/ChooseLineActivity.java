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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.R;

import java.util.ArrayList;

public class ChooseLineActivity extends AppCompatActivity {
    @SuppressLint({"WrongConstant", "ShowToast"})
    @RequiresApi(api = Build.VERSION_CODES.M)

    RecyclerView recyclerView;
    String[] s1;
    String[] s2;
    int[] images = {
            R.drawable.images,
            R.drawable.blue,
            R.drawable.green,
            R.drawable.orange,
            R.drawable.brown,
            R.drawable.pink,
            R.drawable.yellow,
            R.drawable.purple};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_line);

        recyclerView = findViewById(R.id.recycler_view);
        s1 = getResources().getStringArray(R.array.programming_languages);
        s2 = getResources().getStringArray(R.array.description);

        MyAdapter myAdapter = new MyAdapter(this, s1, s2, images);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }


//        ArrayList<String> arrayList = new ArrayList<>();
//        Bundle bb;
//        bb = getIntent().getExtras();
//        final Integer position1 = bb.getInt("position");

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view =super.getView(position, convertView, parent);
//                TextView textView=(TextView) view.findViewById(android.R.id.text1);
//                textView.setTextColor(Color.BLACK);
//
//                // Set the item text style to bold
//                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//
//                // Change the item text size
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
//                return view;
//            }
//
//        };
//        final ListView list = (ListView) findViewById(R.id.station_lines);
//        list.setAdapter(adapter);
//        String[] station_lines = new String[]{"Red", "Blue", "Brown", "Green", "Orange", "Purple", "Pink", "Yellow"};
//        for (String lines : station_lines){
//            arrayList.add(lines);
//            adapter.notifyDataSetChanged();
//        }
//
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(ChooseLineActivity.this, ChooseDirectionActivity.class);
//                intent.putExtra("target_station_type", String.valueOf(list.getItemAtPosition(position)));
//                intent.putExtra("position", position1);
//
//                startActivity(intent);
//            }
//        });

    }
