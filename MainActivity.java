package com.example.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "smoke_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManagerCompat notificationManager;
    private BarChart temperatureChart; // BarChart for temperature values
    private BarChart humidityChart; // BarChart for humidity values
    private ImageButton imageButton; // Image button for opening a new page
    private TextView temptext;
    private TextView humtext;
    private ImageButton setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageButton = findViewById(R.id.imageButton);

        imageButton.setContentDescription("Open New Page");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open NewPageActivity
                Intent intent = new Intent(MainActivity.this, NewPageActivity.class);
                startActivity(intent);
            }
        });

        setting = findViewById(R.id.setting);

        setting.setContentDescription("Open Settings Page");
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open settingspage
                Intent intent = new Intent(MainActivity.this, settingActivity.class);
                startActivity(intent);
            }
        });


        // Firebase Realtime Database reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("sensor/dht");
        temptext = findViewById(R.id.temptext);
        humtext = findViewById(R.id.humtext);

        // Initialize temperature chart
        temperatureChart = findViewById(R.id.temperatureChart);
        setupBarChart(temperatureChart, "Temperature");

        // Initialize humidity chart
        humidityChart = findViewById(R.id.humidityChart);
        setupBarChart(humidityChart, "Humidity");

        // ChildEventListener to listen for data changes
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // New child node is added
                String temperature = dataSnapshot.child("Temperature").getValue(String.class);
                String humidity = dataSnapshot.child("Humidity").getValue(String.class);
                String smoke = dataSnapshot.child("Smoke").getValue(String.class);

                if (temperature != null && !temperature.isEmpty()) {
                    temptext.setText("Temperature:" + temperature);
                    updateBarChart(temperatureChart, "Temperature", Float.parseFloat(temperature));
                }

                if (humidity != null && !humidity.isEmpty()) {
                    humtext.setText("Humidity: " + humidity);
                    updateBarChart(humidityChart, "Humidity", Float.parseFloat(humidity));
                }

                if (smoke != null && smoke.equals("0")) {
                    showNotification();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Value of a child node is changed
                String temperature = dataSnapshot.child("Temperature").getValue(String.class);
                String humidity = dataSnapshot.child("Humidity").getValue(String.class);
                String smoke = dataSnapshot.child("Smoke").getValue(String.class);

                if (temperature != null && !temperature.isEmpty()) {
                    updateBarChart(temperatureChart, "Temperature", Float.parseFloat(temperature));
                    temptext.setText("Temperature: " + temperature);
                }

                if (humidity != null && !humidity.isEmpty()) {
                    updateBarChart(humidityChart, "Humidity", Float.parseFloat(humidity));
                    humtext.setText("Humidity: " + humidity);
                }

                if (smoke != null && smoke.equals("0")) {
                    showNotification();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // A child node is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // A child node is moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Database operation is cancelled
            }
        });

        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
    }

    // Method to setup BarChart with initial configurations
    private void setupBarChart(BarChart chart, String label) {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);

        // X-axis
        chart.getXAxis().setEnabled(false);

        // Y-axis
        chart.getAxisLeft().setEnabled(true);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisLeft().setTextColor(Color.BLACK);

        // Legend
        chart.getLegend().setEnabled(false);

        // Create an empty ArrayList of BarEntry
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 0f));

        // Create a BarDataSet with the entries and label
        BarDataSet dataSet = new BarDataSet(entries, label);

        // Set the BarDataSet color
        dataSet.setColor(Color.RED);

        // Create an ArrayList of IBarDataSet and add the BarDataSet to it
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        // Create a BarData with the dataSets
        BarData data = new BarData(dataSets);

        // Set the data to the chart
        chart.setData(data);

        // Refresh the chart
        chart.invalidate();
    }

    // Method to update BarChart with new data
    private void updateBarChart(BarChart chart, String label, float value) {
        BarData data = chart.getData();
        BarDataSet dataSet = (BarDataSet) data.getDataSetByIndex(0);

        // Update the BarDataSet with new value
        BarEntry entry = new BarEntry(dataSet.getEntryCount(), value);
        dataSet.addEntry(entry);

        // Notify the chart that the data has changed
        data.notifyDataChanged();
        chart.notifyDataSetChanged();

        // Scroll the chart to the end
        chart.moveViewToX(data.getEntryCount() - 7);

        // Refresh the chart
        chart.invalidate();
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationicon)
                .setContentTitle("Smoke Alert")
                .setContentText("Smoke value is abnormal!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "SmokeAlertChannel";
            String description = "Channel for Smoke Alert";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
