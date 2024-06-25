// RegisterActivity.java

package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edittextFullName, editTextemail, editTextLocation, editTextSid, editTextPassword, editTextConfirmpassword;
    private Spinner programSpinner;
    private static final String TAG = "RegisterActivity";
    private ProgressBar progressBar;
    private static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Button regbtn = findViewById(R.id.signupbtn);
        Button locationbtn = findViewById(R.id.locationbtn);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setProgress(0);

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        edittextFullName = findViewById(R.id.name);
        editTextemail = findViewById(R.id.email);
        programSpinner = findViewById(R.id.program_spinner);
        editTextLocation = findViewById(R.id.location);
        editTextSid = findViewById(R.id.sid);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmpassword = findViewById(R.id.repassword);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.programs_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        programSpinner.setAdapter(adapter);

        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoc = new Intent(RegisterActivity.this, LocationActivity.class);
                startActivityForResult(intentLoc, LOCATION_REQUEST_CODE);
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtain entered data
                String textFullName = edittextFullName.getText().toString();
                String textEmail = editTextemail.getText().toString();
                String textProgram = programSpinner.getSelectedItem().toString();
                String textLocation = editTextLocation.getText().toString();
                String textSid = editTextSid.getText().toString();
                String textPassword = editTextPassword.getText().toString();
                String textConfpass = editTextConfirmpassword.getText().toString();

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter the name", Toast.LENGTH_LONG).show();
                    edittextFullName.setError("Full name is Required");
                    edittextFullName.requestFocus();

                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter the Email", Toast.LENGTH_LONG).show();
                    editTextemail.setError("Student Mail is Required");
                    editTextemail.requestFocus();

                } else if (TextUtils.isEmpty(textProgram)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter the degree program", Toast.LENGTH_LONG).show();
                    programSpinner.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-Enter the Email", Toast.LENGTH_LONG).show();
                    editTextemail.setError("Valid Email Required");
                    editTextemail.requestFocus();

                } else if (TextUtils.isEmpty(textLocation)) {
                    Toast.makeText(RegisterActivity.this, "Please Track location", Toast.LENGTH_LONG).show();
                    editTextLocation.setError("Location Required");
                    editTextLocation.requestFocus();

                } else if (TextUtils.isEmpty(textSid)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter the SID", Toast.LENGTH_LONG).show();
                    editTextSid.setError("SID Required");
                    editTextSid.requestFocus();

                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter the new Password", Toast.LENGTH_LONG).show();
                    editTextPassword.setError("New Password Required");
                    editTextPassword.requestFocus();

                } else if (TextUtils.isEmpty(textConfpass)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter the Password Again", Toast.LENGTH_LONG).show();
                    editTextConfirmpassword.setError("Please enter the same password as above");
                    editTextConfirmpassword.requestFocus();

                } else if (!textPassword.equals(textConfpass)) {
                    Toast.makeText(RegisterActivity.this, "Password should be matched", Toast.LENGTH_LONG).show();
                    editTextConfirmpassword.setError("Passwords should be matched");
                    editTextConfirmpassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textProgram, textLocation, textSid, textPassword);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String cityName = data.getStringExtra("city_name");
                if (cityName != null) {
                    editTextLocation.setText(cityName);
                }
            }
        }
    }

    private void registerUser(String textFullName, String textEmail, String textProgram, String textLocation, String textSid, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // Update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    // Enter User data into Firebase Realtime Database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textProgram, textLocation, textSid);

                    // Extracting user reference from database for "registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email verification
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "Registration successful, please verify your Email", Toast.LENGTH_LONG).show();

                                // Open home after registration
                                Intent homeIntent = new Intent(RegisterActivity.this, UserProfileActivity.class);

                                // To prevent user from entering register activity using back button
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(homeIntent);
                                finish(); // Close RegisterActivity
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed, please try again", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration not successful", Toast.LENGTH_LONG).show();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextPassword.setError("Your password is too weak");
                        editTextPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextemail.setError("Email already in use");
                        editTextemail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextemail.setError("User Already exists");
                        editTextemail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public EditText getEditTextLocation() {
        return editTextLocation;
    }

    public void setEditTextLocation(String text) {
        editTextLocation.setText(text);
    }
}
