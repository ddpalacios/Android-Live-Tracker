package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.cta_map.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

public class TrainInfoPopUp  extends Activity {

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_info_popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        final Switch notify_me = findViewById(R.id.getNotified);
        getWindow().setLayout((int)(width*.8), (int)(height*.6));
        TextView train_title = findViewById(R.id.train_title);
        ImageView imageView = findViewById(R.id.info_window_imageView);
        TextView info_window_train_id = findViewById(R.id.info_window_train_id);
        TextView info_window_next_stop_name = findViewById(R.id.info_window_next_stop_name);
        TextView info_window_next_stop_train_eta = findViewById(R.id.info_window_next_stop_train_eta);
        TextView info_window_target_name = findViewById(R.id.info_window_target_name);
        TextView info_window_target_eta = findViewById(R.id.info_window_target_eta);
        TextView info_window_next_stop_name_dist = findViewById(R.id.info_window_next_stop_name_dist);
        TextView info_window_next_stop_train_dist = findViewById(R.id.info_window_next_stop_train_dist);
        TextView info_window_target_name_dist = findViewById(R.id.info_window_target_name_dist);
        TextView info_window_target_train_dist = findViewById(R.id.info_window_target_train_dist);
        TextView info_window_is_app = findViewById(R.id.info_window_is_app);
        TextView info_window_is_dly = findViewById(R.id.info_window_is_dly);
        TextView info_window_coord = findViewById(R.id.info_window_coord);
        TextView info_window_home = findViewById(R.id.info_window_home);
        TextView info_window_arrival_time = findViewById(R.id.info_window_arrival_time);
        HashMap<String, Integer> keyCodes = getTrainLineKeys();
try {

        Bundle bb = getIntent().getExtras();
        final HashMap<String, String > chosen_train = (HashMap<String, String>) bb.getSerializable("chosen_train");
        assert chosen_train != null;
        Integer key = keyCodes.get(Objects.requireNonNull(chosen_train.get("station_type")).toLowerCase().trim());
        imageView.setImageResource(key);

        info_window_train_id.setText("Train# "+chosen_train.get("train_id"));
        info_window_arrival_time.setText(chosen_train.get("next_stop_pred_arr_time"));

        if (Objects.equals(chosen_train.get("train_direction"), "1")){
            train_title.setText(chosen_train.get("next_stop")+" - N");

        }else{
            train_title.setText(chosen_train.get("next_stop")+" - S");

        }
        info_window_next_stop_name.setText("(Next) "+chosen_train.get("next_stop")+" - ETA");
        info_window_next_stop_train_eta.setText(chosen_train.get("next_stop_eta")+"m");
        info_window_target_name.setText("(Target) "+chosen_train.get("target_station")+" - ETA");
        info_window_target_eta.setText(chosen_train.get("train_eta")+"m");

        info_window_next_stop_name_dist.setText("(Next) "+chosen_train.get("next_stop"));
        Double d0 = Double.parseDouble(Objects.requireNonNull(chosen_train.get("next_stop_distance")));
        info_window_next_stop_train_dist.setText(String.format("%.2f",d0)+" mi");

        info_window_target_name_dist.setText("(Target)"+" Distance from "+chosen_train.get("target_station"));
        Double d1 = Double.parseDouble(Objects.requireNonNull(chosen_train.get("train_distance")));
        info_window_target_train_dist.setText(String.format("%.2f",d1)+" mi");

        if (Objects.equals(chosen_train.get("isApproaching"), "1")) {

            info_window_is_app.setText("Yes");
        }else {
            info_window_is_app.setText("No");
        }

        if (Objects.equals(chosen_train.get("isDelayed"), "1")){
            info_window_is_dly.setText("Yes");
        }else{
            info_window_is_dly.setText("No");
        }

        info_window_coord.setText("("+chosen_train.get("train_lat")+" , "+chosen_train.get("train_lon")+")");
        info_window_home.setText(chosen_train.get("main_station"));

    notify_me.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (notify_me.isChecked()){
                String tracking_train_id = chosen_train.get("train_id");
                Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_SHORT).show();

            }
        }
    });

}catch (Exception e){
    e.printStackTrace();
}

//        TextView distance_from = findViewById(R.id.distance_from);
//        TextView train_id_text = findViewById(R.id.info_window_train_id);
//        TextView train_eta = findViewById(R.id.info_window_train_eta);
//        TextView train_dist = findViewById(R.id.info_window_train_dist);
//        TextView isApp = findViewById(R.id.info_window_is_app);
////        TextView isDly = findViewById(R.id.info_window_is_dly);
//        TextView current_coord = findViewById(R.id.info_window_coord);
//        TextView home_station = findViewById(R.id.info_window_home);

//

////        train_title.setText(Objects.requireNonNull(chosen_train.get("next_stop"))+" ("+chosen_train.get("station_type")+")");
////        train_id_text.setText("Train #"+chosen_train.get("train_id"));
////        train_eta.setText(chosen_train.get("train_eta")+"m");
//        Double coord = Double.parseDouble(chosen_train.get("train_distance").trim());
//        train_dist.setText(String.format("%.2f", coord)+" mi");
//        if (chosen_train.get("isApproaching").equals("1")){
//            isApp.setText("Yes");
//        }else{
//            isApp.setText("No");
//
//        }

//        if (chosen_train.get("isDelayed").equals("1")){
//            isDly.setText("Yes");
//        }else{
//            isDly.setText("No");
//
//        }
//        current_coord.setText("("+chosen_train.get("train_lat")+" , "+chosen_train.get("train_lon")+")");
//        home_station.setText(chosen_train.get("main_station"));
//        exit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(TrainInfoPopUp.this, MapsActivity.class));
//            }
//        });
////
////

//
//
//
//        Log.e("POPUP", chosen_train+" ");
//
//

    }

    public HashMap<String, Integer> getTrainLineKeys(){
        HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();

        TrainLineKeyCodes.put("red",R.drawable.red );
        TrainLineKeyCodes.put("blue", R.drawable.blue);
        TrainLineKeyCodes.put("brown", R.drawable.brown);
        TrainLineKeyCodes.put("green", R.drawable.green);
        TrainLineKeyCodes.put("orange", R.drawable.orange);
        TrainLineKeyCodes.put("pink", R.drawable.pink);
        TrainLineKeyCodes.put("purple", R.drawable.purple);
        TrainLineKeyCodes.put("yellow", R.drawable.yellow);
        return TrainLineKeyCodes;
    }


}
