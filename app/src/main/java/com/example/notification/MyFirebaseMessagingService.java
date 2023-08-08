package com.example.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    private static final String NOTIFICATION_CHANNEL_ID = "notification_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(TAG, "onNewToken: "+token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // 接收來自 FCM 的消息
        if (message.getNotification() != null) {
            String messageTitle = message.getNotification().getTitle();
            String messageBody = message.getNotification().getBody();
//            String messageIcon = message.getNotification().getIcon();
            showNotification(messageTitle, messageBody);
        }
    }


    private void showNotification(String messageTitle, String messageBody) {
        long currentTimeMillis = System.currentTimeMillis();
        int notificationId = (int) currentTimeMillis;

        // 創建通知
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.removebg_preview))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // 發送通知
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        // 創建通知頻道 (Android 8.0 及以上版本需要)
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "MQTT Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(notificationId, notificationBuilder.build());

    }
}
