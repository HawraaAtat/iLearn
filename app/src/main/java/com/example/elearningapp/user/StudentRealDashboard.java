package com.example.elearningapp.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.elearningapp.MainActivity;
import com.example.elearningapp.R;
import com.example.elearningapp.admin.AdminRegister;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentRealDashboard extends AppCompatActivity {

    CardView cv_web, cv_frontend, cv_backend, cv_database, cv_android, cv_machineLearning;
    String[] courses = {"Web", "Frontend", "Backend", "Database", "Android", "Machine Learning"};
    private ImageButton logoutButton;
    private ProgressDialog progressDialog;

    public static final String CATEGORY = "category";
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_real_dashboard);


        cv_web = findViewById(R.id.cv_web);
        cv_frontend = findViewById(R.id.cv_frontend);
        cv_backend = findViewById(R.id.cv_backend);
        cv_database = findViewById(R.id.cv_database);
        cv_android = findViewById(R.id.cv_android);
        cv_machineLearning = findViewById(R.id.cv_machineLearning);

        logoutButton = findViewById(R.id.btn_logout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out from your Account....");
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);


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


        cv_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentRealDashboard.this, StudentDashboard.class).putExtra(CATEGORY, courses[0]));
            }
        });
        cv_frontend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentRealDashboard.this, StudentDashboard.class).putExtra(CATEGORY, courses[1]));
            }
        });
        cv_backend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentRealDashboard.this, StudentDashboard.class).putExtra(CATEGORY, courses[2]));
            }
        });
        cv_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentRealDashboard.this, StudentDashboard.class).putExtra(CATEGORY, courses[3]));
            }
        });
        cv_android.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentRealDashboard.this, StudentDashboard.class).putExtra(CATEGORY, courses[4]));
            }
        });
        cv_machineLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentRealDashboard.this, StudentDashboard.class).putExtra(CATEGORY, courses[5]));
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }


    private void logoutUser() {
        progressDialog.show();
        FirebaseAuth.getInstance().signOut();
        sendUserToLoginActivity();
        progressDialog.dismiss();
        Toast.makeText(StudentRealDashboard.this, "Logout Successful", Toast.LENGTH_LONG).show();
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(StudentRealDashboard.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish the current activity to prevent the user from coming back to the dashboard after logging out
        finish();
    }

}