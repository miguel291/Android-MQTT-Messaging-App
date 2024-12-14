/*
  Based on:
    https://wokwi.com/projects/322524997423727188
    https://wokwi.com/projects/321525495180034642
*/

#include <PubSubClient.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#include <WiFi.h>

// Define the LCD screen
LiquidCrystal_I2C LCD = LiquidCrystal_I2C(0x27, 16, 2);

// Define random ID
String ID_MQTT;
char *letters = "abcdefghijklmnopqrstuvwxyz0123456789";

// Define MQTT Topics
const char* TOPIC_TESTPUB="topicstest";
const char* TOPIC_TESTSUB="MC/MCHAT/TOPICGENERAL/";

// Update every 1 seconds
#define PUBLISH_DELAY 500
unsigned long publishUpdate;

// Wi-Fi settinges
const char *SSID = "Wokwi-GUEST";
const char *PASSWORD = "";

// Define MQTT Broker and PORT
//broker.hivemq.com can be another alternative for a public broker
//const char *BROKER_MQTT = "broker.emqx.io";
const char *BROKER_MQTT = "broker.hivemq.com";
int BROKER_PORT = 1883;

// Defined ports
const int LED_PIN = 12;

// Global variables
WiFiClient espClient;
PubSubClient MQTT(espClient); 

// Declarations
void startWifi(void);
void initMQTT(void);
void callbackMQTT(char *topic, byte *payload, unsigned int length);
void reconnectMQTT(void);
void reconnectWiFi(void);
void checkWiFIAndMQTT(void);


// Starts the Wi-Fi
void startWifi(void) {
  reconnectWiFi();
}

// Starts everything from MQTT
void initMQTT(void) {
  MQTT.setServer(BROKER_MQTT, BROKER_PORT);
  MQTT.setCallback(callbackMQTT);
}

// Callback from Android 
// --- Get the messages here
void callbackMQTT(char *topic, byte *payload, unsigned int length) {
  String msg;

  // Convert payload to string
  for (int i = 0; i < length; i++) {
    char c = (char)payload[i];
    msg += c;
  }

  msg = msg.c_str();
  Serial.printf("Topic: %s\n", topic);

  int firstUnderscore = msg.indexOf('_');
  int secondUnderscore = msg.indexOf('_', firstUnderscore + 1);
  int thirdUnderscore = msg.indexOf('_', secondUnderscore + 1);

  if (firstUnderscore == -1 || secondUnderscore == -1 || thirdUnderscore == -1) {
    Serial.println("Error: Message format incorrect.");
    return;
  }
  
  String sender = msg.substring(0, firstUnderscore);
  String receiver = msg.substring(firstUnderscore + 1, secondUnderscore);
  String content = msg.substring(secondUnderscore + 1, thirdUnderscore);
  String timestamp = msg.substring(thirdUnderscore + 1);
  
  // Debugging prints
  Serial.printf("Sender: %s\n", sender.c_str());
  Serial.printf("Receiver: %s\n", receiver.c_str());
  Serial.printf("Message: %s\n", content.c_str());
  Serial.printf("Timestamp: %s\n", timestamp.c_str());
  
  if (receiver.equals("arduino")) {
    printMessageOnScreen(sender, content);
    blinkLed(2000);
  }

}


void printMessageOnScreen(String sender, String content){
    LCD.clear();
    LCD.setCursor(0, 0);
    LCD.print("From: " + sender);
    LCD.setCursor(0, 1);
    LCD.print(content);
}


void blinkLed(int duration){
  digitalWrite(LED_PIN, HIGH);
  delay(duration);
  digitalWrite(LED_PIN, LOW);
}

// Connects to the Broker with a specific random ID
void reconnectMQTT(void) {
  while (!MQTT.connected()) {
    ID_MQTT = "";
    Serial.print("* Starting connection with broker: ");
    Serial.println(BROKER_MQTT);

    int i = 0;
    for (i = 0; i < 10; i++) {
      ID_MQTT = ID_MQTT + letters[random(0, 36)];
    }

    if (MQTT.connect(ID_MQTT.c_str())) {
      Serial.print("* Connected to broker successfully with ID: ");
      Serial.println(ID_MQTT);
      MQTT.subscribe(TOPIC_TESTSUB);
    } else {
      Serial.println("* Failed to connected to broker. Trying again in 2 seconds.");
      delay(2000);
    }
  }
}

// Checks both Wi-Fi and MQTT state, and reconnects if something failed.
void checkWiFIAndMQTT(void) {
  if (!MQTT.connected())
    reconnectMQTT();
  reconnectWiFi();
}

void reconnectWiFi(void) {
  if (WiFi.status() == WL_CONNECTED)
    return;

  WiFi.begin(SSID, PASSWORD); // Conecta na rede WI-FI

  Serial.print("* Connecting to Wifi ");
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("* Successfully connected to Wi-Fi, with local IP: ");
  Serial.println(WiFi.localIP());

  LCD.clear();
  LCD.setCursor(0, 0);
  LCD.print("Finished!");
  LCD.setCursor(0, 1);
  LCD.print("-- ");
  LCD.print(WiFi.localIP());
}

// ==========================================
void setup() {
  Serial.begin(115200);
  Serial.println("ST");

  LCD.init();
  LCD.backlight();
  LCD.setCursor(0, 0);
  LCD.print("Initializing...");
  LCD.setCursor(0, 1);
  LCD.print("Please wait...");
  pinMode(LED_PIN, OUTPUT);
  startWifi();
  initMQTT();
  
}

void loop() {
  // Loop every 1 second
  if ((millis() - publishUpdate) >= PUBLISH_DELAY) {
    publishUpdate = millis();
    // Check if Wi-Fi and MQTT are up.
    checkWiFIAndMQTT();

    //LCD.clear();
    //LCD.setCursor(0, 0);
    //LCD.print("Time: " + String(publishUpdate));

    //MQTT.publish(TOPIC_TESTPUB, String(publishUpdate).c_str());

    // MQTT Keep-alive loop
    MQTT.loop();
  }
}
