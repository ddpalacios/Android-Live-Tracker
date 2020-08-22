package com.example.cta_map.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.cta_map.R;

import java.util.HashMap;

public class TrainInfoPopUp  extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_info_popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.4));
        TextView train_title = findViewById(R.id.train_title);
//        Switch notify_me = findViewById(R.id.getNotified);


        Bundle bb = getIntent().getExtras();
        HashMap<String, String > chosen_train = (HashMap<String, String>) bb.getSerializable("chosen_train");
        train_title.setText("#"+chosen_train.get("train_id")+". "+chosen_train.get("next_stop").trim());


        Log.e("POPUP", chosen_train+" ");



    }
}
