package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;

public class NewPageActivity extends AppCompatActivity {

    private VideoView videoView; // VideoView object
    private ImageButton imageButton2; // Image button for opening a new page
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpage);
        imageButton2 = findViewById(R.id.reverse);
        // Set content description for accessibility
        imageButton2.setContentDescription("Open New Page");
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open NewPageActivity
                Intent intent = new Intent(NewPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Initialize VideoView object
        videoView = findViewById(R.id.videoView);

        // Set media controller for video playback controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Set video URI
        Uri videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/plantapp-bc512.appspot.com/o/output.mp4?alt=media");
        videoView.setVideoURI(videoUri);

        // Start video playback
        videoView.start();

        // Example code for displaying current time
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        String formattedDate = dateFormat.format(date);
        TextView textView = findViewById(R.id.textView3);
        textView.setText("Time: " + formattedDate);
    }


    // ... other code ...
}
