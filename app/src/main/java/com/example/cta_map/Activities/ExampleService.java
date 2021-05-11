package com.example.cta_map.Activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.cta_map.Backend.Threading.Message;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;

import java.util.HashMap;

import static com.example.cta_map.Activities.App.CHANNEL_ID;

public class ExampleService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String map_id = intent.getStringExtra("map_id");
        boolean isDone = intent.getBooleanExtra("isDone", false);
        String train_status = intent.getStringExtra("train_status");
        String train_type = intent.getStringExtra("train_type");
        String train_dir = intent.getStringExtra("train_dir");
        String notification_message = intent.getStringExtra("notification_message");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("has_train", true);
        notificationIntent.putExtra("map_id", map_id);
        notificationIntent.putExtra("train_type", train_type);
        notificationIntent.putExtra("train_dir", train_dir);

        Intent dismissIntent = new Intent(this, StopServices.class);
        dismissIntent.putExtra("willDismiss", true);

        Intent TrackNextIntent = new Intent(this, BroadcastReceiver.class);
        TrackNextIntent.putExtra("direction", train_dir);
        TrackNextIntent.putExtra("map_id", map_id);
        TrackNextIntent.putExtra("station_type", train_type);




        Chicago_Transits chicago_transits = new Chicago_Transits();
        int image = chicago_transits.getTrainImage(train_type);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent dismiss_pendingIntent =
                PendingIntent.getBroadcast(this, 0, dismissIntent, 0);

        PendingIntent tracknext_pendingIntent =
                PendingIntent.getBroadcast(this, 1, TrackNextIntent, 0);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("CTA Stops")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notification_message)
                .setContentText(notification_message)
                .setContentInfo(notification_message)
                .addAction(image,"Dismiss", dismiss_pendingIntent);
        if (isDone){
            notificationBuilder.addAction(image,"Track Next Train", tracknext_pendingIntent);
            notificationBuilder.setOnlyAlertOnce(true);
        }


        HashMap<String, Integer> colors = new HashMap<>();
        colors.put("red", R.color.red_100);
        colors.put("yellow", R.color.yellow);
        colors.put("green", R.color.green_100);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.red);
            if (train_status == null){
                train_status = "red";
            }
            notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));
        } else {
            if (train_status == null){
                train_status = "red";
            }
            notificationBuilder.setSmallIcon(R.drawable.red);
            notificationBuilder.setColor(getResources().getColor(colors.get(train_status.toLowerCase().trim())));

        }

        notificationBuilder.setContentIntent(pendingIntent); // On tap response
        notificationBuilder.build();

        startForeground(1, notificationBuilder.build());
        //do heavy work on a background thread
//        stopSelf();
        return START_NOT_STICKY;
    }
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
