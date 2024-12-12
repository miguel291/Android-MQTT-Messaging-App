package com.example.challenge3;


import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;

    //final String server = "tcp://2.80.198.184:1883";
    //final String server = "tcp://broker.hivemq.com:1883";
    final String server = "ssl://84a3e970e8234777ad22ce82e5433680.s1.eu.hivemq.cloud:8883";
    final String username = "miguel";
    final String password = "Miguel123";
    final String TAG = "MQTT";
    private String topic;
    private String name;


    public MQTTHelper(Context context, String name, String topic) {
        this.name = name;
        this.topic = topic;

        mqttAndroidClient = new MqttAndroidClient(context, server, name, Ack.AUTO_ACK);
        // Create notification for foreground service
        Notification notification = new NotificationCompat.Builder(context, "your_channel_id")
                .setContentTitle("MQTT Service")
                .setContentText("MQTT is running in the foreground")
                .setSmallIcon(R.drawable.ic_arduino) // Make sure to set a small icon
                .build();

        // Set it as a foreground service with type for data sync
        mqttAndroidClient.setForegroundService(notification, 3);  // 3 is for data sync
        // Set the foreground service type
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                //Adjusting the set of options that govern the behaviour of Offline (or Disconnected) buffering of messages
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Failed to connect to: " + server + " " + exception.toString());
            }
        });

    }

    public void stop() {
        mqttAndroidClient.disconnect();
    }


    public void subscribeToTopic(String topic) {
        mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.w(TAG, "Subscribed!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Subscribed fail!");
            }
        });
    }

    public void unsubscribeToTopic(String topic) {
        mqttAndroidClient.unsubscribe(topic, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG, "Unsubscribed!");
            }
            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Unsubscribed fail!");
            }
        });

    }

    public String getName() {
        return name;
    }
}
