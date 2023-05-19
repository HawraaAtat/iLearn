package com.example.elearningapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class PlayVideo extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTv;
    private ProgressBar progressBar;

    private String title = "";
    private String videoUrl = "";

    private FirebaseUser currentUser;
    private DatabaseReference userProgressRef;

    private int savedPosition = 0; // Variable to store the current position of the video
//    private String courseName = "";


    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = findViewById(R.id.videoView);
        titleTv = findViewById(R.id.titleTv);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        videoUrl = intent.getStringExtra("videoUrl");
//        courseName = intent.getStringExtra("courseTitle");


        titleTv.setText(title);

        MediaController mediaController = new MediaController(PlayVideo.this);
        mediaController.setAnchorView(videoView);
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBar.setVisibility(View.GONE); // hide the loader when video starts playing

                // Get the current user
                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Get the reference to the user's progress in the database
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    userProgressRef = FirebaseDatabase.getInstance().getReference()
                            .child("UserProgress")
                            .child(userId)
                            .child("videos")
                            .child(title);

                    userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Check if the "completed" value is present
                                if (dataSnapshot.hasChild("completed")) {
                                    Boolean isCompleted = dataSnapshot.child("completed").getValue(Boolean.class);
                                    // Check if the video has been marked as completed
                                    if (isCompleted != null && isCompleted) {
                                        // Handle the case when the video is already completed, e.g., show a completed state
                                    } else {
                                        // Retrieve the saved progress from Firebase
                                        Object progressValue = dataSnapshot.child("progress").getValue();
                                        if (progressValue instanceof Long) {
                                            long savedPosition = (Long) progressValue;
                                            videoView.seekTo((int) savedPosition);
                                        }
                                    }
                                } else {
                                    // Retrieve the saved progress from Firebase
                                    Object progressValue = dataSnapshot.getValue();
                                    if (progressValue instanceof Long) {
                                        long savedPosition = (Long) progressValue;
                                        videoView.seekTo((int) savedPosition);
                                    }
                                }
                            }

                            // Start video playback
                            mediaPlayer.start();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors that may occur
                        }
                    });
                } else {
                    // Start video playback
                    mediaPlayer.start();
                }
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        progressBar.setVisibility(View.GONE); // hide the loader when video starts rendering
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        progressBar.setVisibility(View.VISIBLE); // show the loader when video buffering starts
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        progressBar.setVisibility(View.GONE); // hide the loader when video buffering ends
                        return true;
                    }
                }
                return false;
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Add completion listener to the video view
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Mark the video as completed in Firebase
                markVideoAsCompleted();

                // Return to the previous activity
                finish();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Save the current position of the video
        savedPosition = videoView.getCurrentPosition();
        videoView.pause();

        // Track user progress when the activity is paused (e.g., user exits the activity)
        trackUserProgress(savedPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore the video position and resume playback
        videoView.seekTo(savedPosition);
        videoView.start();
    }

    private void trackUserProgress(int progress) {
        if (userProgressRef != null) {
            userProgressRef.child("progress").setValue(progress);
        }
    }

    private void markVideoAsCompleted() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference videoProgressRef = FirebaseDatabase.getInstance().getReference()
                    .child("UserProgress")
                    .child(userId)
                    .child("videos")
                    .child(title)
                    .child("completed");

            videoProgressRef.setValue(true); // Set the "completed" value as true to indicate completion
        }
    }
}