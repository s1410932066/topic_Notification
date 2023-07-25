package com.example.notification;

public interface MqttMessageListener {
    void onMessageReceived(String topic, String message);
}
