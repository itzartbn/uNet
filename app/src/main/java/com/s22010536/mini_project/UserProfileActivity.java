package com.s22010536.mini_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewFullname, textViewEmail, textViewProgram, textViewSid, textViewLocation;
    private ProgressBar progressBar;
    private String fullName, email, sid, program, location;
    private ShapeableImageView profileDp;
    private FirebaseAuth authProfile;
    private ImageView updateProfileBtn, profileBackBtn;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        textViewFullname = findViewById(R.id.profile_fullName);
        textViewEmail = findViewById(R.id.profile_email);
        textViewSid = findViewById(R.id.profile_sid);
        textViewProgram = findViewById(R.id.profile_program);
        textViewLocation = findViewById(R.id.profile_location);
        progressBar = findViewById(R.id.progressBar_profile);
        profileDp = findViewById(R.id.profileDp);
        Button profileLogoutbtn = findViewById(R.id.profile_logout_btn);
        updateProfileBtn = findViewById(R.id.edit_button);
        profileBackBtn = findViewById(R.id.profile_back_button);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();


        //back button
        profileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Logout button
        profileLogoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authProfile.signOut();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Oops!, Something went wrong", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        profileDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadpicIntent = new Intent(UserProfileActivity.this, UploadProfilePictureActivity.class);
                startActivity(uploadpicIntent);
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    userRole = readUserDetails.role;
                    if ("teacher".equals(userRole)) {
                        sid = readUserDetails.tid;
                        program = readUserDetails.department;
                    } else {
                        sid = readUserDetails.sid;
                        program = readUserDetails.program;
                    }
                    location = readUserDetails.location;

                    textViewFullname.setText(fullName);
                    textViewEmail.setText(email);
                    textViewSid.setText(sid);
                    textViewProgram.setText(program);
                    textViewLocation.setText(location);

                    // Set profile picture
                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get()
                            .load(uri)
                            .placeholder(R.drawable.user_icon)
                            .into(profileDp, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // Handle success
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Handle error
                                    Toast.makeText(UserProfileActivity.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Set onClickListener for update profile button after fetching user details
                    updateProfileBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("teacher".equals(userRole)) {
                                Intent intent = new Intent(UserProfileActivity.this, TeacherUpdateProfileActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
