package com.example.challenge3;


import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class MQTTHelper {

    private MqttAndroidClient mqttAndroidClient;
    String userId;

    // Constructor that accepts an existing MqttAndroidClient
    public MQTTHelper(MqttAndroidClient client, String userId) {
        this.mqttAndroidClient = client;
        this.userId = userId;
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

    public void publishMessage(String topic, String message) throws Exception{
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                mqttAndroidClient.publish(topic, mqttMessage);
                Log.d("MQTT", "Message published to topic: " + topic);
            } catch (Exception e) {
                Log.e("MQTT", "Failed to publish message: " + e.getMessage());
            }
        } else {
            Log.e("MQTT", "MQTT Helper is null or not connected. Cannot publish message.");
        }
    }


    public Message messageArrived(String topic, MqttMessage message) {
        Log.d("MQTT", "Message arrived from topic: " + topic);
        Log.d("MQTT", "Message content: " + message.toString());

        return processMessageReceived(topic, message.toString());
    }

    // Callback to handle the received message
    private Message processMessageReceived(String topic, String message) {
        Log.d("MQTT", "Processing message: " + message + " from topic: " + topic);
        Message newMessage = null;
        String[] parts = message.split("_");
        Log.d("MQTT", "For user: " + parts[0].strip());
        Log.d("MQTT", "Current user: " + userId);
        if(parts[0].strip().matches(userId)){
            // Message is for current user
            newMessage = new Message(0, new User(parts[1]), new User(parts[0]), parts[2], parts[3]);
            Log.d("MQTT", "Timestamp: " + parts[3]);
        }
        else{
            Log.d("MQTT", "Message is not for user");
        }
        return newMessage;
    }

    public void stop() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.disconnect();
        }
    }
}
