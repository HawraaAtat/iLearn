package com.example.elearningapp.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elearningapp.CourseListAdapter;
import com.example.elearningapp.ModelCourse;
import com.example.elearningapp.R;
import com.example.elearningapp.admin.AdminRegister;
import com.example.elearningapp.tutor.AddCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentDashboard extends AppCompatActivity {

    private ArrayList<ModelCourse> videoArrayList;
    private CourseListAdapter adapterVideo;
    private RecyclerView videosRv;
    private TextView welcomeTextView;
    String category = "";
    private ImageButton backBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        videosRv = findViewById(R.id.courseRv);
        backBtn = findViewById(R.id.backBtn);

        Intent intent = getIntent();
        category = intent.getStringExtra(StudentRealDashboard.CATEGORY);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userId = ((FirebaseUser) mUser).getUid();

        // Get the username from the database based on the user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(AdminRegister.RIDER_USERS).child(userId);


        welcomeTextView= findViewById(R.id.tv_username);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);

                    // update the UI with the username
                    welcomeTextView.setText("Welcome, " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur
            }
        });

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(AddCourse.COURSES);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot : ds.getChildren()) {
                        ModelCourse modelVideo = dataSnapshot.getValue(ModelCourse.class);
                        if (modelVideo.getCategory().equals(category)) {
                            videoArrayList.add(modelVideo);
                        }
                    }
                }
                adapterVideo = new CourseListAdapter(StudentDashboard.this, videoArrayList);
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}