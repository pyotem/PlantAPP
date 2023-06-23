import paho.mqtt.client as mqtt
from firebase_admin import db
from firebase import firebase
import firebase_admin
from firebase_admin import credentials
import time
firebase = firebase.FirebaseApplication("https://plantapp-bc512-default-rtdb.firebaseio.com/.json", None)
cred = credentials.Certificate("/home/pi/Downloads/plantapp-bc512-firebase-adminsdk-bj9ig-3f0110496e.json")
firebase_admin.initialize_app(cred, {
'databaseURL': 'https://plantapp-bc512-default-rtdb.firebaseio.com/'
})

ref = db.reference('settings')
val = ''
val2 = ''
val3 = ''
mqtt_client = mqtt.Client()

def on_connect(client, userdata, flags, rc):
print("Connected with result code " + str(rc))

client.subscribe("/esp8266/temperature")
client.subscribe("/esp8266/humidity")
client.subscribe("/esp8266/smoke")

def on_message(client, userdata, message):

if message.topic == "/esp8266/temperature":
print("temperature update")
temp = str(message.payload, 'UTF-8')
temp = temp.strip()
print(temp)
global val
val = temp

if message.topic == "/esp8266/humidity":
print("humidity update")
humi = str(message.payload, 'UTF-8')
humi = humi.strip()
print(humi)
global val2
val2 = humi

if message.topic == "/esp8266/smoke":
print("smoke update")
smoke = str(message.payload, 'UTF-8')
smoke = smoke.strip()
print(smoke)
global val3
val3 = smoke

if val != '' and val2 != '' and val3 != '' :    
print(val, val2 ,val3)
data = {"Temperature": val, "Humidity": val2 , "Smoke":val3}
firebase.post('/sensor/dht', data)
val = ''
val2 = ''
val3= ''
time.sleep(10)

def on_settings_change(event, client):
print("Degisen:", event.path)

if isinstance(event.data, dict):
settings = event.data

print("Duman:", settings.get("duman", 0))
print("Sulama:", settings.get("sulama", 0))
print("Sıcaklık:", settings.get("sıcaklık", 0))
print("Toprak Nem:", settings.get("topraknem", 0))
print("Tüm:", settings.get("tüm", 0))
print("Yağmur:", settings.get("yağmur", 0))


if "duman" in settings:
duman = settings["duman"]
if isinstance(duman, int):  # Check if duman is an integer
    duman = str(duman)
print("Duman:", duman)
client.publish("/esp8266/duman", duman)

if "sulama" in settings:
sulama = settings["sulama"]
if isinstance(sulama, int):  # Check if sulama is an integer
    sulama = str(sulama)
print("Sulama:", sulama)
client.publish("/esp8266/sulama", sulama)

if "sıcaklık" in settings:
sıcaklık = settings["sıcaklık"]
if isinstance(sıcaklık, int):  # Check if sıcaklık is an integer
    sıcaklık = str(sıcaklık)
print("Sıcaklık:", sıcaklık)
client.publish("/esp8266/sıcaklık", sıcaklık)

if "topraknem" in settings:
topraknem = settings["topraknem"]
if isinstance(topraknem, int):  # Check if topraknem is an integer
    topraknem = str(topraknem)
print("Toprak Nem:", topraknem)
client.publish("/esp8266/topraknem", topraknem)

if "tüm" in settings:
tüm = settings["tüm"]
if isinstance(tüm, int):  # Check if tüm is an integer
    tüm = str(tüm)
print("Tüm:", tüm)
client.publish("/esp8266/tüm", tüm)

if "yağmur" in settings:
yağmur = settings["yağmur"]
if isinstance(yağmur, int):  # Check if yağmur is an integer
    yağmur = str(yağmur)
print("Yağmur:", yağmur)
client.publish("/esp8266/yağmur", yağmur)

elif isinstance(event.data, int):
# Handle the case when event.data is an integer
# Example:
print("Yeni değer:", event.data)
# Do something with the integer value

else:
print("Invalid settings data type:", type(event.data))

mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message

mqtt_client.connect('localhost', 1883, 60)
mqtt_client.loop_start()

ref.listen(lambda event: on_settings_change(event, mqtt_client))
