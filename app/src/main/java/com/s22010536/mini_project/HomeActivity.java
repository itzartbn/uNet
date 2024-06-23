package com.s22010536.mini_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseAuth auth;
    private DatabaseReference tasksReference;
    private DatabaseReference usersReference;

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        auth = FirebaseAuth.getInstance();
        tasksReference = FirebaseDatabase.getInstance().getReference("Tasks");
        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "User authenticated: " + user.getUid());
            fetchUserProgram(user.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User not authenticated");
        }
    }

    private void fetchUserProgram(String userId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String program = snapshot.child("program").getValue(String.class);
                    Log.d(TAG, "User program: " + program);
                    fetchTasksByDepartment(program);
                } else {
                    Toast.makeText(HomeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User data not found for ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to retrieve user data: " + error.getMessage());
            }
        });
    }

    private void fetchTasksByDepartment(String department) {
        tasksReference.orderByChild("department").equalTo(department).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    taskList.add(task);
                    Log.d(TAG, "Task added: " + task.getTaskTitle());
                }
                taskAdapter.notifyDataSetChanged();
                Log.d(TAG, "Tasks loaded, total count: " + taskList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to retrieve tasks", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to retrieve tasks: " + error.getMessage());
            }
        });
    }
}