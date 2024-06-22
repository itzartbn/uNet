package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpadateEmail;
    private EditText editTextNewMail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_email);

        progressBar = findViewById(R.id.progressBar_profile);
        editTextPwd = findViewById(R.id.editText_mail_verify_password);
        editTextNewMail = findViewById(R.id.editText_new_mail);
        textViewAuthenticated = findViewById(R.id.textView_update_mail_authenticated);
        buttonUpadateEmail = findViewById(R.id.mail_update_btn);

        buttonUpadateEmail.setEnabled(false); //update button disabled untill user authenticate themselves
        editTextNewMail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //set old email on textview
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_old_mail);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "Something Went Wrong! User not found",Toast.LENGTH_LONG).show();
        }else {
            reAuthenticate(firebaseUser);
        }
    }
// Reauthenticate user before change email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.btn_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtain password for authentication
                userPwd = editTextPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Password Cannot be empty");
                    editTextPwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified", Toast.LENGTH_LONG).show();

                                //set the textview once user authenticated
                                textViewAuthenticated.setText("You are authenticated. you can update your email now");

                                buttonUpadateEmail.setEnabled(true); //update button enabled
                                editTextNewMail.setEnabled(true);
                                buttonVerifyUser.setEnabled(false);
                                editTextPwd.setEnabled(false);

                                buttonUpadateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editTextNewMail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this, "Please Enter the new email", Toast.LENGTH_SHORT).show();
                                            editTextNewMail.setError("New Email Requred");
                                            editTextNewMail.requestFocus();

                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                                            editTextNewMail.setError("Valid Email Required");
                                            editTextNewMail.requestFocus();


                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New email shouldnt be same as Old email", Toast.LENGTH_SHORT).show();
                                            editTextNewMail.setError("New Email Requred");
                                            editTextNewMail.requestFocus();

                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            }else{
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }


                        }
                    });
                }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {

        firebaseUser.verifyBeforeUpdateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(UpdateEmailActivity.this, "Email Updated!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}