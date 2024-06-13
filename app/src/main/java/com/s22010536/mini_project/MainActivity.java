package com.s22010536.mini_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define buttons
        Button btnstudent = findViewById(R.id.student);
        Button btnteacher = findViewById(R.id.teacher);


        //open student login page
        btnstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        });

        //open teacher login page
        btnteacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logTeacher = new Intent(MainActivity.this, TeacherActivity.class);
                startActivity(logTeacher);
            }
        });










    }
}