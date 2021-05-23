package com.example.cta_map.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.cta_map.Activities.Adapters.CustomInfoWindowAdapter;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Content_Parser_Thread;
import com.example.cta_map.Backend.Threading.MainNotificationService;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
    public static GoogleMap mMap;
    public static API_Caller_Thread api_caller;
    public static Context context;
    public static Context context1;
    SearchView searchView;
    Cursor cursor;
    ExampleAdapter todoAdapter;
    public static Spinner spinner;
    private String main_train_line;
    public boolean destroyed = false;
    public static int TIMEOUT = 2000;
    public static String BACKGROUND_COLOR_STRING = "#fffea8";
    public boolean isActive;
    public static int check = 0;
    Content_Parser_Thread content_parser;
    public static ActionBar bar;
    public static Fragment frg;
    public static Message message;
    MainPlaceHolder_Fragment mainPlaceHolder_fragment;
    int IsSharingLocation;
    public static Thread t1;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            new Chicago_Transits().setBarTitle(context, message.getStop_id(), message.getTarget_type());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                displayResults(bundle);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayResults(Bundle bundle) {
        ArrayList<Train> current_incoming_trains = (ArrayList<Train>) bundle.getSerializable("new_incoming_trains");
        if (current_incoming_trains != null && current_incoming_trains.size() > 0) {
            ActionBar bar = getSupportActionBar();
            assert bar != null;
            boolean isBeingNotified = new Chicago_Transits().StartNotificationServices(getApplicationContext(), message, current_incoming_trains);
            UpdateFragments(current_incoming_trains);
        } else {
            // if there are no incoming trains
//            new Chicago_Transits().plot_all_markers(getApplicationContext(), message, mMap, current_incoming_trains);
            UpdateFragments(current_incoming_trains);

        }
        updatetUserLocation();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void UpdateFragments(ArrayList<Train> current_incoming_trains) {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());

        if (current_incoming_trains.size() > 0) {
            // Update UI
            chicago_transits.plot_all_markers(getApplicationContext(), message, mMap, current_incoming_trains);


            message.setOld_trains(current_incoming_trains);
            frg = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
            chicago_transits.refresh(frg);
            IsSharingLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);

        } else {
            message.setNearestTrain(null);
            message.setOld_trains(null);
            setTitle("No Trains");
            frg = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
            chicago_transits.refresh(frg);
        }

        cta_dataBase.close();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        context = getApplicationContext();
        context1 = MainActivity.this;


        frg = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
        message = new Message();
        message.setDestoryed(false);
        message.setDoneNotified(false);
        bar = getSupportActionBar();
        message.setMadeBroadcastSwitch(false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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




        Start();





    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Start() {
        IsSharingLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> sett = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null, null, null);
        ArrayList<Object> loc = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_LOCATION, null, null, null);
        if (IsSharingLocation < 0) {
            if (sett != null && loc != null) {
                // always reset if not sharing location
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, "0", CTA_DataBase.IS_SHARING_LOC + " = '1'");
                cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "0", CTA_DataBase.HAS_LOCATION + " = '1'");
                cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.USER_LAT, " ", CTA_DataBase.HAS_LOCATION + " = '1'");
                cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.USER_LON, " ", CTA_DataBase.HAS_LOCATION + " = '1'");
            }
            Toast.makeText(getApplicationContext(), "Location Permission NOT Granted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Location Permission Granted.", Toast.LENGTH_SHORT).show();
            // if user settings is set to ON - update user location
            if (sett != null && loc != null) {
                UserSettings userSettings = (UserSettings) sett.get(0);
                HashMap<String, String> user_loc = (HashMap<String, String>) loc.get(0);
                if (userSettings.getIs_sharing_loc().equals("1") && user_loc.get(CTA_DataBase.HAS_LOCATION).equals("1")) {
                    updatetUserLocation();
                }
            }
        }
        cta_dataBase.close();

        // Now that we have updated our most current location - RUN threads
        run();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(
            // Excecutes when user replies to Permission message

            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_PHONE_STATE) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            Chicago_Transits chicago_transits = new Chicago_Transits();
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null, null, null);
                if (record != null) {
                    cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION, "1", "STOP_ID = '1'");
                    cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, "1", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                    updatetUserLocation();
                    Toast.makeText(MainActivity.this, "Permission Granted! Location Has been updated", Toast.LENGTH_SHORT).show();
                }

//                frg = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
//                chicago_transits.refresh(frg);
                if (message.getT1() != null) {
                    message.getT1().interrupt();
                }
                cta_dataBase.close();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                cta_dataBase.update("USER_LOCATION", "HAS_LOCATION", "0", "STOP_ID = '1'");
                cta_dataBase.update(CTA_DataBase.USER_SETTINGS, CTA_DataBase.IS_SHARING_LOC, "0", CTA_DataBase.USER_SETTINGS_ID + " = '1'");
                cta_dataBase.close();
//                frg = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
//                chicago_transits.refresh(frg);
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setCancelable(true);
//                builder.setTitle("Location");
//                builder.setMessage("It appears that your location is turned off.  Grant application access to use your " +
//                        "location for a more precise train status.");
//                builder.setPositiveButton("Head to settings",
//                        (dialog, which) -> {
//                            final Intent i = new Intent();
//                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            i.addCategory(Intent.CATEGORY_DEFAULT);
//                            i.setData(Uri.parse("package:" + context.getPackageName()));
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                            context.startActivity(i);
//
//                        });
//                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
//                    View inflatedView = getLayoutInflater().inflate(R.layout.alarms_layout, null);
////                    Switch location_switch = (Switch) inflatedView.findViewById(R.id.switch1);
////                    location_switch.isChecked();
////                    location_switch.setChecked(false);
////                    if (MainActivity.message.getT1()!=null) {
////                        MainActivity.message.getT1().interrupt();
////                    }
//
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
            cta_dataBase.close();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    public void updatetUserLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                Toast.makeText(this.context,
                        "LAT: " + location.getLatitude() + " LON: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                ArrayList<Object> userLocationRecord = cta_dataBase.excecuteQuery("*", "USER_LOCATION", null, null, null);
                if (userLocationRecord != null) {
                    ArrayList<Object> userLocationRecord1 = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION + "= '1'", null, null);
                    if (userLocationRecord1 != null) {
                        ToastMessage(getApplicationContext(), "Is FULLY Sharing Location");
                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(this);
                        mMap.setOnMyLocationClickListener(this);
                        message.setSharingFullConnection(true);

                        Double test_lat = 41.88574;
                        Double test_lon = -87.627835;
                        Double real_lat = location.getLatitude();
                        Double real_lon = location.getLongitude();
                        cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.USER_LAT, real_lat + "", "HAS_LOCATION= '1'");
                        cta_dataBase.update(CTA_DataBase.USER_LOCATION, CTA_DataBase.USER_LON, real_lon + "", "HAS_LOCATION = '1'");

                    } else {
                        message.setSharingFullConnection(false);
                        mMap.setMyLocationEnabled(false);
                        mMap.setOnMyLocationButtonClickListener(this);
                        mMap.setOnMyLocationClickListener(this);
                        ToastMessage(getApplicationContext(), "NOT Sharing Location");
                    }
                } else {
                    // if this is the first time
                    UserLocation new_userLocation = new UserLocation();
                    new_userLocation.setLat(location.getLatitude());
                    new_userLocation.setLon(location.getLongitude());
                    new_userLocation.setHasLocation(1);
                    message.setSharingFullConnection(true);
                    cta_dataBase.commit(new_userLocation, CTA_DataBase.USER_LOCATION);
                }
                cta_dataBase.close();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void run() {
        if (isRunning()) {
            Toast.makeText(getApplicationContext(), "Threads Are Running!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Threads Running!", Toast.LENGTH_SHORT).show();

        }
    }

    private void initializeView() {
        FloatingActionButton floatingActionButton = findViewById(R.id.AddStationFloatingButton);
        floatingActionButton.setOnClickListener(v -> {
            if (message.getT1() != null) {
                message.getT1().interrupt();
                message.getApi_caller_thread().cancel();
            }
            Intent intent = new Intent(MainActivity.this, ChooseTrainLineActivity.class);
            startActivity(intent);
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean isRunning() {
        /*
        Running most recent train track based on user selection on startup
         */
        Chicago_Transits chicago_transits = new Chicago_Transits();
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_FAVORITES, CTA_DataBase.ISTRACKING + " = '1'", null, null);
        ArrayList<Object> current_tracking_train = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null, null, null);

        // Tracking last default station or were we tracking a specific train?


        if (current_tracking_train != null) {
            // Currently Tracking Specific Train
            HashMap<String, String> tracking_station = (HashMap<String, String>) current_tracking_train.get(0);
            ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, "MAP_ID = '" + tracking_station.get("MAP_ID") + "'", null, null);
            Station target_station = (Station) record.get(0);


            if (!new Chicago_Transits().isMyServiceRunning(getApplicationContext(), new MainNotificationService().getClass())) {
                // If NO services are running, Call threads.
                new Chicago_Transits().callThreads(getApplicationContext(), handler, message, tracking_station.get("TRAIN_DIR"), tracking_station.get("TRAIN_TYPE"), tracking_station.get("MAP_ID"), false);
            } else {
                // Otherwise, we need to cancel our current threads then start new ones
                chicago_transits.StopThreads(API_Caller_Thread.msg, context);
                chicago_transits.callThreads(getApplicationContext(), handler, message, tracking_station.get("TRAIN_DIR"), tracking_station.get("TRAIN_TYPE"), tracking_station.get("MAP_ID"), false);
            }

            // Set action bar title

            setTitle("To " + target_station.getStop_name() + ".");
            bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor(tracking_station.get("TRAIN_TYPE"), getApplicationContext())));
            cta_dataBase.close();
            return true;
        } else if (user_tracking_record != null) {
            // Tracking last selected
            ToastMessage(getApplicationContext(), "Tracking last selected station");
            HashMap<String, String> tracking_station = (HashMap<String, String>) user_tracking_record.get(0);
            chicago_transits.callThreads(getApplicationContext(), handler, message, tracking_station.get("STATION_DIR"), tracking_station.get("STATION_TYPE"), tracking_station.get("FAVORITE_MAP_ID"), false);
            cta_dataBase.close();

            return true;
        } else {
            // No stations selected or being tracked
            setTitle("Select a station.");
            bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor("red", getApplicationContext())));
            API_Caller_Thread api_caller = new API_Caller_Thread(message, context, handler);
            Thread t1 = new Thread(api_caller);
            message.setT1(t1);
            message.setApi_caller_thread(api_caller);
        }
        cta_dataBase.close();

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem clearData = menu.findItem(R.id.clearData);
        MenuItem getData = menu.findItem(R.id.getData);
        MenuItem userSettings = menu.findItem(R.id.user_settings);
        MenuItem searchView_item = menu.findItem(R.id.app_bar_search);

        MenuItem item11 = menu.findItem(R.id.spinner);
        spinner = (Spinner) item11.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.line_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelected(false);  // must
        menu.getItem(3).setVisible(false);
        initializeSearchView(searchView_item, menu);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.clear();
                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
                String train_line = parent.getSelectedItem().toString();
                Chicago_Transits chicago_transits = new Chicago_Transits();
                main_train_line = train_line;
                bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor(train_line, getApplicationContext())));
                ArrayList<Object> record1 = cta_dataBase.excecuteQuery("*", "CTA_STOPS",
                        chicago_transits.TrainLineKeys(main_train_line) + " = '1' ",
                        null,
                        null);
                LogMessage("Retrieved! query!!...");

                ArrayList<String> check = new ArrayList<>();
                if (record1!= null){
                    Station s = (Station) record1.get(record1.size()-1);
                    chicago_transits.ZoomIn(mMap, 12f,s.getLat(),s.getLon());
                    PolylineOptions options = null;
                    ArrayList<PolylineOptions> l = new ArrayList<>();
                    for (Object r :  record1){
                        Station station = (Station) r;
                        if (check.contains(station.getMap_id())){
                            continue;
                        }else {
                            check.add(station.getMap_id());
//                            message.getT1().interrupt();
                            station.setIsTarget(false);
                            message.setTarget_type(main_train_line);
                            options = new PolylineOptions().width(15).color(Color.RED);
                                        double station_lat = station.getLat();
                                        double station_lon = station.getLon();
                                        LatLng lt = new LatLng(station_lat, station_lon);
                                        options.add(lt);
                                        l.add(options);
//
//                                }


//                            Polyline line = mMap.addPolyline(new PolylineOptions()
//                                    .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
//                                    .width(5)
//                                    .color(Color.RED));
//                            chicago_transits.plot_marker(getApplicationContext(), message, mMap, null, station);
                        }


                    }

                    if (options != null) {
                        for (PolylineOptions options1 : l) {
                            mMap.addPolyline(options1);
                        }
                    }

                }


                cta_dataBase.close();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        userSettings.setOnMenuItemClickListener(item -> {
            Chicago_Transits chicago_transits = new Chicago_Transits();
            chicago_transits.StopThreads(message, getApplicationContext());
            chicago_transits.callThreads(context,handler, message, message.getDir(), message.getTarget_type(),message.getTARGET_MAP_ID(), false);
            ToastMessage(getApplicationContext(), "App Refreshed!");
//                if (!chicago_transits.isMyServiceRunning(context, new MainNotificationService().getClass()) && message.getT1().isAlive()) {
//                    new Chicago_Transits().cancelRunningThreads(message);
//                    message.getT1().interrupt();
//                    try {
//                        message.getT1().join(TIMEOUT);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                startActivity(new Intent(MainActivity.this, UserSettings_Form.class));
            return false;
        });


        getData.setOnMenuItemClickListener(item -> {
            Toast.makeText(getApplicationContext(), "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
            try {
                createDB(R.raw.cta_stops, R.raw.main_stations, R.raw.line_stops);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });


        clearData.setOnMenuItemClickListener(item1 -> {
            Toast.makeText(getApplicationContext(), "Clicked " + item1.getTitle(), Toast.LENGTH_SHORT).show();
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            cta_dataBase.delete_all_records("L_STOPS");
            cta_dataBase.delete_all_records("MAIN_STATIONS");
            cta_dataBase.delete_all_records(CTA_DataBase.CTA_STOPS);
            cta_dataBase.delete_all_records(CTA_DataBase.USER_FAVORITES);
            cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
            cta_dataBase.delete_all_records(CTA_DataBase.ALARMS);
            cta_dataBase.delete_all_records(CTA_DataBase.USER_SETTINGS);


            cta_dataBase.close();
            finish();
            startActivity(getIntent());


            return false;
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeSearchView(MenuItem searchView_item, Menu menu) {
       searchView = (SearchView) searchView_item.getActionView();
        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());

        searchView.setQueryHint("Search for a station");
        searchView.setOnSearchClickListener(v -> {
            ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.MAP_TRACKER, null,null,null);
            if (record != null) {

                HashMap<String, String> CURRENT_STATION = (HashMap<String, String>) record.get(0);
                setTitle("Loading...");
                menu.getItem(3).setVisible(true);
                spinner.setSelection(new Chicago_Transits().getSpinnerPosition(CURRENT_STATION.get(CTA_DataBase.MAP_STATION_TYPE)));
                mMap.clear();
                Chicago_Transits chicago_transits = new Chicago_Transits();
                chicago_transits.StopThreads(message, getApplicationContext());
            }
            try {
//                ArrayList<Object> record1 = cta_dataBase.excecuteQuery("*", "CTA_STOPS",
//                        chicago_transits.TrainLineKeys(message.getTarget_type()) + " = '1' ",
//                        null,
//                        null);
//                LogMessage("Retrieved! query!!...");
//
//                ArrayList<String> check = new ArrayList<>();
//                if (record1!= null){
//                    Station s = (Station) record1.get(record1.size()-1);
//                    chicago_transits.ZoomIn(mMap, 12f,s.getLat(),s.getLon());
//
//                    for (Object r :  record1){
//                        Station station = (Station) r;
//                        if (check.contains(station.getMap_id())){
//                            continue;
//                        }else {
//                            check.add(station.getMap_id());
////                            message.getT1().interrupt();
//                            station.setIsTarget(false);
//                            chicago_transits.plot_marker(getApplicationContext(), message, mMap, null, station);
//                        }
//
//                    }
//
//                }
            } catch(Exception e){
                e.printStackTrace();
            }


            cta_dataBase.close();

        });



                searchView.setOnCloseListener(() -> {
                    mMap.clear();
                    searchView.setQuery("", false);
                    searchView.clearFocus();
            bar.setBackgroundDrawable(new ColorDrawable(new Chicago_Transits().GetBackgroundColor(message.getTarget_type(), getApplicationContext())));
            menu.getItem(3).setVisible(false);
            if (!message.getT1().isAlive()) {
                ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.MAP_TRACKER, null,null,null);
                if (record != null) {
                    HashMap<String, String> CURRENT_STATION = (HashMap<String, String>) record.get(0);

                    new Chicago_Transits().callThreads(context,
                            message.getHandler(),
                            message,
                            CURRENT_STATION.get(CTA_DataBase.MAP_STATION_DIR),
                            CURRENT_STATION.get(CTA_DataBase.MAP_STATION_TYPE),
                            CURRENT_STATION.get(CTA_DataBase.MAP_MAP_ID),
                            false);
                }
            }
                    message.getT1().interrupt();
                    return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextChange(String newText) {
                // SearchView
                Chicago_Transits chicago_transits = new Chicago_Transits();
//                if (message.getT1().isAlive()){
//
//                    chicago_transits.StopThreads(message, getApplicationContext());
//                }

                mMap.clear();
                CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());

//                try {
                LogMessage("Retrieving query..." + main_train_line);
                ArrayList<Object> record1 = cta_dataBase.excecuteQuery("*", "CTA_STOPS",
                            chicago_transits.TrainLineKeys(message.getTarget_type()) + " = '1' AND STATION_NAME",
                            newText,
                            null);
                    ArrayList<String> check = new ArrayList<>();
                    if (record1!= null){
                        Station s = (Station) record1.get(record1.size()-1);
                        chicago_transits.ZoomIn(mMap, 12f,s.getLat(),s.getLon());

                        for (Object r :  record1){
                            Station station = (Station) r;
                            if (check.contains(station.getMap_id())){
                                continue;
                            }else {
                                check.add(station.getMap_id());
//                            message.getT1().interrupt();
                                station.setIsTarget(false);
                                chicago_transits.plot_marker(getApplicationContext(), message, mMap, null, station);
                            }

                        }

                    }




//                }catch (Exception e ){
//                    e.printStackTrace();
//                }



//                ArrayList<Station> non_duplicated_station_list = chicago_transits.removeDuplicates(record1);
//                for (Station station : non_duplicated_station_list){
//                }


//                AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
//                searchAutoCompleteTextView.setThreshold(0);

//                int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
//                AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
//                searchAutoCompleteTextView.setThreshold(0);
//                String query = "SELECT * FROM CTA_STOPS WHERE " + chicago_transits.TrainLineKeys(main_train_line).toUpperCase() + " = '1' AND STATION_NAME LIKE '" + newText + "%'";
//                cursor = db.rawQuery(query, null);
//                ArrayList<HashMap<String, String>> result = new ArrayList<>();
//                // Find ListView to populate
//                ListView lvItems = (ListView) findViewById(R.id.search_items_view);
//                // Setup cursor adapter using cursor from last step
//                todoAdapter = new ExampleAdapter(getApplicationContext(), cursor);
//                // Attach cursor adapter to the ListView
//                lvItems.setAdapter(todoAdapter);
//                cta_dataBase.close();
//                searchView.setSuggestionsAdapter(todoAdapter);
//                cta_dataBase.close();
                return false;
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int createDB(int file1, int file2, int file3) throws IOException {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        BufferedReader file1Buffer = chicago_transits.setup_file_reader(getApplicationContext(), file1);
        BufferedReader file2Buffer = chicago_transits.setup_file_reader(getApplicationContext(), file2);
        BufferedReader file3Buffer = chicago_transits.setup_file_reader(getApplicationContext(), file3);
        chicago_transits.create_line_stops_table(file3Buffer, getApplicationContext(), null);
        chicago_transits.Create_TrainInfo_table(file1Buffer, getApplicationContext());
        chicago_transits.create_main_station_table(file2Buffer, getApplicationContext());
        return 0;
    }


    public static void ToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void LogMessage(String message) {
        Log.e("MainActivity", message);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Chicago_Transits chicago_transits = new Chicago_Transits();
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
        ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);
        chicago_transits.plotTargetStation(context, message, mMap);

        if (IsSharingLocation >= 0 && UserLocation != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
        mapListeners(mMap);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            String map_id = b.getString("map_id");
            ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + map_id + "'", null, null);
            Station tracking_station = (Station) user_tracking_record.get(0);
            chicago_transits.ZoomIn(mMap, 12f,tracking_station.getLat(), tracking_station.getLon());
        }else {
            ArrayList<Object> user_tracking_record = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "ISTRACKING = '1'", null, null);
            if (user_tracking_record != null) {
                HashMap<String, String> station_record = (HashMap<String, String>) user_tracking_record.get(0);
                user_tracking_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + station_record.get("FAVORITE_MAP_ID") + "'", null, null);
                Station tracking_station = (Station) user_tracking_record.get(0);
                chicago_transits.ZoomIn(mMap, 12f,tracking_station.getLat(), tracking_station.getLon());

            }
        }
        cta_dataBase.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mapListeners(GoogleMap mMap){
        mMap.setOnInfoWindowClickListener(marker -> {
            CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
            Chicago_Transits chicago_transits = new Chicago_Transits();
            Train train = findTrainFromMap(marker);
            if (train == null){
                String target_station_name = marker.getTitle();
                String map_id = marker.getSnippet().replaceAll("Station# ", "").trim();
                new Chicago_Transits().callThreads(getApplicationContext(), handler, message, "1",message.getTarget_type(), map_id, false);
            }else {


                Fragment fragment = getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
                if (train.getIsNotified()) { // Resets all trains + its notifications handler
                    chicago_transits.reset(message.getOld_trains(), message);
                    chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                    chicago_transits.refresh(fragment);
                    cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                    chicago_transits.stopService(context);
                    chicago_transits.ZoomIn(mMap, 12f, train.getLat(), train.getLon());


                } else {
                    chicago_transits.reset(message.getOld_trains(), message); // Resets all trains + its notifications handler
                    // Setting selected train for notifications
                    train.setSelected(true);
                    train.setNotified(true);
                    chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
                    chicago_transits.refresh(fragment);
                    cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
                    cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
                    chicago_transits.ZoomIn(mMap, 13f, train.getLat(), train.getLon());
                    message.getT1().interrupt();


                }
            }
            cta_dataBase.close();



//            message.getT1().interrupt();
//            Chicago_Transits chicago_transits = new Chicago_Transits();
//            Fragment fragment= getSupportFragmentManager().findFragmentByTag("main_place_holder_frag");
//            Train train = findTrainFromMap(marker);
//
//           if (train!=null){
//               CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//               if (!train.getIsNotified()){ // if this train is not currently being notified - notify it!
//                   chicago_transits.reset(message.getOld_trains(),message); // Resets all trains + its notifications handler
//                   // Setting selected train for notifications
//                   train.setSelected(true);
//                   train.setNotified(true);
//
//                   // TODO: Do we need to do this? ///
//                   new Chicago_Transits().plot_all_markers(getApplicationContext(), message, mMap, message.getOld_trains());
//                   chicago_transits.refresh(fragment);
//                   /////////////////////////////////
//
//                   cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
//                   cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
//                   cta_dataBase.close();
//
//               }else{
//                   // if we reselect our train tracking train then turn it off!
//                   train.setNotified(false);
//                   train.setSelected(false);
//
//                   chicago_transits.refresh(fragment);
//                   cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
//
//                   //TODO: Add condition to check if there is a service running before stopping a service
//                   chicago_transits.stopService(context);
//                   cta_dataBase.close();
//               }
//           }else{
//               ToastMessage(getApplicationContext(), "Could not locate train# "+ marker.getTitle()+".");
//           }
        });


        mMap.setOnMarkerClickListener(marker -> {
            Chicago_Transits chicago_transits = new Chicago_Transits();
            ArrayList<Train> all_trains = message.getOld_trains();
            Train train = findTrainFromMap(marker); //if returns null then it is a station that was selected


            if (train!=null) {
                CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context, message,false);
                mMap.setInfoWindowAdapter(adapter);
                ToastMessage(getApplicationContext(), train.getRn());
                if (!train.getSelected()) {
                    for (Train train1: all_trains){ // resets all trains
                        if (train1.getIsNotified() && train1.getSelected()){
                            continue;
                        }
                        train1.setSelected(false);
                    }
                    train.setSelected(true);
                    chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());


                }else{
                    train.setSelected(false);
                    chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());

                }


            }else{
                mMap.setInfoWindowAdapter(null);
                ToastMessage(getApplicationContext(), "Station");

            }


//                for (Train train1: all_trains){ // resets all trains
//                    if (train1.getIsNotified() && train1.getSelected()){
//                        continue;
//                    }
//                    train1.setSelected(false);
//                }
//            if (train!=null) {
//                train.setSelected(true);
//            }

            return false;
        });

    }

    private Train findTrainFromMap(Marker marker) {
        ArrayList<Train> all_trains = message.getOld_trains();
        String rn = marker.getTitle();
        Train train = null;
        for (Train t: all_trains){
            if (t.getRn().equals(rn)){
                train = t;
                break;
            }
        }
        return train;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        message.setDestoryed(true);
        if (!new Chicago_Transits().isMyServiceRunning(getApplicationContext(),new MainNotificationService().getClass())) {
            // force stop application if no service is running
            int id = android.os.Process.myPid();
            android.os.Process.killProcess(id);
        }
    }


}


