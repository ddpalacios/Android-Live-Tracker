package com.example.cta_map.Displayers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

//import com.example.cta_map.Activities.Navigation.mainactivity;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.R;

public class NotificationBuilder {
    Context context;
    Intent intent;
    public NotificationBuilder(Context context, Intent intent){
        this.context = context;
        this.intent = intent;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notificationDialog(String notification_title, String text, Train train) {
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("CTA Stops")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notification_title)
                .setAutoCancel(true) // This dismisses the Notification when it is clicked
                .setContentText(text)
                .setContentInfo("Information");
        Intent intent = new Intent(context, MainActivity.class);
        String map_id = train.getTarget_id();
        String station_type = train.getTrain_type();
        String station_dir = train.getTrDr();
        intent.putExtra("map_id", map_id );
        intent.putExtra("station_dir", station_dir);
        intent.putExtra("station_type", station_type);

        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0,
               intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setVibrate(new long[]{50, 50});
        notificationBuilder.setContentIntent(contentIntent);

        notificationManager.notify(1, notificationBuilder.build());

    }




}
