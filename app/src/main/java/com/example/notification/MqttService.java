package com.example.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MqttService extends Service {
    private static final String Foreground_CHANNEL_ID = "foreground_service_channel";
    private static final String Notification_CHANNEL_ID = "notification_channel";
    private static final int FOREGROUND_SERVICE_ID = 100;
    private MqttHandler mqttHandler;
    private String brokerUrl = "tcp://broker.hivemq.com:1883";
    private String clientId = "Pixel_6";

    @Override
    public void onCreate() {
        super.onCreate();
        mqttHandler = new MqttHandler();
        createNotificationChannel();
        //mqtt連接
        mqttHandler.connect(brokerUrl, clientId, new MqttMessageListener() {
            //接收的訊息
            @Override
            public void onMessageReceived(String topic, String message) {
                // 發送通知
                showNotification(message);
            }
        });
        mqttHandler.subscribe("test/");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 使用空的Notification來設置前台服務
        startForeground(FOREGROUND_SERVICE_ID, createNotification());
        // 其他初始化操作
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        // 斷開MQTT連接
        mqttHandler.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //創建頻道
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                Notification_CHANNEL_ID,
                "MQTT Service Channel",
                NotificationManager.IMPORTANCE_NONE
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    //創建通知
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, Notification_CHANNEL_ID)
                .setSmallIcon(R.drawable.monitor)
                .setPriority(NotificationManager.IMPORTANCE_NONE)
                .setGroup("FOREGROUND_GROUP")
                .build();
    }

    //發送通知
    private void showNotification(String message) {
        long currentTimeMillis = System.currentTimeMillis();
        int notificationId = (int) currentTimeMillis;
        // 创建打开页面的Intent
        Intent openIntent = new Intent(this, MainActivity2.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 1, openIntent, PendingIntent.FLAG_MUTABLE);

        // 創建通知
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Notification_CHANNEL_ID)
                .setSmallIcon(R.drawable.test)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.monitor))
                .setContentTitle("監視器通知")
                .setContentText(message)
                //通知被使用者點擊後是否清除
                .setAutoCancel(true)
                //設置通知等級
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //設置通知類型
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(openPendingIntent);

        // 發送通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
