package com.example.cta_map.Activities;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements Serializable  {
    private GoogleMap mMap;
    API_Caller_Thread  api_caller;
    Content_Parser_Thread content_parser;
    Message message = new Message();
    MainPlaceHolder_Fragment mainPlaceHolder_fragment;
    Thread t1,t2;

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
//            mMap.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                displayResults(bundle);
            }
        }
    };


    public void displayResults(Bundle bundle){
        Toast.makeText(getApplicationContext(), "TRAINS!", Toast.LENGTH_SHORT).show();
        ArrayList<Train> current_incoming_trains = (ArrayList<Train>) bundle.getSerializable("new_incoming_trains");
        message.setIncoming_trains(current_incoming_trains);
        for (Train train: current_incoming_trains){
        }
        FragmentManager fragment_manager = mainPlaceHolder_fragment.getChildFragmentManager();
        if (fragment_manager !=null) {
            Fragment TrainTimes_fragment = fragment_manager.findFragmentByTag("f1");
            Fragment mapDetails_fragment = fragment_manager.findFragmentByTag("f0");
            if (TrainTimes_fragment != null || mapDetails_fragment !=null) {
                Log.e("Frag Update", "Frag update");
                FragmentTransaction fragmentTransaction = fragment_manager.beginTransaction();
                fragmentTransaction.detach(TrainTimes_fragment);
                fragmentTransaction.attach(TrainTimes_fragment);
                fragmentTransaction.detach(mapDetails_fragment);
                fragmentTransaction.attach(mapDetails_fragment);
                fragmentTransaction.commitAllowingStateLoss();


            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        View inflatedView = getLayoutInflater().inflate(R.layout.train_times_frag_layout, null);
        api_caller = new API_Caller_Thread(message);
        content_parser = new Content_Parser_Thread(getApplicationContext(),handler,message);
        t1 = new Thread(api_caller);
        t2 = new Thread(content_parser);
        message.setT1(t1);
        message.setT2(t2);
        message.setApi_caller_thread(api_caller);
        message.setContent_parser_thread(content_parser);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mainPlaceHolder_fragment = new MainPlaceHolder_Fragment();
        ft.replace(R.id.place_holder, mainPlaceHolder_fragment, "main_place_holder_frag");
        ft.commit();
        initializeView();
        if (isRunning()){
            Toast.makeText(getApplicationContext(), "Threads Are Running!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "No Threads Running!", Toast.LENGTH_SHORT).show();

        }
    }
    private void initializeView(){
        FloatingActionButton floatingActionButton = findViewById(R.id.AddStationFloatingButton);
        floatingActionButton.setOnClickListener(v -> {
            message.getT1().interrupt();
            message.getApi_caller_thread().cancel();
            message.getT2().interrupt();
            message.getContent_parser_thread().cancel();
            Intent intent = new Intent(MainActivity.this, ChooseTrainLineActivity.class);
            startActivity(intent);
        });


    }

    private Boolean isRunning(){
        /*
        Running most recent train track based on user selection on startup
         */
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null,null);
        cta_dataBase.close();
        if (user_tracking_record!=null){
            HashMap<String, String> tracking_station = (HashMap<String, String>) user_tracking_record.get(0);
            callThreads(tracking_station);
            return true;
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem clearData = menu.findItem(R.id.clearData);
        MenuItem getData = menu.findItem(R.id.getData);

        getData.setOnMenuItemClickListener(item -> {
            Toast.makeText(getApplicationContext(), "Clicked "+ item.getTitle(), Toast.LENGTH_SHORT).show();
            try {
                createDB(R.raw.cta_stops, R.raw.main_stations, R.raw.line_stops);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });


        clearData.setOnMenuItemClickListener(item1 -> {
            Toast.makeText(getApplicationContext(), "Clicked "+ item1.getTitle(), Toast.LENGTH_SHORT).show();
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            cta_dataBase.delete_all_records("L_STOPS");
            cta_dataBase.delete_all_records("MAIN_STATIONS");
            cta_dataBase.delete_all_records("CTA_STOPS");
            cta_dataBase.delete_all_records("USER_FAVORITES");
            cta_dataBase.delete_all_records("MARKERS");
            cta_dataBase.close();
            finish();
            startActivity(getIntent());


            return false;
        });
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int createDB(int file1, int file2, int file3) throws IOException {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader file1Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file1);
        BufferedReader file2Buffer  = chicago_transits.setup_file_reader(getApplicationContext(), file2);
        BufferedReader file3Buffer = chicago_transits.setup_file_reader(getApplicationContext(), file3);
        chicago_transits.create_line_stops_table(file3Buffer, getApplicationContext(), null);
        chicago_transits.Create_TrainInfo_table(file1Buffer, getApplicationContext());
        chicago_transits.create_main_station_table(file2Buffer, getApplicationContext());
        chicago_transits.createMarkerTable(getApplicationContext());
        return 0;
    }
    private void callThreads(HashMap<String, String> target_station){
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+target_station.get("FAVORITE_MAP_ID")+"'", null,null);
        HashMap<String,String> tracking_record = (HashMap<String, String>) record.get(0);
        cta_dataBase.close();
        message.setTARGET_MAP_ID(tracking_record.get("MAP_ID"));
        message.setDir(target_station.get("STATION_DIR"));
        message.setTarget_type(target_station.get("STATION_TYPE"));
        message.keepSending(true);
        message.setTarget_station(tracking_record);
        message.getT1().start();
        message.getT2().start();
    }
}


