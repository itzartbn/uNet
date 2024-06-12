package com.s22010536.mini_project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {

    private EditText editTextLoginMail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private DatabaseReference databaseReference;
    private static final String TAG = "TeacherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        editTextLoginMail = findViewById(R.id.login_mail);
        editTextLoginPwd = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.loginProgress);

        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwdw);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwdw);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwdw);
                }
            }
        });

        Button btnreg = findViewById(R.id.newSignup);
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReg = new Intent(TeacherActivity.this, TeacherRegisterActivity.class);
                startActivity(intentReg);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        Button buttonForgotPassword = findViewById(R.id.login_forgotPassword);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TeacherActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeacherActivity.this, ForgotPasswordActivity.class));
            }
        });

        Button btnLogin = findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMail = editTextLoginMail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textMail)) {
                    Toast.makeText(TeacherActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextLoginMail.setError("Email Required");
                    editTextLoginMail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(TeacherActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password Required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textMail, textPwd);
                }
            }
        });
    }

    private void loginUser(String textMail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textMail, textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkUserRole(authProfile.getCurrentUser().getUid())





                    ;
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextLoginMail.setError("User does not exist");
                        editTextLoginMail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginMail.setError("Invalid Credentials");
                        editTextLoginMail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(TeacherActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void checkUserRole(String uid) {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("teacher".equals(role)) {
                        Intent loginToHome = new Intent(TeacherActivity.this, NotificationActivity.class);
                        startActivity(loginToHome);
                        Toast.makeText(TeacherActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TeacherActivity.this, "You are not authorized to log in as a teacher", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                } else {
                    Toast.makeText(TeacherActivity.this, "User role not found", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to retrieve user role", error.toException());
                Toast.makeText(TeacherActivity.this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
