package com.example.cta_map.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;


// Instances of this class are fragments representing a single
// object in our collection.
public class FindStation_Fragment extends Fragment {
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


        return inflater.inflate(R.layout.pick_train_line_frag3_layout, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Switch location_switch = view.findViewById(R.id.switch1);
        CTA_DataBase cta_dataBase = new CTA_DataBase(myContext);
        int IsSharingLocation = ContextCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION);
        location_switch.setChecked(IsSharingLocation == 0);
        location_switch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) (buttonView, isChecked) -> {
            if (isChecked){
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
                ((MainActivity) Objects.requireNonNull(getActivity())).updatetUserLocation();

                if (ContextCompat.checkSelfPermission(((MainActivity) Objects.requireNonNull(getActivity())).context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity)getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)){

                        ActivityCompat.requestPermissions((MainActivity)getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }else{
                        ActivityCompat.requestPermissions((MainActivity)getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                        if (IsSharingLocation < 0){
//                            location_switch.setChecked(false);
//
//                        }else{
//                            location_switch.setChecked(true);
//                        }
                    }
                }

            }else{
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");
                cta_dataBase.update("USER_LOCATION", "USER_LAT", "", "HAS_LOCATION= '0'");
                cta_dataBase.update("USER_LOCATION", "USER_LON", "", "HAS_LOCATION = '0'");
                Toast.makeText(myContext, "Location is turned off!", Toast.LENGTH_SHORT).show();
            }
        });





    }




}
