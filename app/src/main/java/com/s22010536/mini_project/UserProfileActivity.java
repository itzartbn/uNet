package com.s22010536.mini_project;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewFullname, textViewEmail, textViewProgram, textViewSid, textViewLocation;
    private ProgressBar progressBar;
    private String fullName, email, sid, program, location;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        textViewWelcome = findViewById(R.id.showWelcome);
        textViewFullname = findViewById(R.id.profile_fullName);
        textViewEmail = findViewById(R.id.profile_email);
        textViewSid = findViewById(R.id.profile_sid);
        textViewProgram = findViewById(R.id.profile_program);
        textViewLocation = findViewById(R.id.profile_location);
        progressBar = findViewById(R.id.progressBar_profile);
        imageView = findViewById(R.id.profileDp);
        Toolbar toolBar = findViewById(R.id.toolbar);


        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Profile");



        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Oops!, Something went wrong", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }


    }



    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        //Extracting user reference from database for "registered users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    sid = readUserDetails.sid;
                    program = readUserDetails.program;
                    location = readUserDetails.location;

                    textViewWelcome.setText("Welcome " + fullName + "!");
                    textViewFullname.setText(fullName);
                    textViewEmail.setText(email);
                    textViewSid.setText(sid);
                    textViewProgram.setText(program);
                    textViewLocation.setText(location);

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

    //create actionbar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh){
            //refreash
            startActivity(getIntent());
            finish();
        }

        return super.onOptionsItemSelected(item);

    }
}