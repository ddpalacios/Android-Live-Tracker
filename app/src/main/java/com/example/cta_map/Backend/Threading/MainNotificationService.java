package com.example.cta_map.Backend.Threading;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;

import java.util.HashMap;

import static com.example.cta_map.Activities.App.CHANNEL_ID;

public class MainNotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder notificationBuilder = buildNotification(intent);
        startForeground(1, notificationBuilder.build());
        return START_NOT_STICKY;
    }

    private NotificationCompat.Builder buildNotification(Intent intent) {
        Intent dismissIntent = new Intent(this, StopServices.class);  // Dismiss button
        Intent switchIntent = new Intent(this, SwitchDirection_Services.class);  // Switch Direction Button
        Intent OnClick_notificationIntent = new Intent(this, MainActivity.class); // on notification click
        Intent TrackNextIntent = new Intent(this, TrackNextServices.class); // Track next button
        PendingIntent dismiss_pendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, 0);
        PendingIntent switch_pendingIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);
        PendingIntent onClick_notification_pendingIntent = PendingIntent.getActivity(this, 0, OnClick_notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent tracknext_pendingIntent;

        Train CURRENT_NOTIFICATION_TRAIN = (Train) intent.getSerializableExtra("train");
        String notification_message = intent.getStringExtra("notification_message");
        String subtitle = intent.getStringExtra("subtitle");
        boolean willUpdate = intent.getBooleanExtra("willUpdate", false);
        boolean is_current_notification_train_still_in_scope = intent.getBooleanExtra("isDone", false);
        int default_image = R.drawable.red;
        Message message = API_Caller_Thread.msg;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("CTA Commuting")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notification_message)
                .setContentText(subtitle)
                .setContentInfo(subtitle)
                .setContentIntent(onClick_notification_pendingIntent) // On tap response
                .addAction(default_image, "Dismiss", dismiss_pendingIntent); // Always visible
        notificationBuilder.setSmallIcon(default_image);
        notificationBuilder.setColor(getResources().getColor(getColor("green"))); // notification image color
        if (willUpdate){
            notificationBuilder.setOnlyAlertOnce(true);
        }

        if (!is_current_notification_train_still_in_scope) {
            tracknext_pendingIntent = PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);
            message.setIs_retrieving_first_nearest_train(false);

            // train is NOT null, it's still in scope
            TrackNextIntent.putExtra("train", CURRENT_NOTIFICATION_TRAIN);
            TrackNextIntent.putExtra("test", "test");
            message.setCurrentNotificationTrain(CURRENT_NOTIFICATION_TRAIN);
            notificationBuilder.addAction(default_image, "Track Next Train", tracknext_pendingIntent);
            notificationBuilder.addAction(default_image, "Switch Directions", switch_pendingIntent); // ADDING ACTION BUTTONS FOR NOTIFICATIONS
            notificationBuilder.setSmallIcon(default_image);
            String train_status = (CURRENT_NOTIFICATION_TRAIN!= null && CURRENT_NOTIFICATION_TRAIN.getStatus() != null ? CURRENT_NOTIFICATION_TRAIN.getStatus() : "red");
            notificationBuilder.setSmallIcon(default_image);
            notificationBuilder.setColor(getResources().getColor(getColor(train_status.toLowerCase().trim()))); // notification image color


        } else {
            // Train is NULL (Not in scope)
            message.setIs_retrieving_first_nearest_train(true);
            TrackNextIntent.putExtra("isNew", is_current_notification_train_still_in_scope); // If not in scope - give option to track the nearest train
            tracknext_pendingIntent = PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);
            notificationBuilder.addAction(default_image, "Track next nearest train", tracknext_pendingIntent);


        }
        return notificationBuilder;
    }


    private int getColor(String color){
        HashMap<String, Integer> colors = new HashMap<>();
        colors.put("red", R.color.red_100);
        colors.put("yellow", R.color.yellow);
        colors.put("green", R.color.green_100);

        return colors.get(color);

    }


//
//
//        if (isDone){
//            Message message = API_Caller_Thread.msg;
//            TrackNextIntent.putExtra("direction", API_Caller_Thread.msg.getDir());
//            TrackNextIntent.putExtra("map_id", API_Caller_Thread.msg.getTarget_station().getMap_id());
//            TrackNextIntent.putExtra("station_type", API_Caller_Thread.msg.getTarget_type());
//            PendingIntent tracknext_pendingIntent =
//                    PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);
//            notificationBuilder.addAction(R.drawable.red, "Track Nearest Train" ,tracknext_pendingIntent);
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                notificationBuilder.setSmallIcon(R.drawable.red);
//                notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//            } else {
//                notificationBuilder.setSmallIcon(R.drawable.red);
//                notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//            }
//
//
//        }else {
//            // if currently tracking train - Allow to switch direction + track next train
//            if (train != null) {
//                TrackNextIntent.putExtra("direction", train.getTrDr());
//                TrackNextIntent.putExtra("map_id", train.getTarget_id());
//                TrackNextIntent.putExtra("station_type", train.getRt());
//                PendingIntent tracknext_pendingIntent = PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);
//                notificationBuilder.addAction(R.drawable.red, "Track Next Train", tracknext_pendingIntent);
//                notificationBuilder.addAction(R.drawable.red, "Switch Directions", switch_pendingIntent);
//                String train_status = train.getStatus();
//                HashMap<String, Integer> colors = new HashMap<>();
//                colors.put("red", R.color.red_100);
//                colors.put("yellow", R.color.yellow);
//                colors.put("green", R.color.green_100);
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    notificationBuilder.setSmallIcon(R.drawable.red);
//                    if (train_status == null) {
//                        train_status = "red";
//                    }
//                    notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
//                } else {
//                    if (train_status == null) {
//                        train_status = "red";
//                    }
//                    notificationBuilder.setSmallIcon(R.drawable.red);
//                    notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
//
//                }
//            }else{
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    notificationBuilder.setSmallIcon(R.drawable.red);
//                    notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//                } else {
//                    notificationBuilder.setSmallIcon(R.drawable.red);
//                    notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//                }
//
//            }


















//            }else{
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    notificationBuilder.setSmallIcon(R.drawable.red);
//                    notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//                } else {
//                    notificationBuilder.setSmallIcon(R.drawable.red);
//                    notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//
//                }
//            }
//
//            PendingIntent tracknext_pendingIntent =
//                    PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);
//            notificationBuilder.addAction(R.drawable.red, "Track Next Train", tracknext_pendingIntent);
//
//            notificationBuilder.addAction(R.drawable.red, "Switch Directions", switch_pendingIntent);
//
//
//            String train_status = train.getStatus();
//            HashMap<String, Integer> colors = new HashMap<>();
//            colors.put("red", R.color.red_100);
//            colors.put("yellow", R.color.yellow);
//            colors.put("green", R.color.green_100);
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                notificationBuilder.setSmallIcon(R.drawable.red);
//                if (train_status == null) {
//                    train_status = "red";
//                }
//                notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
//            } else {
//                if (train_status == null) {
//                    train_status = "red";
//                }
//                notificationBuilder.setSmallIcon(R.drawable.red);
//                notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
//
//            }
//        }
//
//        if (train == null) {
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                notificationBuilder.setSmallIcon(R.drawable.red);
//                notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//            } else {
//                notificationBuilder.setSmallIcon(R.drawable.red);
//                notificationBuilder.setColor(getResources().getColor(R.color.green_100));
//
//            }
//        }
//        String map_id = intent.getStringExtra("map_id");
//        Train train = (Train) intent.getSerializableExtra("train");
//        boolean isDone = intent.getBooleanExtra("isDone", false);
//        String train_status = intent.getStringExtra("train_status");
//        String train_type = intent.getStringExtra("train_type");
//        String train_dir = intent.getStringExtra("train_dir");
//        boolean willUpdate = intent.getBooleanExtra("willUpdate",false);
//
//        String notification_message = intent.getStringExtra("notification_message");
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.putExtra("has_train", true);
//        notificationIntent.putExtra("map_id", map_id);
//        notificationIntent.putExtra("train_type", train_type);
//        notificationIntent.putExtra("train_dir", train_dir);
//
//        Intent dismissIntent = new Intent(this, StopServices.class);
//        dismissIntent.putExtra("willDismiss", true);
//
//        Intent TrackNextIntent = new Intent(this, MyBroadCastReciever.class);
//        TrackNextIntent.putExtra("direction", train_dir);
//        TrackNextIntent.putExtra("map_id", map_id);
//        TrackNextIntent.putExtra("station_type", train_type);
//
//
//
//
//        Chicago_Transits chicago_transits = new Chicago_Transits();
//        int image = chicago_transits.getTrainImage(train_type);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//
//        PendingIntent dismiss_pendingIntent =
//                PendingIntent.getBroadcast(this, 0, dismissIntent, 0);
//
//        PendingIntent tracknext_pendingIntent =
//                PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);
//
//        CTA_DataBase cta_dataBase = new CTA_DataBase(getApplicationContext());
//        ArrayList<Object> UserLocation = cta_dataBase.excecuteQuery("*", "USER_LOCATION", "HAS_LOCATION = '1'", null, null);
//        ArrayList<Object> station_record = (ArrayList<Object>)cta_dataBase.excecuteQuery("*", CTA_DataBase.CTA_STOPS, "MAP_ID = '"+map_id+"'", null,null);
//        ArrayList<Object> record = cta_dataBase.excecuteQuery("*", CTA_DataBase.USER_SETTINGS, null,null,null);
//        String subtitle = "";
//
//        if (record != null) {
//            UserSettings settings = (UserSettings) record.get(0);
//            if (UserLocation != null && station_record != null && settings.getIs_sharing_loc().equals("1")) {
//                HashMap<String, String> user_location_record = (HashMap<String, String>) UserLocation.get(0);
//                Station target_station = (Station) station_record.get(0);
//                Double lat = Double.parseDouble(user_location_record.get(CTA_DataBase.USER_LAT));
//                Double lon = Double.parseDouble(user_location_record.get(CTA_DataBase.USER_LON));
//                Double user_distance_miles = chicago_transits.calculate_coordinate_distance(lat, lon, target_station.getLat(), target_station.getLon());
//                Time time = new Time();
//                int user_eta = time.get_estimated_time_arrival(3, user_distance_miles);
//                DecimalFormat df = new DecimalFormat("####0.0");
//                subtitle = "You are " + user_eta + "m away (" + df.format(user_distance_miles) + " mi) from " + target_station.getStation_name();
//            } else {
//                if (station_record != null) {
//                    Station target_station = (Station) station_record.get(0);
//                    subtitle = train_type + " line to " + target_station.getStation_name() + ".";
//                }
//            }
//        }else{
//            Station target_station = (Station) station_record.get(0);
//            subtitle = train_type + " line to " + target_station.getStation_name() + ".";
//        }
//        cta_dataBase.close();
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        notificationBuilder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setTicker("CTA Commuting")
//                .setPriority(Notification.PRIORITY_MAX)
//                .setContentTitle(notification_message)
//                .setContentText(subtitle)
//                .setContentInfo(subtitle)
//                .addAction(image,"Dismiss", dismiss_pendingIntent);
//
//
//        if (willUpdate){
//            // Update Notification?
//            Log.e("Notification", "Updating notification");
//            notificationBuilder.setOnlyAlertOnce(true);
//        }
//
//        if (isDone){
//            notificationBuilder.addAction(image,"Track next nearest train", tracknext_pendingIntent);
//            notificationBuilder.setOnlyAlertOnce(true);
//        }
//
//
//        HashMap<String, Integer> colors = new HashMap<>();
//        colors.put("red", R.color.red_100);
//        colors.put("yellow", R.color.yellow);
//        colors.put("green", R.color.green_100);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.setSmallIcon(R.drawable.red);
//            if (train_status == null){
//                train_status = "red";
//            }
//            notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
//        } else {
//            if (train_status == null){
//                train_status = "red";
//            }
//            notificationBuilder.setSmallIcon(R.drawable.red);
//            notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
//
//        }
//
//        notificationBuilder.setContentIntent(pendingIntent); // On tap response
//        startForeground(1, notificationBuilder.build());
        //do heavy work on a background thread
//        stopSelf();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
