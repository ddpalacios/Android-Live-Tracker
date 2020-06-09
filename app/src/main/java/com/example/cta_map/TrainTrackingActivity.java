package com.example.cta_map;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class TrainTrackingActivity extends AppCompatActivity {

    Bundle bb; // Retrieve data from main screen


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


        Thread t3 = new Thread(new Thread3(message), "Displayer");
        t3.start();


    }


    private Button initiate_button(int widget) {
        Button button = findViewById(widget);
        return button;
    }
//        mapRelativeListView.add_to_list_view(train_etas, TRAIN_RECORD, current_train_info,chosen_trains, connect, current_train_info.get("train_direction"));
}
