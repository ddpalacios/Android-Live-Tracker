package com.example.cta_map.Displayers;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cta_map.Activities.Adapters.CustomInfoWindowAdapter;
import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.ExampleService;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.Activities.MyBroadCastReciever;
import com.example.cta_map.Backend.Threading.API_Caller_Thread;
import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.DataBase.CTA_Stops;
import com.example.cta_map.DataBase.L_stops;
import com.example.cta_map.DataBase.MainStation;
import com.example.cta_map.DataBase.Markers;
import com.example.cta_map.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class Chicago_Transits {

    public  Integer getTrainImage(String train_line){
        if (train_line!=null) {
            if (train_line.toLowerCase().equals("purple")) {
                train_line = "p";

            } else if (train_line.toLowerCase().equals("orange")) {
                train_line = "org";

            } else if (train_line.toLowerCase().equals("yellow")) {
                train_line = "y";

            } else if (train_line.toLowerCase().equals("green")) {
                train_line = "g";
            } else if (train_line.toLowerCase().equals("brown")) {
                train_line = "brn";

            }
            HashMap<String, Integer> TrainLineKeyCodes = new HashMap<>();
            train_line = train_line.toLowerCase().trim();
            TrainLineKeyCodes.put("red", R.drawable.red);
            TrainLineKeyCodes.put("blue", R.drawable.blue);
            TrainLineKeyCodes.put("brn", R.drawable.brown);
            TrainLineKeyCodes.put("g", R.drawable.green);
            TrainLineKeyCodes.put("org", R.drawable.orange);
            TrainLineKeyCodes.put("pink", R.drawable.pink);
            TrainLineKeyCodes.put("p", R.drawable.purple);
             TrainLineKeyCodes.put("target", R.drawable.target);

            TrainLineKeyCodes.put("y", R.drawable.yellow);
            return TrainLineKeyCodes.get(train_line.toLowerCase());
        }
        return  R.drawable.red;
    }


    public void createMarkerTable(Context context){
        CTA_DataBase cta_dataBase= new CTA_DataBase(context);
        ArrayList<Object> redLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "RED = '1'", null,null);
        ArrayList<Object> BlueLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "BLUE = '1'",null, null);
        ArrayList<Object> GreenLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "G = '1'", null,null);
        ArrayList<Object> OrangeLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "ORG = '1'", null,null);
        ArrayList<Object> PurpleLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "P = '1'", null,null);
        ArrayList<Object> BrownLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "BRN = '1'", null,null);
        ArrayList<Object> PinkLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "PINK = '1'", null,null);
        ArrayList<Object> YellowLine = cta_dataBase.excecuteQuery("MAP_ID,LAT, LON, STATION_NAME", "CTA_STOPS", "Y = '1'", null,null);
        populateMarkers(context, redLine, "red");
        populateMarkers(context, BlueLine, "blue");
        populateMarkers(context, GreenLine, "green");
        populateMarkers(context, PurpleLine, "purple");
        populateMarkers(context, BrownLine, "brown");
        populateMarkers(context, YellowLine, "yellow");
        populateMarkers(context, PinkLine, "pink");
        populateMarkers(context, OrangeLine, "orange");




    }

 @RequiresApi(api = Build.VERSION_CODES.M)
 public void scheduleAlarm(Context context, int dayOfWeek, Alarm alarm_record, String id, String alarm_id) {
     int hours = Integer.parseInt(alarm_record.getHour());
     int minutes = Integer.parseInt(alarm_record.getMin());
     String hour_of_day = alarm_record.getTime().replace(":", "").replaceAll("[^A-Za-z]", "");
     if (hour_of_day.toLowerCase().trim().equals("pm")) {
         hours = hours + 12; // 24 hour format
     }

     Calendar calendar = Calendar.getInstance();
     calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
     calendar.set(Calendar.HOUR_OF_DAY, hours);
     calendar.set(Calendar.MINUTE, minutes);
     calendar.set(Calendar.SECOND, 0);

     Log.e("Alarm",calendar.getTimeInMillis()+" | "+ System.currentTimeMillis() );

     // Check we aren't setting it in the past which would trigger it to fire instantly
     if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
         calendar.add(Calendar.DAY_OF_YEAR, 7);
         Log.e("Alarm", "Day: " + dayOfWeek + " Will first fire on: " + calendar.get(Calendar.DAY_OF_MONTH) + " at " + hours + ":" + minutes + ". Then every 7 days after");
     }else{
         Log.e("Alarm", "Day: " + dayOfWeek + " Will first fire on: " + calendar.get(Calendar.DAY_OF_MONTH) + " at " + hours + ":" + minutes + ". Then every 7 days after");

     }

    int days = (int) (calendar.getTimeInMillis() / 1000 * 60*60*24);
     Log.e("Alarm","Alarm #"+id+ " is set in "+calendar.getTimeInMillis() + " or "+ days+ " days.");

     // Set this to whatever you were planning to do at the given time
     Intent intent = new Intent(context, MyBroadCastReciever.class);
     intent.putExtra("alarm_id", alarm_id+"");

     intent.putExtra("map_id", alarm_record.getMap_id());
     intent.putExtra("direction", alarm_record.getDirection());
     intent.putExtra("day_of_week_alarm_id", id);
     intent.putExtra("station_type", alarm_record.getStationType());
     intent.putExtra("day_of_week",dayOfWeek);


     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
             Integer.parseInt(id), intent, PendingIntent.FLAG_UPDATE_CURRENT);
     alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
 }


    private void populateMarkers(Context context, ArrayList<Object> all_stations, String station_type){
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Markers> list_of_markers = new ArrayList<>();
        for (Object station : all_stations){
            Markers marker = new Markers();
            HashMap<String, String> current_station = (HashMap<String, String>) station;
            marker.setMarker_name(current_station.get("STATION_NAME"));
            marker.setMarker_id(current_station.get("MAP_ID"));
            marker.setMarker_type(station_type);
            marker.setMarker_lat(Double.parseDouble(current_station.get("LAT")));
            marker.setMarker_lon(Double.parseDouble(current_station.get("LON")));
            list_of_markers.add(marker);

        }
        for (Markers marker : list_of_markers) {
            cta_dataBase.commit(marker, "MARKERS");
        }
    }


  public void CallStatusUpdate(Context context, Train train, Message message, String notification_message){
      if (train !=null && train.getStatus() == null){
            if (train.getTarget_eta() > 5 && !message.isScheduledGreenNotified()){
                CallNotificationService(context, train, "Status: GREEN | Train #" + train.getRn() + " is scheduled to depart in " + train.getTarget_eta() + "m", message, false);
                message.setScheduledGreenNotified(true);
            }
            else if (train.getTarget_eta() >=3 && train.getTarget_eta() <=5 && !message.isScheduledYellowNotified()){
                CallNotificationService(context,  train, "Status: YELLOW |Train #"+train.getRn() + " is scheduled to depart in "+ train.getTarget_eta()+"m", message, false);
                message.setScheduledYellowNotified(true);
            }else {
                if (!message.isScheduledRedNotified()) {
                    CallNotificationService(context, train, "Status: RED | Train #" + train.getRn() + " is scheduled to depart in " + train.getTarget_eta() + "m", message, false);
                    message.setScheduledRedNotified(true);
                }
            }
        }else {
            if (train != null) {
                CallNotificationService(context, train, notification_message, message, true);
                if (train.getStatus().equals("GREEN") && !message.getGreenNotified()) {
                    message.setGreenNotified(true);
                    CallNotificationService(context, train, notification_message, message, false);

                } else if (train.getStatus().equals("YELLOW") && !message.getYellowNotified()) {
                    message.setYellowNotified(true);
                    CallNotificationService(context, train, notification_message, message, false);

                } else if (train.getStatus().equals("RED")) {
                    if (!message.getRedNotified()) {
                        message.setRedNotified(true);
                        CallNotificationService(context, train, notification_message, message, false);

                    }
                    if (train.getIsApp().equals("1") && !message.getApproachingNotified()) {
                        Chicago_Transits chicago_transits = new Chicago_Transits();
                        message.setApproachingNotified(true);
                        if (MainActivity.mMap != null) {
                            chicago_transits.ZoomIn(MainActivity.mMap, 15f, train.getLat(), train.getLon());
                        }
                        CallNotificationService(context, train, notification_message, message, false);

                    } else if (message.getDoneNotified()) {
                        CallNotificationService(context, train, notification_message, message, false);

                    }
                }
            }else {
                if (message.getDoneNotified()) {
                        CallNotificationService(context, train, notification_message, message, false);

                    }
            }
        }

  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  public void plot_all_markers(Context context, Message message, GoogleMap mMap, ArrayList<Train> all_trains){
        /*
        Plots target station and all trains on maps
         */
        mMap.clear();
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
         ArrayList<Object> target_station_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + message.getTARGET_MAP_ID() + "'", null, null);
        Station target_station = (Station) target_station_record.get(0);
        Chicago_Transits chicago_transits = new Chicago_Transits();
        target_station.setIsTarget(true);
        chicago_transits.plot_marker(context,message,mMap,null, target_station); // Plot Target Station

      if (all_trains != null && all_trains.size() > 0) {
          for (Train train : all_trains) {
              Log.e("INCOMING", train.getRn() + "# | " + train.getRt() + " | " + train.getTarget_eta() + "m Selected? " + train.getSelected() + "| Notfifed?: " + train.getIsNotified() + " | Status?: "+ train.getStatus()+ " | Scheduled?:  "+ train.getIsSch());
              plot_marker(context, message, mMap, train, null);
          }
      }
        cta_dataBase.close();
  }
    private Station find(ArrayList<Station> stationArrayList,Station station) {
        for (Station station1 : stationArrayList){
            if (station1.getMap_id().equals(station.getMap_id())){
                return station;
            }
        }
        return null;

    }

    public   ArrayList<Station>  removeDuplicates(ArrayList<Object> record) {
        ArrayList<Station> stationArrayList = new ArrayList<>();
        ArrayList<Station> non_duplicated_station_list = new ArrayList<>();
        if (record!=null){
            for (Object r : record){
                Station station = (Station) r;
                stationArrayList.add(station);
            }
            for (Station station : stationArrayList){
                Station found_station = find(non_duplicated_station_list, station);
                if (found_station!=null){
                    continue;
                }else{
                    non_duplicated_station_list.add(station);
                }
            }

        }
        return non_duplicated_station_list;
    }

    public void StopThreads(Message message){
        int res = new Chicago_Transits().cancelRunningThreads(message);
        if (res > 0) {
            if (message.getT1().isAlive()) {
                message.getT1().interrupt();
            }else{
                MainActivity.LogMessage("Thread not alive.");
            }
            try {
               message.getT1().join(MainActivity.TIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void CallNotificationService(Context context, Train train, String notification_message, Message message, boolean b){

        if (train!=null) {
            Intent serviceIntent = new Intent(context, ExampleService.class);
            serviceIntent.putExtra("has_train", true);
            String status;
            status = train.getStatus();
            serviceIntent.putExtra("willUpdate", b);

            serviceIntent.putExtra("train_status", status);
            serviceIntent.getBooleanExtra("isDone", message.getDoneNotified());
            serviceIntent.putExtra("map_id", train.getTarget_id());
            serviceIntent.putExtra("train_type", train.getRt());
            serviceIntent.putExtra("train_dir", train.getTrDr());
            serviceIntent.putExtra("notification_message", notification_message);
            ContextCompat.startForegroundService(context, serviceIntent);
        }else{
            Intent serviceIntent = new Intent(context, ExampleService.class);
            serviceIntent.putExtra("has_train", false);
            serviceIntent.putExtra("isDone", true);
            serviceIntent.putExtra("notification_message", notification_message);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void refresh(Fragment fragment){
        if (fragment!=null) {
            FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
            ft.detach(fragment);
            ft.attach(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    public void stopService(Context context){
        try {
            context.stopService(new Intent(context, ExampleService.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


     public void reset(ArrayList<Train> trainList, Message message){
        if (trainList!=null) {
            for (Train train1 : trainList) {
                train1.setSelected(false);
                train1.setNotified(false);
            }

        }
         // once we have selected a new train to track - we also reset its notification
         // This allows the user to receive all 3-4 notifications
         message.setYellowNotified(false);
         message.setRedNotified(false);
         message.setGreenNotified(false);
         message.setApproachingNotified(false);
    }

    public int cancelRunningThreads(Message message){
        if (message.getApi_caller_thread() != null && message.getT1()!=null){
            message.getApi_caller_thread().cancel();
            message.getT1().interrupt();
            return 1;

        }else{
            return -1;
        }

    }


    public String getDirectionLabel(String label){
        String direction_label = null;
        if (label.equals("N")){
            direction_label = "North";
        }else if (label.equals("E")){
            direction_label = "East";


        }else if (label.equals("S")){
            direction_label = "South";

        }else if (label.equals("W")){
            direction_label = "West";
        }else{
            direction_label = "";
        }

        return direction_label;
    }


    public Integer getUserEstimatedTimeArrivalToStation(ArrayList<Object> user_location_record, Station target_station){

        if (user_location_record!=null) {
            HashMap<String, String> user_location = (HashMap<String, String>) user_location_record.get(0);
             if (user_location.get(CTA_DataBase.HAS_LOCATION).equals("1")) {
                 // only retrieve location if location is being shared
                 if (!user_location.get(CTA_DataBase.USER_LAT).equals("") || !user_location.get(CTA_DataBase.USER_LON).equals("")) {
                     Double lat = Double.parseDouble(user_location.get(CTA_DataBase.USER_LAT));
                     Double lon = Double.parseDouble(user_location.get(CTA_DataBase.USER_LON));
                     Double user_distance_miles = calculate_coordinate_distance(lat, lon, target_station.getLat(), target_station.getLon());
                     Time time = new Time();
                     return time.get_estimated_time_arrival(3, user_distance_miles);
                 }else{
                     return null;
                 }

             }
         }


        // otherwise, return null
        return null;

    }




    public void callThreads(Context context, Handler handler, Message message, String dir, String station_type, String map_id, boolean fromAlarm){
        if (new Chicago_Transits().isMyServiceRunning(context,new ExampleService().getClass())) {
            stopService(context);
        }
        API_Caller_Thread api_caller = new API_Caller_Thread(message, context, handler, fromAlarm);
        message.setHandler(handler);
        Thread t1 = new Thread(api_caller);
        message.setDoneNotified(false);
        message.setT1(t1);
        message.setScheduledRedNotified(false);
        message.setScheduledYellowNotified(false);
        message.setScheduledGreenNotified(false);
        message.setApproachingNotified(false);
        message.setGreenNotified(false);
        message.setRedNotified(false);
        message.setYellowNotified(false);
        message.setApi_caller_thread(api_caller);
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '"+map_id+"'", null,null);
        ArrayList<Object> userfav_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_FAVORITES, CTA_DataBase.FAVORITE_MAP_ID + " ='"+map_id+"' AND "+CTA_DataBase.FAVORITE_STATION_TYPE +"= '"+station_type+"'", null,null);
        Station tracking_record = (Station) record.get(0);
        Log.e("TRACKING", "Starting to track: "+tracking_record.getStation_name()+".");
        message.setTARGET_MAP_ID(tracking_record.getMap_id());
        message.setDir(dir);
        message.setTarget_name(tracking_record.getStation_name());
        message.setTarget_type(station_type);
        message.keepSending(true);
        message.setTarget_station(tracking_record);
        message.getT1().start();
        Chicago_Transits chicago_transits = new Chicago_Transits();


        // Loading bar + zoom to target station
        if (MainActivity.bar != null && userfav_record !=null) {
            HashMap<String, String> fav = (HashMap<String, String>) userfav_record.get(0);
            MainActivity.bar.setTitle("Loading...");
            MainActivity.bar.setBackgroundDrawable(new ColorDrawable(chicago_transits.GetBackgroundColor(fav.get(CTA_DataBase.FAVORITE_STATION_TYPE),context)));

            if (MainActivity.mMap!=null) {
                MainActivity.check = 0;
                chicago_transits.ZoomIn(MainActivity.mMap, 12f, tracking_record.getLat(), tracking_record.getLon());
            }
        }
        cta_dataBase.close();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setBarTitle(Context context, String stop_id, String train_line) {
        if (stop_id !=null) {
                CTA_DataBase cta_dataBase = new CTA_DataBase(context);
                ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, "STOP_ID = '" + stop_id + "'", null, null);
                if (record != null) {
                    Station new_station = (Station) record.get(0);
                    if (MainActivity.bar != null) {
                        MainActivity.bar.setTitle(new_station.getStation_name() + " " + getDirectionLabel(new_station.getDirection_id()) + " Bound");
//                        MainActivity.bar.setBackgroundDrawable(new ColorDrawable(GetBackgroundColor(train_line, context)));

                    }
                }

        }else{
            if (MainActivity.bar != null) {
                MainActivity.bar.setTitle("No Trains.");
//                MainActivity.bar.setBackgroundDrawable(new ColorDrawable(GetBackgroundColor(train_line, context)));

            }


            CTA_DataBase cta_dataBase = new CTA_DataBase(context);




            cta_dataBase.close();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean StartNotificationServices(Context context, Message message, ArrayList<Train> current_incoming_trains) {
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> current_tracking_train = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null, null, null);
        cta_dataBase.close();
        // if we are tracking a specific train...
        if (current_tracking_train != null){
            HashMap<String, String> current_notified_train = (HashMap<String, String>) current_tracking_train.get(0);
            Log.e("INCOMING", "Tracking: "+current_notified_train.get("TRAIN_ID") + " | "+ current_notified_train.get("TRAIN_TYPE"));
            Chicago_Transits chicago_transits = new Chicago_Transits();
            Train selected_train = findTrain(current_incoming_trains, current_notified_train);
            if (selected_train != null){
                Log.e("INCOMING", "FOUND: "+current_notified_train.get("TRAIN_ID") + " | "+ current_notified_train.get("TRAIN_TYPE"));
                // if we found the train under current API Call, it is still in scope therefore - send status update
                String notification_message = "Rt# "+selected_train.getRn()+". "+ selected_train.getRt() +" line train is " + selected_train.getTarget_eta() +"m away!";
                message.setDoneNotified(false);
                chicago_transits.CallStatusUpdate(context, selected_train, message, notification_message);
                return true;
            }else{
                // otherwise - the train is no longer in scope and we stop current services
                Log.e("TRACKING_TRAIN", "NOT FOUND: "+current_notified_train.get("TRAIN_ID") + " | "+ current_notified_train.get("TRAIN_TYPE"));
                if (current_notified_train.get("TRAIN_DIR").equals(current_incoming_trains.get(0).getTrDr())){
                    cta_dataBase = new CTA_DataBase(context);
                    cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                    cta_dataBase.close();
             }
                if (chicago_transits.isMyServiceRunning(context, new ExampleService().getClass())) {
//                    chicago_transits.stopService(context);
                    message.setDoneNotified(true);
                    Station target = message.getTarget_station();
                    chicago_transits.CallStatusUpdate(context, selected_train, message, message.getTarget_type()+" line train to "+target.getStation_name() +" is no longer visible.");
                }
                return false;
            }
        }else {
            Log.e("TRACKING_TRAIN", "Tracking: N/A");

        }
        return false;
    }


    private Train findTrain(ArrayList<Train> current_incoming_trains, HashMap<String, String> current_notified_train) {
        Train selected_train = null;
        for (Train incoming_train : current_incoming_trains) {
            if (incoming_train.getRn().equals(current_notified_train.get("TRAIN_ID"))) {
                selected_train = incoming_train;
                Log.e("Notification train", "#" + selected_train.getRn() + " Is being notified |" + selected_train.getRt() + " line.");
                break;
            }
        }
        return selected_train;
    }







    public void create_main_station_table(BufferedReader reader, Context context){
        CTA_DataBase sqlite = new CTA_DataBase(context);
        try{
//            ArrayList<Object> cta_stops_table = sqlite.excecuteQuery("*", "MAIN_STATIONS", null, null);
//            if (cta_stops_table != null){
//                sqlite.close();
//                Log.e(Thread.currentThread().getName(), "RECORD EXSITS");
//
//                return;
//            }
        }catch (Exception e){Log.e(Thread.currentThread().getName(), "No MainStations table, will create!");}
        String line;
        int row=0;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    if (row == 0){
                        row++;
                        continue;
                    }
                    String[] tokens = (line.replaceAll("\"","")
                            .replaceAll(",,",",")).split(",");



                    String station_type = tokens[0];
                    String northbound = tokens[1];
                    String southbound = tokens[2];
                    String express = tokens[3];

                    MainStation mainStation = new MainStation();
                    mainStation.setStationType(station_type);
                    mainStation.setNorthBound(northbound);
                    mainStation.setSouthBound(southbound);
                    mainStation.setExpress(express);
                    sqlite.commit(mainStation, "MAIN_STATIONS");

                    row++;

                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            sqlite.close();

        }
    }

    public ArrayList<String> create_line_stops_table(BufferedReader reader, Context context, String type) {
        CTA_DataBase sqlite = null;
           sqlite = new CTA_DataBase(context);
        String line;
        int row=0;
        ArrayList<String> ordered_station_list = new ArrayList<>();
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    if (row == 0) {
                        row++;
                        continue;
                    }
                    String[] tokens = (line.replaceAll("\"", "")
                            .replaceAll(",,", ",")).split(",");
                    L_stops cta_STOPS = new L_stops();

                    if (type != null){
                    switch (type.toLowerCase()) {
                        case "red":
                            // code block
                            ordered_station_list.add(tokens[2]);
                            break;
                        case "blue":
                            // code block
                            ordered_station_list.add(tokens[3]);
                            break;
                        case "brown":
                            ordered_station_list.add(tokens[7]);
                            break;


                        case "purple":
                            ordered_station_list.add(tokens[8]);
                            break;
                        case "yellow":
                            ordered_station_list.add(tokens[4]);
                            break;

                        case "pink":
                            ordered_station_list.add(tokens[5]);
                            break;

                        case "green":
                            ordered_station_list.add(tokens[1]);
                            break;

                        case "orange":
                            ordered_station_list.add(tokens[6]);
                            break;

                        default:
                            cta_STOPS.setGreen(tokens[1]);
                            cta_STOPS.setRed(tokens[2]);
                            cta_STOPS.setBlue(tokens[3]);
                            cta_STOPS.setYellow(tokens[4]);
                            cta_STOPS.setPink(tokens[5]);
                            cta_STOPS.setOrange(tokens[6]);
                            cta_STOPS.setBrown(tokens[7]);
                            cta_STOPS.setPurple(tokens[8]);

                            sqlite.commit(cta_STOPS, "L_STOPS");
                            row++;
                    }
                }else{
                        cta_STOPS.setGreen(tokens[1]);
                        cta_STOPS.setRed(tokens[2]);
                        cta_STOPS.setBlue(tokens[3]);
                        cta_STOPS.setYellow(tokens[4]);
                        cta_STOPS.setPink(tokens[5]);
                        cta_STOPS.setOrange(tokens[6]);
                        cta_STOPS.setBrown(tokens[7]);
                        cta_STOPS.setPurple(tokens[8]);

                        sqlite.commit(cta_STOPS, "L_STOPS");
                        row++;

                    }


                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
                sqlite.close();


        }
        return ordered_station_list;
    }



    public void Create_TrainInfo_table(BufferedReader reader, Context context) throws IOException {
        CTA_DataBase sqlite = new CTA_DataBase(context);
        String line;
        int row=0;
        while (true) {
            try{
            if ((line = reader.readLine()) != null) {
                if (row == 0){
                        row++;
                        continue;
                    }
            }


            String[] tokens = line.split(",");


//            String[] tokens = line.split(","); //(line.replaceAll("\"","").replaceAll(",,",",")).split(",");
            CTA_Stops cta_stops = new CTA_Stops();
            cta_stops.setSTOP_ID(tokens[0]);
            cta_stops.setDIRECTION_ID(tokens[1]);
            cta_stops.setSTOP_NAME(tokens[2]);




                String station_name = tokens[3];

                if (!station_name.matches("\\A\\p{ASCII}*\\z")){
                    cta_stops.setSTATION_NAME("O'Hare");

                }
            cta_stops.setSTATION_NAME(station_name);
            cta_stops.setMAP_ID(tokens[4]);
            if (Boolean.parseBoolean(tokens[5])){
                cta_stops.setADA("1");

            }else{
                cta_stops.setADA("0");
            }

            if (Boolean.parseBoolean(tokens[6])){
                cta_stops.setRED("1");

            }else{
                cta_stops.setRED("0");
            }
            if (Boolean.parseBoolean(tokens[7])){
                cta_stops.setBLUE("1");
            }else{
                cta_stops.setBLUE("0");
            }
            if (Boolean.parseBoolean(tokens[8])){
                cta_stops.setG("1");

            }else{
                cta_stops.setG("0");


            }
            if (Boolean.parseBoolean(tokens[9])){
                cta_stops.setBRN("1");
            }else{
                cta_stops.setBRN("0");

            }
            if (Boolean.parseBoolean(tokens[10])){
                cta_stops.setP("1");

            }else{
                cta_stops.setP("0");


            }
            if (Boolean.parseBoolean(tokens[11])){
                cta_stops.setPEXP("1");

            }else{
                cta_stops.setPEXP("0");

            }
            if (Boolean.parseBoolean(tokens[12])){
                cta_stops.setY("1");


            }else{
                cta_stops.setY("0");


            }
            if (Boolean.parseBoolean(tokens[13])){
                cta_stops.setPINK("1");

            }else{
                cta_stops.setPINK("0");


            }
            if (Boolean.parseBoolean(tokens[14])){
                cta_stops.setORG("1");

            }else{
                cta_stops.setORG("0");


            }

            String lat = tokens[15].replaceAll("[^0-9.\\-]", "");
            String lon = tokens[16].replaceAll("[^0-9.\\-]", "");


            if (lat.equals("") || lon.equals("") ){
                Log.e("STATION", tokens[15]+" ,"+tokens[16]);
            }

            cta_stops.setLAT(Double.parseDouble(lat));
            cta_stops.setLON(Double.parseDouble(lon));

            sqlite.commit(cta_stops, CTA_DataBase.CTA_STOPS);

            row++;


        }
            catch (IOException e) {
                e.printStackTrace();
            }
            sqlite.close();
            if (row == 300){
                break;
            }

        }





    }
    public static boolean isPureAscii(String v) {
        return Charset.forName("US-ASCII").newEncoder().canEncode(v);
        // or "ISO-8859-1" for ISO Latin 1
        // or StandardCharsets.US_ASCII with JDK1.7+
    }

//    public String[] retrieve_station_coordinates(Database2 sqlite, String station_id) {
//        try {
//            ArrayList<String> record = sqlite.get_table_record("cta_stops", "WHERE station_id = '"+station_id+"'");
//            return new String[]{record.get(10), record.get(11)};
//
//        }catch (Exception e){
//            Log.e("SQLITE ERROR", "COULD NOT FIND STATION IN DATABASE!");
//        }
//        return null;
//    }





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public BufferedReader setup_file_reader(Context context, int file){
        try {
            InputStream CSVfile = context.getResources().openRawResource(file);
            return new BufferedReader(new InputStreamReader(CSVfile, StandardCharsets.UTF_8));

        }catch (Exception e){
            Log.e("ERROR IN FILE", "CAN NOT OPEN FILE");
        }
        return null;

    }





    public Train get_train_info( String each_train) {
        /*
            <eta>
        <staId>41450</staId>
        <stpId>30279</stpId>
        <staNm>Chicago</staNm>
        <stpDe>Service toward Howard</stpDe>
        <rn>905</rn>
        <rt>Red</rt>
        <destSt>30173</destSt>
        <destNm>Howard</destNm>
        <trDr>1</trDr>
        <prdt>20210409 12:23:02</prdt>
        <arrT>20210409 12:25:02</arrT>
        <isApp>0</isApp>
        <isSch>0</isSch>
        <isDly>0</isDly>
        <isFlt>0</isFlt>
        <flags />
        <lat>41.88481</lat>
        <lon>-87.62781</lon>
        <heading>358</heading>
    </eta>
 */
        Train train = new Train();

            String staId = get_xml_tag_value(each_train, "staId");
            String stpId = get_xml_tag_value(each_train, "stpId");
            String staNm = get_xml_tag_value(each_train, "staNm");
            String stpDe = get_xml_tag_value(each_train, "stpDe");
            String rn = get_xml_tag_value(each_train, "rn");
            String rt = get_xml_tag_value(each_train, "rt");
            String destSt = get_xml_tag_value(each_train, "destSt");
            String destNm= get_xml_tag_value(each_train, "destNm");
            String trDr = get_xml_tag_value(each_train, "trDr");
            String prdt= get_xml_tag_value(each_train, "prdt");
            String arrT = get_xml_tag_value(each_train, "arrT");
            String isApp = get_xml_tag_value(each_train, "isApp");
            String isDly= get_xml_tag_value(each_train, "isDly");
            String isFlt = get_xml_tag_value(each_train, "isFlt");
            String isSch = get_xml_tag_value(each_train, "isSch");
            String lat = get_xml_tag_value(each_train, "lat");
            String lon= get_xml_tag_value(each_train, "lon");
            String heading= get_xml_tag_value(each_train, "heading");

            if (isSch.equals("1")){
                train.setIsSch(true);
            }else{
                train.setIsSch(false);
            }

            train.setRn(rn);
            train.setViewIcon(false);
            train.setSelected(false);
            train.setDestNm(destNm);
            train.setTrDr(trDr);
            train.setStaId(staId);
            train.setStpId(stpId);
            train.setStaNm(staNm);
            train.setStpDe(stpDe);
            train.setRt(rt);

            String date1 = getDate(prdt.split(" ")[0]);
            train.setPrdt(date1+" "+prdt.split(" ")[1]);
            String date2 = getDate(arrT.split(" ")[0]);
            train.setArrT(date2 +" " +arrT.split(" ")[1]);
            train.setIsApp(isApp);
            train.setHeading(heading);
            train.setIsDly(isDly);
            train.setDestSt(destSt);
            train.setIsFlt(isFlt);

            if (lat == null && lon == null && heading == null){
                train.setLat(null);
                train.setLon(null);
            }else {
                train.setLat(Double.parseDouble(lat.trim()));
                train.setLon(Double.parseDouble(lon.trim()));
            }
        return train;
    }

    private String getDate(String prdt){
        String year = prdt.substring(0, 4);
        String month = prdt.substring(4, 6);
        String day = prdt.substring(6, 8);
        return  year+ "-"+month+"-"+day;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void plot_marker(Context context, Message message, GoogleMap mMap, Train train, Station target_station){
        MapMarker mapMarker = new MapMarker(mMap, context, message);
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context, message,"Test");
        mMap.setInfoWindowAdapter(adapter);
        if (train!=null) {
            if (train.getSelected() && train.getIsNotified()) {
                Marker marker = mapMarker.addMarker(train, target_station, "Train# " + train.getRn(), 1f, train.getRn());
                if (marker!=null){
                    marker.showInfoWindow();
                }
            }else if (train.getSelected()){
                Marker marker = mapMarker.addMarker(train, target_station, "Train# " + train.getRn(), 1f, train.getRn());
                if (marker!=null){
                marker.showInfoWindow();
                }
            }else{
                mapMarker.addMarker(train, target_station, "Train# " + train.getRn(), 1f, train.getRn());

            }
            }else if (target_station != null){
            mapMarker.addMarker(train, target_station,   "Station# "+target_station.getMap_id(), 1f, target_station.getStation_name());

        }
    }


    public TrainStops get_remaining_train_stop_info( String each_train) {
        /*
            <eta>
        <staId>41450</staId>
        <stpId>30279</stpId>
        <staNm>Chicago</staNm>
        <stpDe>Service toward Howard</stpDe>
        <rn>905</rn>
        <rt>Red</rt>
        <destSt>30173</destSt>
        <destNm>Howard</destNm>
        <trDr>1</trDr>
        <prdt>20210409 12:23:02</prdt>
        <arrT>20210409 12:25:02</arrT>
        <isApp>0</isApp>
        <isSch>0</isSch>
        <isDly>0</isDly>
        <isFlt>0</isFlt>
        <flags />
        <lat>41.88481</lat>
        <lon>-87.62781</lon>
        <heading>358</heading>
    </eta>
 */
        TrainStops train = new TrainStops();
        try {
            String staId = get_xml_tag_value(each_train, "staId");
            String stpId = get_xml_tag_value(each_train, "stpId");
            String staNm = get_xml_tag_value(each_train, "staNm");
            String stpDe = get_xml_tag_value(each_train, "stpDe");
            String rn = get_xml_tag_value(each_train, "rn");
            String rt = get_xml_tag_value(each_train, "rt");
            String destSt = get_xml_tag_value(each_train, "destSt");
            String destNm = get_xml_tag_value(each_train, "destNm");
            String trDr = get_xml_tag_value(each_train, "trDr");
            String prdt = get_xml_tag_value(each_train, "prdt");
            String arrT = get_xml_tag_value(each_train, "arrT");
            String isApp = get_xml_tag_value(each_train, "isApp");
            String isDly = get_xml_tag_value(each_train, "isDly");
            String isFlt = get_xml_tag_value(each_train, "isFlt");


            train.setRn(rn);
            train.setViewIcon(false);
            train.setSelected(false);
            train.setDestNm(destNm);
            train.setTrDr(trDr);
            train.setStaId(staId);
            train.setStpId(stpId);
            train.setStaNm(staNm);
            train.setStpDe(stpDe);
            train.setRt(rt);


            String date1 = getDate(prdt.split(" ")[0]);
            train.setPrdt(date1 + " " + prdt.split(" ")[1]);
            String date2 = getDate(arrT.split(" ")[0]);
            train.setArrT(date2 + " " + arrT.split(" ")[1]);

            train.setIsApp(isApp);
            train.setIsDly(isDly);
            train.setDestSt(destSt);
            train.setIsFlt(isFlt);
        }catch (Exception e){
            return null;
        }


        return train;
    }
    public int getSpinnerPosition(String train_line) {
        HashMap<String, Integer> positions = new HashMap<>();
        positions.put("blue", 0);
        positions.put("red", 1);
        positions.put("green", 2);
        positions.put("yellow", 3);
        positions.put("pink", 4);
        positions.put("orange", 5);
        positions.put("purple", 6);
        positions.put("brown", 7);
        Integer pos = positions.get(train_line.toLowerCase().trim());
        if (pos!= null) {
            return pos;
        }else{
            pos = positions.get(train_line_code_to_regular(train_line));
            return pos;
        }
    }

    private String get_xml_tag_value(String raw_xml, String startTag){
        String end_tag = "</"+startTag+">";
        String item = null;
        try{
           item = (StringUtils.substringBetween(raw_xml,"<"+startTag+">", end_tag) !=null ? StringUtils.substringBetween(raw_xml,"<"+startTag+">", end_tag).trim() : null);
        }catch (Exception e){
            e.printStackTrace();
//            Log.e("API CALLER", "Could not find end tag of "+ startTag);
        }
        return item;

    }


    public Double calculate_coordinate_distance(double lat1, double lon1, double lat2, double lon2){
        final int R = 6371; // Radious of the earth


        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);


        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c * 0.621371; // KM to Miles

    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }


    public Integer getDayOfWeekNum(String dayOfWeek){
        if (dayOfWeek.trim().equals("")){
            return null;
        }
        HashMap<String, Integer> week = new HashMap<>();
        week.put("mon", Calendar.MONDAY);
        week.put("tue", Calendar.TUESDAY);
        week.put("wen", Calendar.WEDNESDAY);
        week.put("thu", Calendar.THURSDAY);
        week.put("fri", Calendar.FRIDAY);
        week.put("sat", Calendar.SATURDAY);
        week.put("sun", Calendar.SUNDAY);
        return week.get(dayOfWeek.toLowerCase().trim());



    }

    public String train_line_code_to_regular(String station_type) {
        HashMap<String, String> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red", "Red");
        TrainLineKeyCodes.put("blue", "Blue");
        TrainLineKeyCodes.put("brn", "Brown");
        TrainLineKeyCodes.put("g", "Green");
        TrainLineKeyCodes.put("org", "Orange");
        TrainLineKeyCodes.put("pink", "Pink");
        TrainLineKeyCodes.put("p", "Purple");
        TrainLineKeyCodes.put("y", "Yellow");
        String code = TrainLineKeyCodes.get(station_type.toLowerCase().trim());
        if (code!=null){
            return code.toLowerCase().trim();
        }else{
            return null;
        }
    }

    public String TrainLineKeys(String station_type){
        HashMap<String, String> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red", "red");
        TrainLineKeyCodes.put("blue", "blue");
        TrainLineKeyCodes.put("brown", "brn");
        TrainLineKeyCodes.put("green", "g");
        TrainLineKeyCodes.put("orange", "org");
        TrainLineKeyCodes.put("pink", "pink");
        TrainLineKeyCodes.put("purple", "p");
        TrainLineKeyCodes.put("yellow", "y");
        return TrainLineKeyCodes.get(station_type.toLowerCase().trim());



    }


    public Integer getStatusColor(String color){
        if (color !=null){
            HashMap<String, Integer> TrainLineKeyCodes = new HashMap<>();
            TrainLineKeyCodes.put("red", R.drawable.red_color);
            TrainLineKeyCodes.put("green", R.drawable.green_color);
            TrainLineKeyCodes.put("yellow", R.drawable.yellow_color);

        return TrainLineKeyCodes.get(color.toLowerCase());
        }
        return R.drawable.none_image;
    }


    public Integer GetBackgroundColor(String color, Context context) {
          if (color!=null) {
              if (color.toLowerCase().equals("p")) {
                  color = "purple";

              } else if (color.toLowerCase().equals("org")) {
                  color = "orange";

              } else if (color.toLowerCase().equals("y")) {
                  color = "yellow";

              } else if (color.toLowerCase().equals("g")) {
                  color = "green";
              } else if (color.toLowerCase().equals("brn")) {
                  color = "brown";
              }
          }
        HashMap<String, Integer> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red", context.getResources().getColor(R.color.red_100));
        TrainLineKeyCodes.put("blue", context.getResources().getColor(R.color.blue_100));
        TrainLineKeyCodes.put("brown", context.getResources().getColor(R.color.brown));
        TrainLineKeyCodes.put("green", context.getResources().getColor(R.color.green_100));
        TrainLineKeyCodes.put("orange", context.getResources().getColor(R.color.orange));
        TrainLineKeyCodes.put("pink", context.getResources().getColor(R.color.pink));
        TrainLineKeyCodes.put("purple",context.getResources().getColor(R.color.purple));
        TrainLineKeyCodes.put("yellow", context.getResources().getColor(R.color.yellow));

        return TrainLineKeyCodes.get(color.toLowerCase());
    }


        public void ZoomIn(GoogleMap mMap, Float zoomLevel, Double lat, Double lon){
        LatLng target = new LatLng(lat, lon);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(zoomLevel)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(90)                  // Sets the tilt of the camera to 40 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public ArrayList<Integer> calculate_station_range_eta(HashMap<String, String> current_train_info, int start, int end,int dir, Context context){
//        Time time = new Time();
//        int starting_idx =0;
//        ArrayList<Integer> train_stop_etas = new ArrayList<>();
//        BufferedReader train_station_stops_reader = setup_file_reader(context, R.raw.train_line_stops);
//        ArrayList<String> all_stops = retrieve_line_stations(train_station_stops_reader, current_train_info.get("station_type"), false);
//        Log.e("stops", all_stops+"");
//        List<String> all_stops_till_target = all_stops.subList(start, end);
//        Log.e("all stops", all_stops_till_target+"");
//
//        if (dir==1){
//            starting_idx = all_stops_till_target.size() -1;
//        }
//        for (int i=0; i < all_stops_till_target.size(); i++){
//            BufferedReader train_station_coordinates_reader = setup_file_reader(context, R.raw.train_stations);
//            String remaining_stop = all_stops_till_target.get(starting_idx);
//            String[] remaining_station_coordinates = retrieve_station_coordinates(train_station_coordinates_reader, remaining_stop, current_train_info.get("station_type"));
//            String[] current_train_loc = (current_train_info.get("train_lat") + ","+current_train_info.get("train_lon")).split(",");
//            double train_distance_to_next_stop = calculate_coordinate_distance(
//                    Double.parseDouble(current_train_loc[0]),
//                    Double.parseDouble(current_train_loc[1]),
//                    Double.parseDouble(remaining_station_coordinates[0]),
//                    Double.parseDouble(remaining_station_coordinates[1]));
//
//            int next_stop_eta = time.get_estimated_time_arrival(25, train_distance_to_next_stop);
//            if (dir == 1){
//                starting_idx --;
//            }
//            else{
//                starting_idx++;
//            }
//
//            train_stop_etas.add(next_stop_eta);
//        }
//    return train_stop_etas;
//    }

    public HashMap<String, Integer> train_speed_mapping(){
        HashMap<String, Integer> train_speeds = new HashMap<>();
        train_speeds.put("green", 25);
        train_speeds.put("red", 25);
        train_speeds.put("blue", 25);
        train_speeds.put("orange", 25);
        train_speeds.put("pink", 25);
        train_speeds.put("purple", 55);
        train_speeds.put("yellow", 25);
        train_speeds.put("brown", 25);

        return train_speeds;
    }


    private String[] callApi(String url) throws IOException {
        final Document TRAIN_RESPONSE = Jsoup.connect(url).get(); // JSOUP to webscrape XML
        return TRAIN_RESPONSE.select("eta").outerHtml().split("</eta>");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Train> call_cta_rest(Message msg)  {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ArrayList<Train> all_incoming_trains = null;
        String train_rn;
        SimpleDateFormat dateFormat;

        try {
            String trains_heading_to_station_url = "https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&mapid="+msg.getTARGET_MAP_ID();
            final String[] train_list = callApi(trains_heading_to_station_url);
            all_incoming_trains = new ArrayList<>();
            if (train_list!=null) {
                for (String raw_train : train_list) {
                    Train train = chicago_transits.get_train_info(raw_train);
                    if (train != null) {
                        train.setTarget_id(msg.getTARGET_MAP_ID());
                        if (train.getTrDr().equals(msg.getDir())) { // Filter list based on target direction
                            //get train eta to target
                            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date parsedArrivalTime = dateFormat.parse(train.getArrT());
                            Date parsePredictedTime = dateFormat.parse(train.getPrdt());
                            long diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                            if (diff < 0){
                                diff = parsePredictedTime.getTime() - parsedArrivalTime.getTime();
                            }
                            long eta_in_minutes = diff / (60 * 1000) % 60;
                            train.setTarget_eta((int) eta_in_minutes);
                            train_rn = train.getRn();
                            String[] remaining_stops = callApi("https://lapi.transitchicago.com/api/1.0/ttfollow.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&runnumber=" + train_rn);
                            ArrayList<TrainStops> remaining_stations = new ArrayList<>();
                            for (String raw_stop : remaining_stops) {
                                TrainStops remaining_trainStop = chicago_transits.get_remaining_train_stop_info(raw_stop);
                                parsedArrivalTime = dateFormat.parse(remaining_trainStop.getArrT());
                                parsePredictedTime = dateFormat.parse(remaining_trainStop.getPrdt());
                                diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                                eta_in_minutes = diff / (60 * 1000) % 60;
                                remaining_trainStop.setNextStopEtA((int) eta_in_minutes);
                                remaining_stations.add(remaining_trainStop);
                            }
                            train.setSelected(false);
                            train.setNotified(false);
                            train.setRemaining_stops(remaining_stations);
                            all_incoming_trains.add(train);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return all_incoming_trains;
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void activateTrain(Context context, Message message,  GoogleMap mMap, Train train) {
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        if (!train.getIsNotified()){ // if this train is not currently being notified - notify it!
//            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
//            // Setting selected train for notifications
//            train.setSelected(true);
//            train.setNotified(true);
//
//            chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
//
//
//            cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER); // resets all train tracking
//            cta_dataBase.commit(train, CTA_DataBase.TRAIN_TRACKER); // Commiting a new train to track
//            cta_dataBase.close();
//
//
//        }else{
//            CTA_DataBase cta_dataBase = new CTA_DataBase(context);
//            // if we reselect our train tracking train then turn it off!
//            train.setNotified(false);
//            train.setSelected(false);
//            chicago_transits.plot_all_markers(context, message, mMap, message.getOld_trains());
//
//            cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
//
//            //TODO: Add condition to check if there is a service running before stopping a service
//            chicago_transits.stopService(context);
//            cta_dataBase.close();
//
//        }






//    }
}
