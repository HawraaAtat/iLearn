package com.example.elearningapp.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elearningapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AdminRegister extends AppCompatActivity {

    public static final String RIDER_USERS = "RidersUser";

    EditText et_email, et_password, et_confirmPassword, et_username;
    Button btn_Register;
    TextView tv_loginBtn;

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        btn_Register = findViewById(R.id.btn_register);
        tv_loginBtn = findViewById(R.id.tv_loginButton);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tv_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminRegister.this, AdminLogin.class));
            }
        });

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth();
            }
        });

    }

    private void PerformAuth() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String confirmPassword = et_confirmPassword.getText().toString();
        String username = et_username.getText().toString();

        if (email.isEmpty()) {
            et_email.setError("Please Enter Email");
            return;
        }
        if (!pat.matcher(email).matches()) {
            et_email.setError("Please Enter a valid Email");
            return;
        }
        if (password.isEmpty()) {
            et_password.setError("Please input Password");
            return;
        }
        if (password.length() < 6) {
            et_password.setError("Password too short");
            return;
        }
        if (!confirmPassword.equals(password)) {
            et_confirmPassword.setError("Password doesn't matches");
            return;
        }
        else {
            progressDialog.setMessage("Creating your Account....");
            progressDialog.setTitle("Creating");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

//            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//                    progressDialog.dismiss();
//
//                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                    String userId = firebaseUser.getUid();
//
//                    reference = FirebaseDatabase.getInstance().getReference().child(AdminRegister.RIDER_USERS).child(userId);
//                    HashMap<String, String> hashMap = new HashMap<>();
//                    hashMap.put("id", userId);
//                    hashMap.put("username", username);
//                    hashMap.put("imageUrl", "default");
//                    hashMap.put("search", username.toLowerCase());
//
//                    reference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            sendUserToMainActivity();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(AdminRegister.this, "Registration Failed", Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                    Toast.makeText(AdminRegister.this, "Registration Successful", Toast.LENGTH_LONG).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    progressDialog.dismiss();
//                    Toast.makeText(AdminRegister.this, "Registration Failed", Toast.LENGTH_LONG).show();
//                }
//            });
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.dismiss();

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child(AdminRegister.RIDER_USERS).child(userId);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", username);
                            hashMap.put("imageUrl", "default");
                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sendUserToMainActivity();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AdminRegister.this, "Registration Failed", Toast.LENGTH_LONG).show();
                                        }
                                    });

                            Toast.makeText(AdminRegister.this, "Registration Successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(AdminRegister.this, "Email already exists", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AdminRegister.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(AdminRegister.this, AdminDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish the current activity to prevent the user from coming back to the dashboard after logging out
        finish();
    }

}