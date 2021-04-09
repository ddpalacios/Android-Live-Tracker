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

import com.example.cta_map.Activities.Classes.RecordView;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.NotificationBuilder;
import com.example.cta_map.ListItem;
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
//        Button test_notification = view.findViewById(R.id.test_notification);
//
//        test_notification.setOnClickListener(v -> {
//            NotificationBuilder notificationBuilder = new NotificationBuilder(myContext, new Intent(myContext, MainActivity.class));
//            notificationBuilder.notificationDialog("ALARM GOING OFF", "Its off!", null);
//        });

        add_alarm.setOnClickListener(v -> {
            startActivity(new Intent(myContext, Pop.class));
        });


        CTA_DataBase cta_dataBase = new CTA_DataBase(myContext);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "ALARMS", null, null, null);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        if (record != null) {
            ArrayList<RecordView> arrayList = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                HashMap<String,String> current_live_train = (HashMap<String, String>) record.get(i);
                if (current_live_train.get("STATION_TYPE") == null){
                    continue;
                }
               RecordView listItem = new RecordView();
               listItem.setImage(chicago_transits.getTrainImage(current_live_train.get("STATION_TYPE")));
               listItem.setMain_title(current_live_train.get("STATION_NAME") );
                listItem.setTitle1(current_live_train.get("TIME"));
                listItem.setTitle2(current_live_train.get("WEEK_LABEL"));
                listItem.setAlarm_id(current_live_train.get("ALARM_ID"));
                arrayList.add(listItem);

            }
            MainActivity mainActivity = (MainActivity)getActivity();
            recyclerView.setAdapter(new Alarm_RecyclerView_Adapter_frag1(arrayList, mainActivity));
        }
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

