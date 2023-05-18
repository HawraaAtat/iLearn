package com.example.elearningapp.tutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elearningapp.CourseListAdapter;
import com.example.elearningapp.MainActivity;
import com.example.elearningapp.ModelCourse;
import com.example.elearningapp.R;
import com.example.elearningapp.admin.AdminRegister;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherDashboard extends AppCompatActivity {
    FloatingActionButton addVideoBtn;

    private ArrayList<ModelCourse> videoArrayList;
    private CourseListAdapter adapterVideo;
    private RecyclerView videosRv;

    private TextView welcomeTextView;
    private ImageButton logoutButton;
    private ProgressDialog progressDialog;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        addVideoBtn = findViewById(R.id.addVideoBtn);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        // Get the current user ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userId = ((FirebaseUser) mUser).getUid();

        // Get the username from the database based on the user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(AdminRegister.RIDER_USERS).child(userId);

        welcomeTextView= findViewById(R.id.tv_username);
        logoutButton = findViewById(R.id.btn_logout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out from your Account....");
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String search = dataSnapshot.child("search").getValue(String.class);

                    // update the UI with the username
                    welcomeTextView.setText("Welcome, " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        videosRv = findViewById(R.id.courseRv);
        loadCourseList();

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherDashboard.this, AddCourse.class).putExtra("username", username));
            }
        });

        videosRv = findViewById(R.id.courseRv);

        loadCourseList();
    }



    private void logoutUser() {
        progressDialog.show();
        FirebaseAuth.getInstance().signOut();
        sendUserToLoginActivity();
        progressDialog.dismiss();
        Toast.makeText(TeacherDashboard.this, "Logout Successful", Toast.LENGTH_LONG).show();
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(TeacherDashboard.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish the current activity to prevent the user from coming back to the dashboard after logging out
        finish();
    }
    private void loadCourseList() {
        videoArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(AddCourse.COURSES).child(username);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                videoArrayList.clear();


                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCourse modelVideo = ds.getValue(ModelCourse.class);
                    videoArrayList.add(modelVideo);

                    // Log the course details
                    Log.d("Course", "Course Name: " + modelVideo.getTitle());
                    Log.d("Course", "Course Description: " + modelVideo.getCategory());

                }
                adapterVideo = new CourseListAdapter(TeacherDashboard.this, videoArrayList);
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}