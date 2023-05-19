package com.example.elearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elearningapp.tutor.AddCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;



public class PlayCourse extends AppCompatActivity {

    private ArrayList<ModelVideo> videoArrayList;
    private VideoListAdapter adapterVideo;
    private RecyclerView videosRv;

    String tutor = "";
    String courseTitle = "";
    private ImageButton backBtn;
    private TextView progressTextView;
    private FirebaseUser currentUser;
    private HashMap<String, Boolean> completionStatusMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_course);

        videosRv = findViewById(R.id.courseRv);
        backBtn = findViewById(R.id.backBtn);
        progressTextView = findViewById(R.id.progressTextView);

        Intent intent = getIntent();
        tutor = intent.getStringExtra("tutor");
        courseTitle = intent.getStringExtra("courseTitle");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadCourseList();
        calculateProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the course list and recalculate progress
        loadCourseList();
        calculateProgress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finish the current activity to navigate back
        finish();
    }

    private void loadCourseList() {
        videoArrayList = new ArrayList<>(); // Initialize the videoArrayList

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(AddCourse.COURSES).child(tutor).child(courseTitle).child("videos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoArrayList.clear(); // Clear the videoArrayList before adding videos

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelVideo modelVideo = ds.getValue(ModelVideo.class);
                    videoArrayList.add(modelVideo);
                }

                adapterVideo = new VideoListAdapter(PlayCourse.this, videoArrayList, completionStatusMap); // Initialize the adapterVideo
                videosRv.setAdapter(adapterVideo); // Set the adapter to the RecyclerView

                calculateProgress(); // Calculate the progress after loading the course list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PlayCourse", "Failed to load course list: " + error.getMessage());
            }
        });
    }






    private void calculateProgress() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        completionStatusMap = new HashMap<>();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference progressRef = FirebaseDatabase.getInstance().getReference()
                    .child("UserProgress")
                    .child(userId)
                    .child("videos");

            progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Clear the completionStatusMap before populating it again
                    completionStatusMap.clear();

                    int totalVideos = videoArrayList.size();
                    int completedVideos = 0;

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String videoTitle = ds.getKey();
                        Boolean isCompleted = ds.child("completed").getValue(Boolean.class);

                        if (isCompleted != null && isCompleted) {
                            completionStatusMap.put(videoTitle, true);
                            completedVideos++;
                        } else {
                            completionStatusMap.put(videoTitle, false);
                        }
                    }


                    int progress = (int) (((float) completedVideos / totalVideos) * 100);
                    progressTextView.setText("Progress: " + progress + "%");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("PlayCourse", "Failed to calculate progress: " + error.getMessage());
                }
            });
        } else {
            progressTextView.setText("Progress: 10%");
        }
    }

    private void updateProgressUI(int progress) {
        progressTextView.setText(progress + "%");
    }



}