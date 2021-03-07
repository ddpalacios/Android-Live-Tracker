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
import com.example.cta_map.R;

public class NotificationBuilder {
    Context context;
    Intent intent;
    public NotificationBuilder(Context context, Intent intent){
        this.context = context;
        this.intent = intent;
    }


    /**
     * This method creates an array of Intent objects representing the
     * activity stack for the incoming message details state that the
     * application should be in when launching it from a notification.
     * @return
     */
    private Intent[] makeMessageIntentStack(Context context, CharSequence from,
                                            CharSequence msg) {
        // A typical convention for notifications is to launch the user deeply
        // into an application representing the data in the notification; to
        // accomplish this, we can build an array of intents to insert the back
        // stack stack history above the item being displayed.
        Intent[] intents = new Intent[4];

        // First: root activity of ApiDemos.
        // This is a convenient way to make the proper Intent to launch and
        // reset an application's task.
//        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
//               mainactivity.class));
//
//        // "App"
//        intents[1] = new Intent(context, mainactivity.class);
//        intents[1].putExtra(" com.example.cta_map.Displayers", "App");
//        // "App/Notification"
//        intents[2] = new Intent(context,  mainactivity.class);
//
//        // Now the activity to display to the user.  Also fill in the data it
//        // should display.
//        intents[3] = new Intent(context, mainactivity.class);
////        intents[3].putExtra(IncomingMessageView.KEY_FROM, from);
////        intents[3].putExtra(IncomingMessageView.KEY_MESSAGE, msg);

        return intents;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notificationDialog(String notification_title, String text ) {
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            // play vibration
//            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//
//            // play sound
//            Intent serviceIntent = new Intent(context, SoundService.class);
//            serviceIntent.setAction("ACTION_START_PLAYBACK");
//            serviceIntent.putExtra("SOUND_URI", soundUri.toString());
//            context.startForegroundService(serviceIntent);
//
//            // the delete intent will stop the sound when the notification is cleared
//            Intent deleteIntent = new Intent(context, SoundService.class);
//            deleteIntent.setAction("ACTION_STOP_PLAYBACK");
//            PendingIntent pendingDeleteIntent =
//                    PendingIntent.getService(context, 0, deleteIntent, 0);
//            builder.setDeleteIntent(pendingDeleteIntent);
//
//        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Tutorialspoint")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notification_title)
                .setAutoCancel(true) // This dismisses the Notification when it is clicked
//                .setOnlyAlertOnce(true) //this is very important, it pops up the notification only once. Subsequent notify updates are muted. unless it is loaded again
                .setContentText(text)
                .setContentInfo("Information");

        PendingIntent contentIntent = PendingIntent.getActivities(this.context, 0,
                makeMessageIntentStack(this.context, null, null), PendingIntent.FLAG_CANCEL_CURRENT);



        notificationBuilder.setContentIntent(contentIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createWaveform( new long[]{100,100}, -1));

//            notificationManager.createNotificationChannel(notificationChannel);
        }
//        notificationManager.notify(1, notificationBuilder.build());

    }




}
