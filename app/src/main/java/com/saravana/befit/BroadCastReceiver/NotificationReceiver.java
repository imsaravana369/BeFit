package com.saravana.befit.BroadCastReceiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.saravana.befit.MainActivity;
import com.saravana.befit.R;
import com.saravana.befit.SplashScreen;

public class NotificationReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
         this.context=context;
         createNotificationChannel();
         String id = String.valueOf(context.getResources().getInteger(R.integer.notification_id));
         Intent homeIntent = new Intent(context, SplashScreen.class);
         homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
         PendingIntent pendingIntent = PendingIntent.getActivity(context,1,homeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

         String title = context.getResources().getString(R.string.notification_title);
         String text = context.getResources().getString(R.string.notification_text);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,id);
        builder.setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.main_menu_icon)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(Integer.parseInt(id),builder.build());

    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            String id = String.valueOf(context.getResources().getInteger(R.integer.notification_id));
            String name = context.getResources().getString(R.string.notification_name);
            String desc = context.getResources().getString(R.string.notication_description);
            NotificationChannel notificationChannel = new NotificationChannel(id,name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(desc);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
