package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class UpdateProfileActivity extends AppCompatActivity {

    private TextView editTextUpdatename, editTextUpdateProgram, editTextUpdateSid, editTextUpdateLocation;
    private String textName, textProgram, textSid, textLocation ;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

        progressBar = findViewById(R.id.progressBar_profile);
        editTextUpdatename = findViewById(R.id.editText_update_name);
        editTextUpdateProgram = findViewById(R.id.editText_update_program);
        editTextUpdateSid = findViewById(R.id.editText_update_sid);
        editTextUpdateLocation = findViewById(R.id.editText_update_location);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();


        //show profile data
        showProfile(firebaseUser);

        Button btnUpdateLocation = findViewById(R.id.updateProfile_map_button);
        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoc = new Intent(UpdateProfileActivity.this, LocationActivity.class);
                startActivityForResult(intentLoc, LOCATION_REQUEST_CODE);
            }
        });









        //Update profile on click
        Button buttonUpdateProfile = findViewById(R.id.updateProfile_btn);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProfile(firebaseUser);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {

        //obtain data entered by user
        textName = editTextUpdatename.getText().toString();
        textProgram = editTextUpdateProgram.getText().toString();
        textLocation = editTextUpdateLocation.getText().toString();
        textSid = editTextUpdateSid.getText().toString();


        if (TextUtils.isEmpty(textName)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter the name", Toast.LENGTH_LONG).show();
            editTextUpdatename.setError("Full name is Required");
            editTextUpdatename.requestFocus();

        } else if (TextUtils.isEmpty(textProgram)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter the degree program", Toast.LENGTH_LONG).show();
            editTextUpdateProgram.setError("Degree program name required");
            editTextUpdateProgram.requestFocus();

        }else if (TextUtils.isEmpty(textLocation)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Track location", Toast.LENGTH_LONG).show();
            editTextUpdateLocation.setError("Location Required");
            editTextUpdateLocation.requestFocus();

        }else if (TextUtils.isEmpty(textSid)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter the SID", Toast.LENGTH_LONG).show();
            editTextUpdateSid.setError("SID Required");
            editTextUpdateSid.requestFocus();

        } else {

            //enter user details to firebase
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textName, textProgram, textSid, textLocation);

            //Extract user reference from databse for "Registered users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //setting new display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(UpdateProfileActivity.this, "Profile Updated!", Toast.LENGTH_LONG).show();

                        //stop user from entering update page by pressing back button
                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String cityName = data.getStringExtra("city_name");
                if (cityName != null) {
                    editTextUpdateLocation.setText(cityName);
                }
            }
        }
    }


    //fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting user reference from databse for "registered Users"
        DatabaseReference refernceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);

        refernceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetailas = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetailas != null){
                    textName = firebaseUser.getDisplayName();
                    textProgram = readUserDetailas.program;
                    textSid = readUserDetailas.sid;
                    textLocation = readUserDetailas.location;

                    editTextUpdatename.setText(textName);
                    editTextUpdateProgram.setText(textProgram);
                    editTextUpdateSid.setText(textSid);
                    editTextUpdateLocation.setText(textLocation);


                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "seomething went wrong",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        });

    }
}