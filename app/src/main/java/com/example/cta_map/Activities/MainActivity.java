package com.example.cta_map.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.MapMarker;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
    public GoogleMap mMap;
    API_Caller_Thread api_caller;
    Context context;
    Content_Parser_Thread content_parser;
    private Train NotificationTrain = null;
    Message message = new Message();
    MainPlaceHolder_Fragment mainPlaceHolder_fragment;
    int IsSharingLocation;
    Thread t1, t2;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            if (mMap!= null) {
                mMap.clear();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                displayResults(bundle);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("MissingPermission")
    public void displayResults(Bundle bundle) {
        ArrayList<Train> all_chosen_trains = (ArrayList<Train>) bundle.getSerializable("new_incoming_trains");
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        message.setOld_trains(all_chosen_trains);
        if (all_chosen_trains !=null && all_chosen_trains.size() > 0){
            ActionBar bar = getSupportActionBar();
            assert bar != null;
            Chicago_Transits chicago_transits = new Chicago_Transits();
            setTitle("To "+all_chosen_trains.get(0).getNextStaNm() +".");
            ArrayList<Object> target_station_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + message.getTARGET_MAP_ID() + "'", null, null);
            HashMap<String, String> target_station = (HashMap<String, String>) target_station_record.get(0);
            plot_marker(null, target_station); // Plot Target Station
            for (Train train : all_chosen_trains){
                Log.e("INCOMING", train.getRn() + "# | "+ train.getTrain_type() + " | "+train.getTarget_eta()+"m");
                plot_marker(train, null);
            }

        }else{
            ActionBar bar = getSupportActionBar();
            assert bar != null;
            setTitle("No Trains Available");


        }


//        for (Train train: all_chosen_trains){
//            Log.e("TRAIN", "RN: "+train.getRn()+". Is Selected? "+train.getSelected() +" Icon? "+ train.getViewIcon());
//        }
//
//        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//        ArrayList<Object> target_station_record = cta_dataBase.excecuteQuery("*",
//                "CTA_STOPS",
//                "MAP_ID = '" + message.getTARGET_MAP_ID() + "'",
//                null,
//                null);
//
//        HashMap<String, String> target_station = (HashMap<String, String>) target_station_record.get(0);
//        plot_marker(null, target_station); // Plot Target Station
//        Log.e("TRAIN", "DONE");
//        if (all_chosen_trains.size() > 0) {
//
//            for (Train train : all_chosen_trains) {  // Find Train that has an icon
//                if (train.getViewIcon() && train.getSelected()) {
//                    NotificationTrain = train;
//                    break;
//                }
//            }
//            for (Train train : all_chosen_trains){
//                if (NotificationTrain !=null) {
//                    if (NotificationTrain.getRn().equals(train.getRn())) {
//                        train.setSelected(true);
//                        train.setViewIcon(true);
//                        plot_marker(train, null);
//                        continue;
//                    }
//                }
//                plot_marker(train, null);
//            }
//            Train selected_train = trainLookup(null, true);
//            if (selected_train!=null) {
//                plot_marker(selected_train, null);
//            }
//            cta_dataBase.close();
//        }
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.detach(mainPlaceHolder_fragment);
//        ft.attach(mainPlaceHolder_fragment);
//        ft.commitAllowingStateLoss();

        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commitAllowingStateLoss();
//
//        updateFragment("f0");
//        updateFragment("f1");



        IsSharingLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null,null);
        cta_dataBase.close();
        if (IsSharingLocation == 0 && UserLocation !=null){
            updatetUserLocation();
        }
    }
    private Train trainLookup(String train_id, Boolean findSelected){
        if (!findSelected) {
            for (Train train : message.getOld_trains()) {
                if (train.getRn().equals(train_id)) {
                    return train;
                }
            }
        }else{
            for (Train train : message.getOld_trains()) {
                if (train.getSelected() && !train.getViewIcon()) {
                    return train;
                }
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void plot_marker(Train train, HashMap<String, String > target_station){
        final MapMarker mapMarker = new MapMarker(mMap, getApplicationContext(), message);
        if (train == null){
            mapMarker.addMarker(
                    Double.parseDouble(Objects.requireNonNull(target_station.get("LAT"))),
                    Double.parseDouble(Objects.requireNonNull(target_station.get("LON"))),
                    "Station# "+target_station.get("MAP_ID"),
                    "target",
                    1f,
                    false,
                    true,
                    false,
                    target_station.get("STATION_NAME"), false);
        }else {
            if (train.getSelected()) {
                if (!train.getViewIcon()) {
                    mapMarker.addMarker(
                            train.getLat(),
                            train.getLon(),
                            "Train# " + train.getRn(),
                            train.getStatus().toLowerCase(),
                            1f,
                            true,
                            false,
                            false,
                            " " + train.getTarget_eta() + "m",
                            false).showInfoWindow();
                }else{
                    mapMarker.addMarker(
                            train.getLat(),
                            train.getLon(),
                            "Train# " + train.getRn(),
                            train.getStatus().toLowerCase(),
                            1f,
                            true,
                            false,
                            false,
                            " " + train.getTarget_eta() + "m",
                            true).showInfoWindow();

                }
            }else {
                if (!train.getViewIcon()) {
                    mapMarker.addMarker(
                            train.getLat(),
                            train.getLon(),
                            "Train# " + train.getRn(),
                            train.getTrain_type().toLowerCase(),
                            1f,
                            true,
                            false,
                            false,
                            " " + train.getTarget_eta() + "m", false);

                }
            }
        }
    }







    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        context = getApplicationContext();
        IsSharingLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        api_caller = new API_Caller_Thread(message);
        content_parser = new Content_Parser_Thread(getApplicationContext(), handler, message);
        t1 = new Thread(api_caller);
        t2 = new Thread(content_parser);
        message.setT1(t1);
        message.setT2(t2);
        message.setApi_caller_thread(api_caller);
        message.setContent_parser_thread(content_parser);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        AppBarLayout mAppBarLayout = findViewById(R.id.app_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);


        mainPlaceHolder_fragment = new MainPlaceHolder_Fragment();
        ft.replace(R.id.place_holder, mainPlaceHolder_fragment, "main_place_holder_frag");
        ft.commit();
        initializeView();


        if (IsSharingLocation < 0){
            Toast.makeText(getApplicationContext(), "Not Sharing Location", Toast.LENGTH_SHORT).show();
            run();
        }else{
            Toast.makeText(getApplicationContext(), "Sharing Location", Toast.LENGTH_SHORT).show();
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            ArrayList<Object> userLocationRecord = cta_dataBase.excecuteQuery("*", "USER_LOCATION", null, null, null);
            if (userLocationRecord != null){
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "HAS_LOCATION = '0'");
                updatetUserLocation();
            }else{
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "HAS_LOCATION= '1'");
                updatetUserLocation();
            }
            run();
            cta_dataBase.close();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_PHONE_STATE) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "1", "STOP_ID = '1'");
                cta_dataBase.close();
                updatetUserLocation();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");
                cta_dataBase.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Location");
                builder.setMessage("It appears that your location is turned off.  Grant application access to use your " +
                                        "location for a more precise train status.");
                builder.setPositiveButton("Head to settings",
                        (dialog, which) -> {
                            final Intent i = new Intent();
                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + context.getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(i);

                        });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    View inflatedView = getLayoutInflater().inflate(R.layout.alarms_layout, null);
//                    Switch location_switch = (Switch) inflatedView.findViewById(R.id.switch1);
//                    location_switch.isChecked();
//                    location_switch.setChecked(false);

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void updatetUserLocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                Toast.makeText(this.context,
                        "LAT: "+ location.getLatitude() + " LON: "+ location.getLongitude(), Toast.LENGTH_SHORT).show();
                ArrayList<Object> userLocationRecord = cta_dataBase.excecuteQuery("*", "USER_LOCATION", null, null, null);
                if (userLocationRecord !=null){
                    cta_dataBase.update("USER_LOCATION", "USER_LAT", location.getLatitude()+"", "HAS_LOCATION= '1'");
                    cta_dataBase.update("USER_LOCATION", "USER_LON", location.getLongitude()+"", "HAS_LOCATION = '1'");
                }
                else {
                    UserLocation new_userLocation = new UserLocation();
                    new_userLocation.setLat(location.getLatitude());
                    new_userLocation.setLon(location.getLongitude());
                    new_userLocation.setHasLocation(1);
                    cta_dataBase.commit(new_userLocation, "USER_LOCATION");
                }
                cta_dataBase.close();
            }
        });

    }

    private void run(){
        if (isRunning()) {
            Toast.makeText(getApplicationContext(), "Threads Are Running!", Toast.LENGTH_SHORT).show();
        } else {
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
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            String map_id = b.getString("map_id");
            String dir= b.getString("station_dir");
            String station_type= b.getString("station_type");
            callThreads(dir, station_type, map_id);
            return true;

        }else {
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null, null);
            cta_dataBase.close();
            if (user_tracking_record != null) {
                HashMap<String, String> tracking_station = (HashMap<String, String>) user_tracking_record.get(0);
                callThreads(tracking_station.get("STATION_DIR"), tracking_station.get("STATION_TYPE"), tracking_station.get("FAVORITE_MAP_ID"));
                return true;
            }
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
            cta_dataBase.delete_all_records("ALARMS");

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
    private void callThreads(String dir, String station_type, String map_id){
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+map_id+"'", null,null);
        HashMap<String,String> tracking_record = (HashMap<String, String>) record.get(0);
        Log.e("TRACKING", "Starting to track: "+tracking_record.get("STATION_NAME")+".");
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        setTitle("To: "+tracking_record.get("STATION_NAME")+".");
        bar.setBackgroundDrawable(new ColorDrawable(chicago_transits.GetBackgroundColor(station_type, getApplicationContext())));

        cta_dataBase.close();
        message.setTARGET_MAP_ID(tracking_record.get("MAP_ID"));
        message.setDir(dir);
        message.setTarget_type(station_type);
        message.keepSending(true);
        message.setTarget_station(tracking_record);
        message.getT1().start();
        message.getT2().start();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mapListeners(mMap);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            String map_id = b.getString("map_id");
            ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + map_id + "'", null, null);
            HashMap<String, String> tracking_station = (HashMap<String, String>) user_tracking_record.get(0);
            chicago_transits.ZoomIn(mMap, 12f, Double.parseDouble(tracking_station.get("LAT")), Double.parseDouble(tracking_station.get("LON")));
        }else {
            ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null, null);
            if (user_tracking_record != null) {
                HashMap<String, String> station_record = (HashMap<String, String>) user_tracking_record.get(0);
                user_tracking_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + station_record.get("FAVORITE_MAP_ID") + "'", null, null);
                HashMap<String, String> tracking_station = (HashMap<String, String>) user_tracking_record.get(0);
                chicago_transits.ZoomIn(mMap, 12f, Double.parseDouble(tracking_station.get("LAT")), Double.parseDouble(tracking_station.get("LON")));
            }
        }
        cta_dataBase.close();
    }



//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void mapListeners(GoogleMap mMap){
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            /*
//            Select Target Station / Set Notifications
//             */
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//                if (message.getOld_trains() != null){
//                    String TRAIN_ID  =  marker.getSnippet().replaceAll("Train#", "").trim();
//                    Train selected_train = trainLookup(TRAIN_ID, false);
//
//
//                    if (selected_train !=null){ // If found selected train...
//                        message.setSendingNotifications(false);
//
//                        if (selected_train.getSelected() && selected_train.getViewIcon()){
//                            selected_train.setViewIcon(false);
//                            NotificationTrain = null;
//                            message.getT2().interrupt();
//
//                            return;
//                        }
//                        // Reset all markers
//                        for (Train train : message.getOld_trains()){
//                            if (train.getViewIcon()){
//                                train.setViewIcon(false);
//                                train.setSelected(false);
//                            }
//                        }
//                        // Then set the selected marker
//                        selected_train.setSelected(true);
//                        selected_train.setViewIcon(true);
////                        greenNotified = false;
////                        YellowNotified = false;
////                        RedNotified = false;
//
//                        NotificationTrain = selected_train;
//                        message.getT2().interrupt();
//
//
////                                    initiateNotifications(selected_train);
////                        content_parser.interrupt();
//
//
//                    }
//
//                }else{
//                    String MAP_ID = marker.getSnippet().replaceAll("Station# ", "");
//                    message.setTARGET_MAP_ID(MAP_ID);
//                    Object target_stations = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + MAP_ID + "'", null, null).get(0);
//                    if (target_stations != null) {
//                        HashMap<String, String> new_target_station = (HashMap<String, String>) target_stations;
////                        StartThreads(new_target_station);
//                        Toast.makeText(getApplicationContext(), new_target_station.get("STATION_NAME") + " " + message.getTarget_type(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No Station Found.", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//                cta_dataBase.close();
//
//            }
//        });
//
//
//        mMap.setOnMarkerClickListener(marker -> {
//            if (message.getOld_trains() != null){
//                String TRAIN_ID  =  marker.getSnippet().replaceAll("Train#", "").trim();
//                Train selected_train = trainLookup(TRAIN_ID, false);
//                if (selected_train !=null) {
//                    if (selected_train.getSelected() && !selected_train.getViewIcon()){
//                        selected_train.setSelected(false);
//                        message.getT2().interrupt();
//                        return false;
//                    }
//                    for (Train train : message.getOld_trains()){
//                        if (train.getViewIcon()){
//                            continue;
//                        }
//                        train.setSelected(false);
//                    }
//                    selected_train.setSelected(true);
//                    message.getT2().interrupt();
//
//
//                }
//            }
//            return false;
//        });
//
//    }


    public void updateFragment(String frag_tag){
        FragmentManager fragment_manager = mainPlaceHolder_fragment.getChildFragmentManager();
        if (fragment_manager != null) {
            Fragment TrainTimes_fragment = fragment_manager.findFragmentByTag(frag_tag); // e.g. f0
            Fragment mapDetails_fragment = fragment_manager.findFragmentByTag(frag_tag); // e.g. f1
            if (TrainTimes_fragment != null || mapDetails_fragment != null) {
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

}


