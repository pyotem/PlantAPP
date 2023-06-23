package com.example.firebase;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settingActivity extends AppCompatActivity {

    private Switch switch1, switch2, switch3, switch4, switch5, switch6;
    private TextView textView1, textView2, textView3, textView4, textView5, textView6;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("settings");

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);
        switch6 = findViewById(R.id.switch6);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);


        retrieveSwitchValue("sıcaklık", switch1);
        retrieveSwitchValue("topraknem", switch2);
        retrieveSwitchValue("yağmur", switch3);
        retrieveSwitchValue("duman", switch4);
        retrieveSwitchValue("sulama", switch5);
        retrieveSwitchValue("tüm", switch6);


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(settingActivity.this, "Switch 1 is ON", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("sıcaklık", 1);
                } else {
                    Toast.makeText(settingActivity.this, "Switch 1 is OFF", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("sıcaklık", 0);
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(settingActivity.this, "Switch 2 is ON", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("topraknem", 1);
                } else {
                    Toast.makeText(settingActivity.this, "Switch 2 is OFF", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("topraknem", 0);
                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(settingActivity.this, "Switch 3 is ON", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("yağmur", 1);
                } else {
                    Toast.makeText(settingActivity.this, "Switch 3 is OFF", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("yağmur", 0);
                }
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(settingActivity.this, "Switch 4 is ON", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("duman", 1);
                } else {
                    Toast.makeText(settingActivity.this, "Switch 4 is OFF", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("duman", 0);
                }
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(settingActivity.this, "Switch 5 is ON", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("sulama", 1);
                } else {
                    Toast.makeText(settingActivity.this, "Switch 5 is OFF", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("sulama", 0);
                }
            }
        });

        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(settingActivity.this, "Switch 6 is ON", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("tüm", 1);
                } else {
                    Toast.makeText(settingActivity.this, "Switch 6 is OFF", Toast.LENGTH_SHORT).show();
                    publishSwitchValue("tüm", 0);
                }
            }
        });
    }
    private void retrieveSwitchValue(String switchName, final Switch switchView) {
        databaseReference.child(switchName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        int switchValue = dataSnapshot.getValue(Integer.class);
                        switchView.setChecked(switchValue == 1);
                    }
                } else {
                    // Handle error while retrieving switch value
                }
            }
        });
    }
    private void publishSwitchValue(String switchName, int value) {
        databaseReference.child(switchName).setValue(value);
    }
}