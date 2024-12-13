package com.example.challenge3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.os.Build;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MqttForegroundService extends Service {

    private static MqttAndroidClient mqttAndroidClient;

    private static OnMqttClientReadyListener mqttClientReadyListener;

    public interface OnMqttClientReadyListener {
        void onClientReady(MqttAndroidClient client);
    }

    public static void setOnMqttClientReadyListener(OnMqttClientReadyListener listener) {
        mqttClientReadyListener = listener;
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            listener.onClientReady(mqttAndroidClient);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MQTT", "Service onStartCommand called");
        // Existing code to handle foreground service and MQTT
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("MQTT","Before notification creation");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("your_channel_id", "MQTT Service", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("MQTT service is running in the foreground.");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        // Create notification for foreground service
        Notification notification = new NotificationCompat.Builder(this, "your_channel_id")
                .setContentTitle("MQTT Service")
                .setContentText("MQTT is running in the foreground")
                .setSmallIcon(R.drawable.ic_arduino) // Make sure to set a small icon
                .build();

        Log.d("MQTT","After notification creation");

        // For Android 13 (API 33) and above, set the foreground service type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Specify the foreground service type
            Log.d("MQTT", "Starting foreground service with type: DATA_SYNC");
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC );
        } else {
            // For older versions, no need to specify service type
            Log.d("MQTT", "Starting foreground service without type");
            startForeground(1, notification);
        }

        Log.d("MQTT", "Starting client connection");

        // Initialize MQTT client
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), "ssl://84a3e970e8234777ad22ce82e5433680.s1.eu.hivemq.cloud:8883", "clientId", Ack.AUTO_ACK);

        // Configure MQTT connection options
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("miguel");
        mqttConnectOptions.setPassword("Miguel123".toCharArray());
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        // Connect to MQTT broker
        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQTT", "Connected successfully!");
                // Here you can set up subscriptions or handle connection success
                if (mqttClientReadyListener != null) {
                    mqttClientReadyListener.onClientReady(mqttAndroidClient);
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w("MQTT", "Failed to connect: " + exception.getMessage());
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // No binding
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttAndroidClient != null) {
            mqttAndroidClient.disconnect();
        }
    }

    public static MqttAndroidClient getMqttClient() {
        return mqttAndroidClient;
    }
}
