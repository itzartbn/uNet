package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextpwdcurr, editTextpwdNew, editTextConfNewPwd;
    private TextView textViewAuthenticated;
    private Button buttonChangepwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        editTextpwdNew = findViewById(R.id.editText_new_pw);
        editTextpwdcurr =  findViewById(R.id.editText_old_password);
        editTextConfNewPwd = findViewById(R.id.editText_confnew_pw);
        textViewAuthenticated = findViewById(R.id.textView_pw_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.btn_authenticate_pw);
        buttonChangepwd = findViewById(R.id.password_update_btn);

        //disable new passswords slots and update password btn
        editTextpwdNew.setEnabled(false);
        editTextConfNewPwd.setEnabled(false);
        buttonChangepwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this, "Sowmthing went wrong!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);

        }


    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editTextpwdcurr.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextpwdcurr.setError("please enter your current password to authenticate");
                    editTextpwdcurr.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    //Reauthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //disable edit text for current password
                                editTextpwdcurr.setEnabled(false);
                                editTextpwdNew.setEnabled(true);
                                editTextConfNewPwd.setEnabled(true);
                                //disable authenticate button and enable change password button
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangepwd.setEnabled(true);

                                //set textview to show that user is authenticated
                                textViewAuthenticated.setText("you are authenticated! Now you can change the password");

                                buttonChangepwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });


                            } else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextpwdNew.getText().toString();
        String userPwdConfNew = editTextConfNewPwd.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this, "New password Required", Toast.LENGTH_SHORT).show();
            editTextpwdNew.setError("Please enter your new Password");
            editTextpwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            editTextConfNewPwd.setError("Please re-enter your new Password");
            editTextConfNewPwd.requestFocus();
            
        } else if (!userPwdNew.matches(userPwdConfNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Passwords did not match", Toast.LENGTH_SHORT).show();
            editTextConfNewPwd.setError("Please re-enter same Password");
            editTextConfNewPwd.requestFocus();
            
        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New password cannot be same as old", Toast.LENGTH_SHORT).show();
            editTextConfNewPwd.setError("Please enter a new password");
            editTextConfNewPwd.requestFocus();


        }else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }
}