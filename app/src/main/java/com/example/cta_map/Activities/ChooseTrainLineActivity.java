package com.example.cta_map.Activities;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Adapters.TrainLineAdapter;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChooseTrainLineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Add New Route");
        setContentView(R.layout.activity_choose_line);
        RecyclerView recyclerView = findViewById(R.id.list_of_train_lines);
        ArrayList<ListItem> arrayList = new ArrayList<>();
        int[] images = {
                R.drawable.red,
                R.drawable.blue,
                R.drawable.brown,
                R.drawable.green,
                R.drawable.orange,
                R.drawable.purple,
                R.drawable.pink,
                R.drawable.yellow
        };


        String[] line_names = {"Red", "Blue", "Brown", "Green", "Orange", "Purple", "Pink", "Yellow"};
        for (int i=0; i<images.length; i++){
            ListItem listItem = new ListItem();
            listItem.setImage(images[i]);
            listItem.setTitle(line_names[i]);
            arrayList.add(listItem);
        }
        HashMap<String, String> tracking_station = new HashMap<>();
        recyclerView.setAdapter(new TrainLineAdapter(getApplicationContext(),arrayList, tracking_station));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }





}
