import paho.mqtt.client as mqtt
from firebase import firebase
from firebase_admin import db
import firebase_admin
from firebase_admin import credentials
import time
firebase = firebase.FirebaseApplication("https://plantapp-bc512-default-rtdb.firebaseio.com/.json", None)
cred = credentials.Certificate("/home/pi/Downloads/plantapp-bc512-firebase-adminsdk-bj9ig-3f0110496e.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://plantapp-bc512-default-rtdb.firebaseio.com/'
})
ref = db.reference('settings')

val=''
val2=''
val3=''
duman = ref.child('duman').get()
print('Duman:', duman)

# Sulama verisini çekme
sulama = ref.child('sulama').get()
print('Sulama:', sulama)

# Sıcaklık verisini çekme
sıcaklık = ref.child('sıcaklık').get()
print('Sıcaklık:', sıcaklık)

# Toprak nem verisini çekme
topraknem = ref.child('topraknem').get()
print('Toprak Nem:', topraknem)

# Tüm veriyi çekme
tüm = ref.child('tüm').get()
print('Tüm:', tüm)

# Yağmur verisini çekme
yağmur = ref.child('yağmur').get()
print('Yağmur:', yağmur)

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    client.subscribe("/esp8266/temperature")
    client.subscribe("/esp8266/humidity")
    client.subscribe("/esp8266/yagmur")
    client.subscribe("/esp8266/smoke")
    client.publish("/esp8266/duman",duman)
    print("Duman değeri ESP'ye gönderildi:", duman)
    # Yağmur verisini ESP'ye gönderme
    
    client.publish("/esp8266/yağmur", yağmur)
    print("Yağmur değeri ESP'ye gönderildi:", yağmur)
    
    # Sıcaklık verisini ESP'ye gönderme
    client.publish("/esp8266/sıcaklık", sıcaklık)
    print("Sıcaklık değeri ESP'ye gönderildi:", sıcaklık)
    
    # Tüm verileri ESP'ye gönderme
    client.publish("/esp8266/tüm", tüm)
    print("Tüm veri ESP'ye gönderildi:", tüm)

def on_message(client, userdata, message):
    print("Received message '" + str(message.payload) + "' on topic '" + message.topic)
    
   

    
    humi = 0
    temp = 0
    rainp= 0
    smoke= 0
    if message.topic == "/esp8266/smoke":
        print("smoke update")
        smoke = str(message.payload, 'UTF-8')
        smoke = smoke.strip()
        print(smoke)
        global val3
        val3 = smoke
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
    
    
    if val != '' and val2 != '' and val3 != ''  :    
        print(val, val2 ,val3)
        data = {"Temperature": val, "Humidity": val2 , "Smoke":val3}
        firebase.post('/sensor/dht', data)
        val = ''
        val2 = ''
        val3= ''
        
        time.sleep(10) # wait for 1 minute
def main():
    mqtt_client = mqtt.Client()
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message
    
    mqtt_client.connect('localhost', 1883, 60) 
    mqtt_client.loop_start()
    

if __name__ == '__main__':
    print('MQTT to InfluxDB bridge')
    
    main()
        
    

