#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include "DHT.h"

const char* ssid = "Ahmet";
const char* password = "Aydeniz123.";
const char* mqtt_server = "172.20.10.14";
eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee,
WiFiClient espClient;
PubSubClient client(espClient);

const int DHTPin = D1; // DHT
int sensor_pin = D2; // Yağmur
int output_value;
#define MQ2pin D0 // Duman
float sensorOku;
#define DHTTYPE DHT11  
float veri;
DHT dht(DHTPin, DHTTYPE);
int duman, sıcaklık, tüm, yağmur;

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("WiFi connected - ESP IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();
  
  // Yeni verileri analiz etmek için burada gerekli işlemleri yapabilirsiniz.
  // Örneğin, verileri ayrıştırabilir ve değişkenlere atayabilirsiniz.
  
  // Yağmur verisini almak için:
  if (String(topic) == "/esp8266/yağmur") {
    yağmur = atoi(messageTemp.c_str());
    Serial.print("yağmur: ");
    Serial.println(yağmur);
  }
  if (String(topic) == "/esp8266/sıcaklık") {
    sıcaklık = atoi(messageTemp.c_str());
    Serial.print("sıcaklık: ");
    Serial.println(sıcaklık);
  }
  if (String(topic) == "/esp8266/duman") {
    duman = atoi(messageTemp.c_str());
    Serial.print("duman: ");
    Serial.println(duman);
  }
  if (String(topic) == "/esp8266/tüm") {
    tüm = atoi(messageTemp.c_str());
    Serial.print("tüm: ");
    Serial.println(tüm);
  }
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    
    if (client.connect("ESP8266Client")) {
      Serial.println("connected");
      client.subscribe("esp8266/4");
      client.subscribe("esp8266/5");
      // İlgili konulara abone olabilirsiniz:
      client.subscribe("/esp8266/duman");
      client.subscribe("/esp8266/sıcaklık");
      client.subscribe("/esp8266/tüm");
      client.subscribe("/esp8266/yağmur");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void setup() {
  dht.begin();
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
    if (!client.loop()) {
      client.connect("ESP8266Client");
    }
  }

  if (!client.loop()) {
    client.connect("ESP8266Client");
  }
  if (tüm ==1){
  if(sıcaklık==1)
  {
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  float f = dht.readTemperature(true);
  float Moisture = analogRead(sensor_pin);

  if (isnan(h) || isnan(t) || isnan(f)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }

  static char temperatureTemp[7];
  dtostrf(t, 6, 2, temperatureTemp);
  
  static char humidityTemp[7];
  dtostrf(h, 6, 2, humidityTemp);
    
  client.publish("/esp8266/temperature", temperatureTemp);
  client.publish("/esp8266/humidity", humidityTemp);

  Serial.print("Humidity: ");
  Serial.print(h);
  Serial.print("\t Temperature: ");
  Serial.print(t);
  Serial.println(" °C");
  }
  if(duman==1)
  {
  // Duman sensörü verisini MQTT broker'a göndermek için:
  sensorOku = digitalRead(MQ2pin);
  char Smoke[7];
  itoa(sensorOku, Smoke, 10);
  client.publish("/esp8266/smoke", Smoke);
  }
  if(yağmur==1)
  {
  // Yağmur sensörü verisini MQTT broker'a göndermek için:
  veri = digitalRead(sensor_pin);
  char Yagmur[7];
  itoa(veri, Yagmur, 10);
  client.publish("/esp8266/rain", Yagmur);
  }
  Serial.println(duman);
  Serial.println(yağmur);
  Serial.println(sıcaklık);
  Serial.println(tüm);
  delay(5000);
}}
