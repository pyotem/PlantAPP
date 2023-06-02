import cv2
import firebase_admin
from firebase_admin import credentials, storage
import time

# Initialize Firebase Admin SDK
cred = credentials.Certificate("/home/pi/Downloads/plantapp-bc512-firebase-adminsdk-bj9ig-3f0110496e.json")
firebase_admin.initialize_app(cred, {
    'storageBucket': "plantapp-bc512.appspot.com"
})
bucket = storage.bucket()

while True:
    # Define the codec and create VideoWriter object
    fourcc = cv2.VideoWriter_fourcc(*'H264')  # Change the codec to XVID
    out = cv2.VideoWriter('output.mp4', fourcc, 20.0, (640, 480))

    # Initialize the camera
    cap = cv2.VideoCapture(0)  # Use 0 as the device index for the Logitech camera

    # Record the video for 10 seconds
    start_time = time.time()
    while time.time() - start_time < 10:
        ret, frame = cap.read()
        if ret:
            # Write the frame to the output video file
            out.write(frame)

            # Display the frame (optional)
            cv2.imshow('frame', frame)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

    # Release the resources
    cap.release()
    out.release()
    cv2.destroyAllWindows()

    # Upload the video file to Firebase Storage
    blob = bucket.blob('output.mp4')
    blob.upload_from_filename('output.mp4', content_type='video/mp4')
    print('File uploaded to Firebase Storage.')

    # Wait for 10 minutes before repeating the code
    time.sleep(1000)