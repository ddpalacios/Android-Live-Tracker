package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Activities.Classes.RecordView;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;


// Instances of this class are fragments representing a single
// object in our collection.
public class Alarms_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private Context myContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity){
            myContext=  context;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.alarms_layout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.alarm_list_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        Button add_alarm = view.findViewById(R.id.add_alarm);

        add_alarm.setOnClickListener(v -> {
            // Activity to new alarm
            Message message = MainActivity.message;
            if (message.getT1()!=null) {
              new Chicago_Transits().cancelRunningThreads(message);
            }
            Intent intent = new Intent(myContext, NewAlarmSetUp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        CTA_DataBase cta_dataBase = new CTA_DataBase(myContext);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "ALARMS", null, null, null);
        if (record != null) {
            ArrayList<Alarm> alarm_list = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                HashMap<String,String> current_alarm = (HashMap<String, String>) record.get(i);
                Alarm alarm = new Alarm();
                alarm.setAlarm_id(current_alarm.get(CTA_DataBase.ALARM_ID));

                alarm.setWeekLabel(current_alarm.get(CTA_DataBase.WEEK_LABEL));
                alarm.setMap_id(current_alarm.get(CTA_DataBase.ALARM_MAP_ID));
                ArrayList<Object> station = cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, "MAP_ID = '"+ alarm.getMap_id()+"'" , null, null);
                Station record_station = (Station) station.get(0);

                alarm.setMin(current_alarm.get(CTA_DataBase.MIN));
                alarm.setHour(current_alarm.get(CTA_DataBase.HOUR));
                alarm.setTime(current_alarm.get(CTA_DataBase.TIME));
                alarm.setStation_name(record_station.getStation_name());
                alarm.setStationType(current_alarm.get(CTA_DataBase.ALARM_STATION_TYPE));
                alarm.setIsRepeating(Integer.parseInt(current_alarm.get(CTA_DataBase.WILL_REPEAT)));
                alarm_list.add(alarm);

            }
            recyclerView.setAdapter(new Alarm_RecyclerView_Adapter_frag1(alarm_list,myContext));
        }
        cta_dataBase.close();

    }
}






//        Switch location_switch = view.findViewById(R.id.switch1);
//        CTA_DataBase cta_dataBase = new CTA_DataBase(myContext);
//        int IsSharingLocation = ContextCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION);
//        location_switch.setChecked(IsSharingLocation == 0);
//        location_switch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) (buttonView, isChecked) -> {
//            if (isChecked){
//                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
//                ((MainActivity) Objects.requireNonNull(getActivity())).updatetUserLocation();
//
//                if (ContextCompat.checkSelfPermission(((MainActivity) Objects.requireNonNull(getActivity())).context,
//                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//
//                    if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity)getActivity(),
//                            Manifest.permission.ACCESS_FINE_LOCATION)){
//
//                        ActivityCompat.requestPermissions((MainActivity)getActivity(),
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                    }else{
//                        ActivityCompat.requestPermissions((MainActivity)getActivity(),
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////                        if (IsSharingLocation < 0){
////                            location_switch.setChecked(false);
////
////                        }else{
////                            location_switch.setChecked(true);
////                        }
//                    }
//                }
//
//            }else{
//                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");
//                cta_dataBase.update("USER_LOCATION", "USER_LAT", "", "HAS_LOCATION= '0'");
//                cta_dataBase.update("USER_LOCATION", "USER_LON", "", "HAS_LOCATION = '0'");
//                Toast.makeText(myContext, "Location is turned off!", Toast.LENGTH_SHORT).show();
//            }
//        });

