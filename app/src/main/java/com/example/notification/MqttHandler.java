package com.example.notification;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttHandler {

    private MqttClient client;
    private MqttMessageListener messageListener;

    public void connect(String brokerUrl, String clientId, MqttMessageListener listener) {
        this.messageListener = listener;
        try {
            // Set up the persistence layer
            MemoryPersistence persistence = new MemoryPersistence();

            // Initialize the MQTT client
            client = new MqttClient(brokerUrl, clientId, persistence);

            // Set up the connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();

            connectOptions.setCleanSession(true);

            // Connect to the broker
            client.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //接收訊息
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // 連接丟失時的處理
                Log.e("TAG", "丟失訊息: "+cause );
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String receivedMessage = new String(message.getPayload());
                try {
                    // 將收到的 JSON 訊息轉換成 JSONObject
                    JSONObject jsonObject = new JSONObject(receivedMessage);

                    // 從 JSONObject 中取得 "msg" 鍵的值
                    String msgValue = jsonObject.getString("msg");

                    // 在這裡處理 msgValue，例如顯示在 UI 上或執行其他動作
//                    Log.e("解析後訊息: ", msgValue);
                    if (messageListener != null) {
                        messageListener.onMessageReceived(topic, msgValue);
                        Log.e("TAG","訊息: " + msgValue);
                    }

                } catch (JSONException e) {
                    // JSON 解析出錯的處理
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 消息交付完成時的處理
                Log.e("TAG", "成功發送訊息");
            }
        });

    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);
            Log.i("TAG", "subscribe: "+"訂閱成功");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe(String topic) {
        try {
            client.unsubscribe(topic);
            Log.e("TAG", "unSubscribe: "+ "已取消訂閱");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
