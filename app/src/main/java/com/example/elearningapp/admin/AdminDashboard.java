package com.example.elearningapp.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elearningapp.AdminCourseListAdapter;
import com.example.elearningapp.MainActivity;
import com.example.elearningapp.ModelCourse;
import com.example.elearningapp.R;
import com.example.elearningapp.tutor.AddCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDashboard extends AppCompatActivity {

    private ArrayList<ModelCourse> videoArrayList;
    private AdminCourseListAdapter adapterVideo;
    private RecyclerView videosRv;
    private TextView welcomeTextView;
    private ImageButton logoutButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

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
    }


    private void logoutUser() {
        progressDialog.show();
        FirebaseAuth.getInstance().signOut();
        sendUserToLoginActivity();
        progressDialog.dismiss();
        Toast.makeText(AdminDashboard.this, "Logout Successful", Toast.LENGTH_LONG).show();
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish the current activity to prevent the user from coming back to the dashboard after logging out
        finish();
    }

    private void loadCourseList() {
        videoArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(AddCourse.COURSES);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot : ds.getChildren()) {
                        ModelCourse modelVideo = dataSnapshot.getValue(ModelCourse.class);
                        videoArrayList.add(modelVideo);
                    }
                }
                adapterVideo = new AdminCourseListAdapter(AdminDashboard.this, videoArrayList);
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}