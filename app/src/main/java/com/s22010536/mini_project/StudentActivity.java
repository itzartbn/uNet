package com.s22010536.mini_project;

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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class StudentActivity extends AppCompatActivity {

    private EditText editTextLoginMail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "StudentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_ativity);

        Button btnreg = findViewById(R.id.newSignup);
        editTextLoginMail = findViewById(R.id.login_mail);
        editTextLoginPwd = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.loginProgress);

        //password hiding eye
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.closedeye);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if visible then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.closedeye);

                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.openedeye);
                }
            }
        });


        //Directiong to sign in page
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReg = new Intent(StudentActivity.this, RegisterActivity.class);
                startActivity(intentReg);
            }
        });
        //avoid logged user login again
        authProfile = FirebaseAuth.getInstance();

        //forgot password button
        Button buttonForgotPassword = findViewById(R.id.login_forgotPassword);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentActivity.this, "you can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentActivity.this, ForgotPasswordActivity.class));
            }
        });

        //login
        Button btnLogin = findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMail = editTextLoginMail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textMail)){
                    Toast.makeText(StudentActivity.this,"Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextLoginMail.setError("Email Required");
                    editTextLoginMail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(StudentActivity.this,"Please enter the password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password Required");
                    editTextLoginPwd.requestFocus();

                }else {
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
                if(task.isSuccessful()){
                    Intent loginToHome = new Intent(StudentActivity.this, UserProfileActivity.class);
                    startActivity(loginToHome);
                    Toast.makeText(StudentActivity.this,"Login Successful", Toast.LENGTH_SHORT).show();


                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        editTextLoginMail.setError("User does not exists");
                        editTextLoginMail.requestFocus();

                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginMail.setError("Invalid Credentials");
                        editTextLoginMail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(StudentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    /* //Check if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(StudentAtivity.this, "You are already logged in", Toast.LENGTH_SHORT).show();
            Intent loginToHome = new Intent(StudentAtivity.this, HomeActivity.class);
            startActivity(loginToHome);
        }else{
            Toast.makeText(StudentAtivity.this, "You can log in now", Toast.LENGTH_SHORT).show();

        }
    }*/
}