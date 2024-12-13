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

    private MqttAndroidClient mqttAndroidClient;

    // Constructor that accepts an existing MqttAndroidClient
    public MQTTHelper(MqttAndroidClient client) {
        this.mqttAndroidClient = client;
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }
/*
    public void connect() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Connected successfully!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("MQTT", "Failed to connect: " + exception.getMessage());
                }
            });
        }
    }*/

    public void subscribeToTopic(String topic) {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Subscribed successfully!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("MQTT", "Failed to subscribe: " + exception.getMessage());
                }
            });
        }
    }

    public void unsubscribeToTopic(String topic) {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.unsubscribe(topic, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Unsubscribed successfully!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("MQTT", "Failed to unsubscribe: " + exception.getMessage());
                }
            });
        }
    }

    public void stop() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.disconnect();
        }
    }
}
