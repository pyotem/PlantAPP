#include <ESP8266WiFi.h>
#include <PubSubClient.h>

const char* ssid = "Ahmet";
const char* password = "Aydeniz123.";
const char* mqtt_server = "172.20.10.14";

WiFiClient espClient;
PubSubClient client(espClient);

int topraknem , sulama ,tüm;
int nemSensor=A0;
int sinirDeger=500;
#define motor_pin D0
float sensorOku; 


long now = millis();
long lastMeasure = 0;

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

void callback(String topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();
    if (String(topic) == "/esp8266/topraknem") {
    topraknem = atoi(messageTemp.c_str());
    Serial.print("topraknem: ");
    Serial.println(topraknem);
  }
  if (String(topic) == "/esp8266/sulama") {
    sulama = atoi(messageTemp.c_str());
    Serial.print("sulama: ");
    Serial.println(sulama);
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
      client.subscribe("/esp8266/topraknem");
      client.subscribe("/esp8266/tüm");
      client.subscribe("/esp8266/sulama");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void setup() {
  pinMode(motor_pin,OUTPUT);
  pinMode(nemSensor,INPUT);

  digitalWrite(motor_pin,LOW);
  Serial.begin(9600);
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  if (!client.loop()) {
    client.connect("ESP8266Client");
  }
if(tüm==1){
  if(topraknem==1){
  int nem = analogRead(nemSensor);
  Serial.println(nem);
  if(sulama==1){

  if(nem>=sinirDeger)
  {
    digitalWrite(motor_pin,LOW);
    delay(1000);
    digitalWrite(motor_pin,HIGH);
    delay(5000);
  }
  else
  {
    digitalWrite(motor_pin,HIGH);
  }
  }
    

  }}

   
    delay(20000);
   
  }  
  


