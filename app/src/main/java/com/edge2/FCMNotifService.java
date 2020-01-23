package com.edge2;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotifService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification fbNotif = remoteMessage.getNotification();
        if (fbNotif == null) {
            return;
        }

        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this,
                "Updates")
                .setSmallIcon(R.drawable.ic_stat_notif)
                .setContentTitle(fbNotif.getTitle())
                .setColor(getColor(R.color.colorAccent))
                .setContentText(fbNotif.getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(0, notifBuilder.build());
    }
}
