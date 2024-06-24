// TeacherUpdateProfileActivity.java
package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherUpdateProfileActivity extends AppCompatActivity {

    private TextView editTextUpdateName, editTextUpdateDepartment, editTextUpdateTid, editTextUpdateLocation;
    private String textName, textDepartment, textTid, textLocation;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_update_profile);

        progressBar = findViewById(R.id.progressBar_profile);
        editTextUpdateName = findViewById(R.id.editText_update_name);
        editTextUpdateDepartment = findViewById(R.id.editText_update_program);
        editTextUpdateTid = findViewById(R.id.editText_update_sid);
        editTextUpdateLocation = findViewById(R.id.editText_update_location);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // Show profile data
        showProfile(firebaseUser);

        // Update profile on click
        Button buttonUpdateProfile = findViewById(R.id.updateProfile_btn);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        // Obtain data entered by user
        textName = editTextUpdateName.getText().toString();
        textDepartment = editTextUpdateDepartment.getText().toString();
        textLocation = editTextUpdateLocation.getText().toString();
        textTid = editTextUpdateTid.getText().toString();

        if (TextUtils.isEmpty(textName)) {
            Toast.makeText(TeacherUpdateProfileActivity.this, "Please Enter the name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is Required");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textDepartment)) {
            Toast.makeText(TeacherUpdateProfileActivity.this, "Please Enter the department", Toast.LENGTH_LONG).show();
            editTextUpdateDepartment.setError("Department name required");
            editTextUpdateDepartment.requestFocus();
        } else if (TextUtils.isEmpty(textLocation)) {
            Toast.makeText(TeacherUpdateProfileActivity.this, "Please Enter the location", Toast.LENGTH_LONG).show();
            editTextUpdateLocation.setError("Location Required");
            editTextUpdateLocation.requestFocus();
        } else if (TextUtils.isEmpty(textTid)) {
            Toast.makeText(TeacherUpdateProfileActivity.this, "Please Enter the TID", Toast.LENGTH_LONG).show();
            editTextUpdateTid.setError("TID Required");
            editTextUpdateTid.requestFocus();
        } else {
            // Enter user details to firebase
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textName, textDepartment, textLocation, textTid, "teacher");

            // Extract user reference from database for "Registered users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Setting new display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(TeacherUpdateProfileActivity.this, "Profile Updated!", Toast.LENGTH_LONG).show();

                        // Stop user from entering update page by pressing back button
                        Intent intent = new Intent(TeacherUpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(TeacherUpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    // Fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        // Extracting user reference from database for "registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    textName = firebaseUser.getDisplayName();
                    textDepartment = readUserDetails.department;
                    textTid = readUserDetails.tid;
                    textLocation = readUserDetails.location;

                    editTextUpdateName.setText(textName);
                    editTextUpdateDepartment.setText(textDepartment);
                    editTextUpdateTid.setText(textTid);
                    editTextUpdateLocation.setText(textLocation);

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherUpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
