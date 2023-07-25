package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity{
    Button button;
    private static final String CHANNEL_ID = "channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        button = findViewById(R.id.button);
        Intent intent = new Intent(MainActivity.this, MqttService.class);
        startForegroundService(intent);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                showNotification();
            }
        });
    }
    //創建頻道
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    //發送通知
    private void showNotification() {
        long currentTimeMillis = System.currentTimeMillis();
        int notificationId = (int) currentTimeMillis;
        // 创建打开页面的Intent
        Intent openIntent = new Intent(this, MainActivity2.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 1, openIntent, PendingIntent.FLAG_MUTABLE);

        // 創建通知
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.test)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.monitor))
                .setContentTitle("hi")
                .setContentText("message")
                //通知被使用者點擊後是否清除
                .setAutoCancel(true)
                //設置通知等級
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //設置通知類型
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(openPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // 發送通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}