package com.example.elearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elearningapp.tutor.AddCourse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayCourse extends AppCompatActivity {

    private ArrayList<ModelVideo> videoArrayList;
    private VideoListAdapter adapterVideo;
    private RecyclerView videosRv;

    String tutor = "";
    String courseTitle = "";
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_course);

        videosRv = findViewById(R.id.courseRv);
        backBtn = findViewById(R.id.backBtn);

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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finish the current activity to navigate back
        finish();
    }


    private void loadCourseList() {
        videoArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(AddCourse.COURSES).child(tutor).child(courseTitle).child("videos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelVideo modelVideo = ds.getValue(ModelVideo.class);
                    videoArrayList.add(modelVideo);
                }
                adapterVideo = new VideoListAdapter(PlayCourse.this, videoArrayList);
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}