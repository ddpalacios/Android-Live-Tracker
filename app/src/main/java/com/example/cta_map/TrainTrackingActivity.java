package com.example.cta_map;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TrainTrackingActivity extends AppCompatActivity {

    Bundle bb; // Retrieve data from main screen

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
                Bundle bundle = msg.getData();
                    ArrayList<Integer> etas = bundle.getIntegerArrayList("train_etas");

                    Log.e("etas", etas + "");



//            Log.e("From handler", etas+"");


        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.train_tracking_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        bb = getIntent().getExtras();
        DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());

        Message message = new Message();
        Thread t1 = new Thread(new Thread1(message, bb), "API_CALL_Thread");
        t1.start();
        Thread t2 = new Thread(new Thread2(message, bb, sqlite), "Content Parser");
        t2.start();
        Thread t3 = new Thread(new Thread3(message, handler), "Displayer");
        t3.start();


    }


//        mapRelativeListView.add_to_list_view(train_etas, TRAIN_RECORD, current_train_info,chosen_trains, connect, current_train_info.get("train_direction"));
}
