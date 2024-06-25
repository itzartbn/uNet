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

import java.util.HashMap;
import java.util.Map;

public class TeacherRegisterActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextLocation, editTextEmployeeId, editTextPassword, editTextConfirmPassword;
    private Spinner spinnerDepartment;
    private static final String TAG = "TeacherRegisterActivity";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_register);

        Button regBtn = findViewById(R.id.signupbtn);
        progressBar = findViewById(R.id.progressBar);
        Button teacherLocationBtn = findViewById(R.id.tecLocationbtn);

        editTextFullName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        spinnerDepartment = findViewById(R.id.department_spinner);
        editTextLocation = findViewById(R.id.location);
        editTextEmployeeId = findViewById(R.id.eid);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.repassword);

        // Populate the spinner with departments
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.departments_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(adapter);

        teacherLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoc = new Intent(TeacherRegisterActivity.this, LocationActivity.class);
                startActivity(intentLoc);
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textFullName = editTextFullName.getText().toString();
                String textEmail = editTextEmail.getText().toString();
                String textDepartment = spinnerDepartment.getSelectedItem().toString();
                String textLocation = editTextLocation.getText().toString();
                String textEmployeeId = editTextEmployeeId.getText().toString();
                String textPassword = editTextPassword.getText().toString();
                String textConfirmPassword = editTextConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the name", Toast.LENGTH_LONG).show();
                    editTextFullName.setError("Full name is Required");
                    editTextFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the Email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Email is Required");
                    editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDepartment)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the department", Toast.LENGTH_LONG).show();
                    spinnerDepartment.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please re-Enter the Email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Valid Email Required");
                    editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(textLocation)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the location", Toast.LENGTH_LONG).show();
                    editTextLocation.setError("Location is Required");
                    editTextLocation.requestFocus();
                } else if (TextUtils.isEmpty(textEmployeeId)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the Employee ID", Toast.LENGTH_LONG).show();
                    editTextEmployeeId.setError("Employee ID is Required");
                    editTextEmployeeId.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the Password", Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Password is Required");
                    editTextPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Please Enter the Password Again", Toast.LENGTH_LONG).show();
                    editTextConfirmPassword.setError("Password Confirmation is Required");
                    editTextConfirmPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(TeacherRegisterActivity.this, "Passwords should match", Toast.LENGTH_LONG).show();
                    editTextConfirmPassword.setError("Passwords should match");
                    editTextConfirmPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerTeacher(textFullName, textEmail, textDepartment, textLocation, textEmployeeId, textPassword);
                }
            }
        });
    }

    private void registerTeacher(String textFullName, String textEmail, String textDepartment, String textLocation, String textEmployeeId, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(TeacherRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // Update display name
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    // Save user details to Realtime Database
                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("fullName", textFullName);
                    userDetails.put("department", textDepartment);
                    userDetails.put("location", textLocation);
                    userDetails.put("tid", textEmployeeId);
                    userDetails.put("role", "teacher");

                    databaseReference.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(TeacherRegisterActivity.this, "Registration successful, please verify your email", Toast.LENGTH_LONG).show();

                                Intent homeIntent = new Intent(TeacherRegisterActivity.this, UserProfileActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(homeIntent);
                                finish();
                            } else {
                                Toast.makeText(TeacherRegisterActivity.this, "Registration failed, please try again", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextPassword.setError("Your password is too weak");
                        editTextPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextEmail.setError("Invalid credentials");
                        editTextEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextEmail.setError("User already exists");
                        editTextEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(TeacherRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
