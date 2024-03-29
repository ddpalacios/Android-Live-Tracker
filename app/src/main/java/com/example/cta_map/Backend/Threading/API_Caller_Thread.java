package com.example.cta_map.Backend.Threading;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.Activities.Settings_view_Fragment;
import com.example.cta_map.Activities.UserSettings_Form;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Time;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.Displayers.TrainStops;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class API_Caller_Thread implements Runnable {
    public static Message msg = null;
    String TAG = "API CALLER";
    Context context;
    Handler handler;
    private boolean fromAlarm;
    public static Boolean AlarmTriggered = false;
    private volatile boolean cancelled = false;

    public API_Caller_Thread(Message msg, Context context, Handler handler) {
        API_Caller_Thread.msg = msg;
        this.context = context;
        this.handler = handler;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        Log.e("API CALLER", "NEW THREADS ARE STARTING");
        synchronized (msg) {
            CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
            while (msg.IsSending()) {
                if (cancelled) {
                    break;
                }
                ArrayList<Object> tracking_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null, null, null);

                ArrayList<Train> new_incoming_trains = null;
                try {
                    new_incoming_trains = call_cta_rest();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                set_up_new_incoming_trains_with_notification_train(tracking_record, new_incoming_trains); // sets up notification trains
                Train default_nearest_train = set_up_default_nearest_train(new_incoming_trains); // sets default nearest train for viewing on screen

                if (msg.getMadeBroadcastSwitch()!= null && msg.getMadeBroadcastSwitch()){ // if a switch was triggered from notification (switch directions)
                    Log.e("Service", "MADE SWITCH");
                    cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                    cta_dataBase.commit(default_nearest_train, CTA_DataBase.TRAIN_TRACKER);
                    msg.setMadeBroadcastSwitch(false);
                    msg.getT1().interrupt();
                }

                if (msg.getAlarmTriggered() != null && msg.getAlarmTriggered()){
                    cta_dataBase.delete_all_records(CTA_DataBase.TRAIN_TRACKER);
                    // we need to set the nearest train if we are calling from an alarm
                    if (default_nearest_train == null){
                        Chicago_Transits chicago_transits = new Chicago_Transits();
                        chicago_transits.StartNotificationServices(context, this.msg ,new_incoming_trains);
                        msg.setAlarmTriggered(false);
                        msg.getT1().interrupt();
                    }else {
                        Log.e("API", "ALARM TRIGGERED FROM API - Tracking: " + default_nearest_train.getStaNm() + " " + default_nearest_train.getRt() + " line.");
                        cta_dataBase.commit(default_nearest_train, CTA_DataBase.TRAIN_TRACKER);
                        msg.setAlarmTriggered(false);
                        msg.getT1().interrupt();
                    }
                }

                msg.setIncoming_trains(new_incoming_trains);
                setStatus(new_incoming_trains, msg); // Set train status
                send_to_UI("new_incoming_trains", new_incoming_trains);
                try {
                    Log.e(TAG, "THREAD IS WAITING");
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (cancelled) {
                        Log.e(TAG, "THREAD IS STOPPING...");
                        break;

                    }
                }
            }
            cta_dataBase.close();
        }
        Log.e(TAG, "Thread is Killed.");
    }

    private Train set_up_default_nearest_train(ArrayList<Train> new_incoming_trains) {
        if (new_incoming_trains.size() > 0) {
            msg.setFinalDest(new_incoming_trains.get(0).getDestNm());
            msg.setNearestTrain(new_incoming_trains.get(0));
            return new_incoming_trains.get(0);
        } else {
            msg.setNearestTrain(null);
            return null;
        }
    }

    private void set_up_new_incoming_trains_with_notification_train(ArrayList<Object> tracking_record, ArrayList<Train> new_incoming_trains) {
        if (tracking_record != null && new_incoming_trains != null) { // setting up new incoming trains
            HashMap<String, String> current_tracking_train = (HashMap<String, String>) tracking_record.get(0);
            for (Train train : new_incoming_trains) {
                if (train.getRn().equals(current_tracking_train.get("TRAIN_ID"))) {
                    train.setNotified(true);
                    train.setSelected(true);
                    msg.setOld_trains(new_incoming_trains);
                    break;
                }
            }
        }else{
            msg.setOld_trains(new_incoming_trains);
        }
    }

    private ArrayList<Train> proceess_new_trains(ArrayList<Train> new_incoming_trains) {
        if (new_incoming_trains.size() == 0) {
            send_to_UI("new_incoming_trains", new_incoming_trains);
            return new_incoming_trains;
        }

        if (msg.getOld_trains() != null && new_incoming_trains != null) {
            for (Train train : msg.getOld_trains()) {
                if (train.getSelected() && train.getIsNotified()) { // Setting new incoming trains for notification train
                    for (Train train1 : new_incoming_trains) {
                        if (train.getRn().equals(train1.getRn())) {
                            train1.setSelected(true);
                            train1.setNotified(true);
                            break;
                        }
                    }
                }
                if (train.getSelected()) {
                    for (Train train1 : new_incoming_trains) {  // Setting new incoming trains for selection trains
                        if (train.getRn().equals(train1.getRn())) {
                            train1.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
        return new_incoming_trains;
    }

    public void cancel() {
        cancelled = true;
    }

    private String[] callApi(String url) throws IOException {
        final Document TRAIN_RESPONSE = Jsoup.connect(url).get(); // JSOUP to webscrape XML
        return TRAIN_RESPONSE.select("eta").outerHtml().split("</eta>");
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Train> call_cta_rest() throws ParseException {
        Chicago_Transits chicago_transits = new Chicago_Transits();
        ArrayList<Train> all_incoming_trains = null;
        String train_rn;
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> tracking_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.TRAIN_TRACKER, null, null, null);
        SimpleDateFormat dateFormat;
        String[] train_list = null;
        ArrayList<String> remaining_stops_url_list = new ArrayList<>();
        String train_eta_url = null;

        try {
            train_eta_url = "https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&mapid=" + msg.getTARGET_MAP_ID();
            String trains_heading_to_station_url = "https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&mapid=" + msg.getTARGET_MAP_ID();
            train_list = callApi(trains_heading_to_station_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        all_incoming_trains = new ArrayList<>();
        if (train_list == null || train_list.length == 0 ) {
            msg.setStop_id(null);
        }

        if (train_list != null) {
            for (String raw_train : train_list) {
                if (raw_train.trim().equals("")){
                    continue;
                }
                Train train = chicago_transits.get_train_info(raw_train);
                if (train != null && train.getRn()!=null) {
                    train.setTarget_id(msg.getTARGET_MAP_ID());
                    if (train.getTrDr().equals(msg.getDir())) { // Filter list based on target direction
                        //get train eta to target
                        msg.setStop_id(train.getStpId());
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        if (train.getArrT()!=null && train.getPrdt()!=null) {
                            Date parsedArrivalTime = dateFormat.parse(train.getArrT());
                            Date parsePredictedTime = dateFormat.parse(train.getPrdt());
                            long diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                            if (diff < 0) {
                                diff = parsePredictedTime.getTime() - parsedArrivalTime.getTime();
                            }
                            long eta_in_minutes = diff / (60 * 1000) % 60;
                            train.setTarget_eta((int) eta_in_minutes);

                            if (tracking_record!=null){
                                HashMap<String, String> notification_train = (HashMap<String, String>) tracking_record.get(0);
                                if (notification_train.get(CTA_DataBase.TRAIN_ID).equals(train.getRn())){
                                    // Updating train eta of current notification train
                                    cta_dataBase.update(CTA_DataBase.TRAIN_TRACKER, CTA_DataBase.TRAIN_ETA,
                                            train.getTarget_eta()+"",
                                            CTA_DataBase.TRAIN_ID +" = '"+train.getRn()+"'");
                                }
                            }



                        }

                        String[] remaining_stops = null;
                        try {
                            train_rn = train.getRn();
                            String remaining_stations_url = "https://lapi.transitchicago.com/api/1.0/ttfollow.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&runnumber=" + train_rn;
                            remaining_stops_url_list.add(remaining_stations_url);
                            remaining_stops = callApi(remaining_stations_url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ArrayList<TrainStops> remaining_stations = new ArrayList<>();
                        if (remaining_stops != null) {
                            for (String raw_stop : remaining_stops) {
                                TrainStops remaining_trainStop = chicago_transits.get_remaining_train_stop_info(raw_stop);
                                if (remaining_trainStop != null) {
                                    Date parsedArrivalTime = dateFormat.parse(remaining_trainStop.getArrT());
                                    Date parsePredictedTime = dateFormat.parse(remaining_trainStop.getPrdt());
                                    long diff = parsedArrivalTime.getTime() - parsePredictedTime.getTime();
                                    long eta_in_minutes = diff / (60 * 1000) % 60;
                                    remaining_trainStop.setNextStopEtA((int) eta_in_minutes);
                                    remaining_stations.add(remaining_trainStop);
                                }
                            }
                        }
                        getUserInformation(train);
                        train.setSelected(false);
                        train.setNotified(false);
                        train.setRemaining_stops(remaining_stations);
                        cta_dataBase.close();
                        all_incoming_trains.add(train);
                    }
                }
            }
        }
        Log.e("API CALLER", "\nMain URL: \n- . " + train_eta_url + "\n");
        for (String url : remaining_stops_url_list) {
            Log.e("API CALLER", "\n- " + url + "\n");

        }
        return proceess_new_trains(all_incoming_trains);
    }

    private void getUserInformation(Train train) {
        Chicago_Transits chicago_transits=  new Chicago_Transits();
        CTA_DataBase cta_dataBase= new CTA_DataBase(context);
        ArrayList<Object> user_location_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION + " = '1'", null, null);
        ArrayList<Object> station_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, CTA_DataBase.TRAIN_MAP_ID + " = '"+msg.getTARGET_MAP_ID()+"'", null,null);
        if (user_location_record!=null) {
            HashMap<String, String> user_location = (HashMap<String, String>) user_location_record.get(0);
            if (!user_location.get(CTA_DataBase.USER_LAT).isEmpty() || !user_location.get(CTA_DataBase.USER_LON).isEmpty()) {
                train.setSharingLoc(true);
                Station target_station = (Station) station_record.get(0);
                Double user_lat = Double.parseDouble(user_location.get(CTA_DataBase.USER_LAT));
                Double user_lon = Double.parseDouble(user_location.get(CTA_DataBase.USER_LON));
                Double target_lat = target_station.getLat();
                Double target_lon = target_station.getLon();
                Double user_to_target_distance = chicago_transits.calculate_coordinate_distance(user_lat, user_lon, target_lat, target_lon);
                Time time = new Time();
                int user_eta = time.get_estimated_time_arrival(3, user_to_target_distance);
                train.setUser_lat(user_lat);
                train.setUser_lon(user_lon);
                train.setUser_to_target_distance(user_to_target_distance);
                train.setUser_to_target_eta(user_eta);
            }else{
                train.setSharingLoc(false);
            }
        }else{
            train.setSharingLoc(false);
        }

        cta_dataBase.close();
    }

    public void send_to_UI(String key, ArrayList<Train> trainArrayList) {

        Bundle bundle = new Bundle();
        android.os.Message handler_msg = msg.getHandler().obtainMessage();
        bundle.putSerializable(key, trainArrayList);
        handler_msg.setData(bundle);
        msg.getHandler().sendMessage(handler_msg);
        Log.e("API CALLER", "Sent to UI");
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setStatus(ArrayList<Train> incoming_trains, Message message) {
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
        ArrayList<Object> user_location_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_LOCATION, CTA_DataBase.HAS_LOCATION + "= '1'", null, null);
        Chicago_Transits chicago_transits = new Chicago_Transits();


        if (incoming_trains!=null && incoming_trains.size() > 0) {
            ArrayList<Object> cta_record = cta_dataBase.excecuteQuery("*", "CTA_STOPS", "MAP_ID = '" + incoming_trains.get(0).getTarget_id() + "'", null, null);
            Station target_station_record = (Station) cta_record.get(0);
            Integer user_eta_from_station = chicago_transits.getUserEstimatedTimeArrivalToStation(user_location_record, target_station_record);
            message.setTarget_name(target_station_record.getStation_name());
            for (Train main_train : incoming_trains) {
                main_train.setTarget_station_name(target_station_record.getStation_name());
                String specific_tracking_type = getSpecificTrackingType();
                if (specific_tracking_type != null && !main_train.getIsSch()) {

                    ArrayList<TrainStops> remaining_stations_till_target = get_RemainingStations_till_target(main_train, target_station_record);
                    Integer threshold = (specific_tracking_type.equals(UserSettings_Form.STATIONS_ITEM) ? remaining_stations_till_target.size() : main_train.getTarget_eta());
                    if (specific_tracking_type.equals(Settings_view_Fragment.STATIONS_ITEM)){
                        Log.e("Threshold", "Status as STATIONS");
                    }else {
                        Log.e("Threshold", "Status as MINUTES");

                    }
                    SetTrueStatusBasedOn(main_train, threshold, user_eta_from_station);

                }
            }
        }
        cta_dataBase.close();
    }

    private void SetTrueStatusBasedOn( Train main_train, Integer selected_threshold,Integer user_eta_from_station){
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        ArrayList<Object> settings_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null, null, null);
        cta_dataBase.close();
        UserSettings userSettings = (UserSettings) settings_record.get(0);
        Integer default_green = Integer.parseInt(userSettings.getGreen_limit());
        Integer default_yellow = Integer.parseInt(userSettings.getYellow_limit());
        Integer main_train_eta = main_train.getTarget_eta();
        if (selected_threshold >= default_green) {
            // status green
            if (user_eta_from_station != null){
                if (main_train_eta >= user_eta_from_station ){
                    main_train.setStatus("GREEN");
                }else if (main_train_eta < user_eta_from_station){
                    main_train.setStatus("RED");
                }
            }else{
                main_train.setStatus("GREEN");
            }


        }else if (selected_threshold >= default_yellow && selected_threshold < default_green){
            // status yellow
            if (user_eta_from_station != null){
                if (main_train_eta >= user_eta_from_station ){
                    main_train.setStatus("YELLOW");
                }else if (main_train_eta < user_eta_from_station){
                    main_train.setStatus("RED");
                }
            }else{
                main_train.setStatus("YELLOW");
            }


        }else if (selected_threshold < default_yellow){
            // status red
            if (user_eta_from_station != null){
                if (main_train_eta >= user_eta_from_station ){
                    main_train.setStatus("RED");
                }else if (main_train_eta < user_eta_from_station){
                    main_train.setStatus("RED");
                }
            }else{
                main_train.setStatus("RED");
            }

        }else{
            if (main_train.getStatus() == null){
                Log.e("API", "TRAIN WITH NULL STATUS");
                String train_status = main_train.getStatus();
                main_train.setStatus("GREEN");

            }
            main_train.setStatus("GREEN");

        }

        if (main_train.getStatus() == null){
            main_train.setStatus("GREEN");
            Log.e("API", "TRAIN WITH NULL STATUS");

        }


    }

    private String getSpecificTrackingType() {
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        String tracking_type= null;
        ArrayList<Object> settings_record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null, null, null);
        if (settings_record != null) {
            UserSettings userSettings = (UserSettings) settings_record.get(0);
            if (userSettings!= null && userSettings.getAsMinutes()!= null && userSettings.getAsMinutes().equals("1")) {
                tracking_type = Settings_view_Fragment.MINUTES_ITEM;
            } else {
                tracking_type =Settings_view_Fragment.STATIONS_ITEM;

            }
        }
        cta_dataBase.close();
        return tracking_type;
    }

    private ArrayList<TrainStops> get_RemainingStations_till_target(Train main_train, Station target_station_record) {
        ArrayList<TrainStops> new_list = new ArrayList<>();
        for (TrainStops remainining_train_stop : main_train.getRemaining_stops()) {
            if (remainining_train_stop.getStaId().equals(target_station_record.getMap_id())) {
                new_list.add(remainining_train_stop);
                break;
            } else {
                new_list.add(remainining_train_stop);
            }
        }
        return new_list;
    }
}