package com.s22010536.mini_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class TeacherHomeActivity extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseAuth auth;
    private DatabaseReference tasksReference;

    private static final String TAG = "TeacherHomeActivity";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getActivity(), taskList); // Pass the activity here
        recyclerView.setAdapter(taskAdapter);

        auth = FirebaseAuth.getInstance();
        tasksReference = FirebaseDatabase.getInstance().getReference("Tasks");

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "User authenticated: " + user.getUid());
            fetchTasksByTeacher(user.getUid());
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User not authenticated");
        }

        return view;
    }

    private void fetchTasksByTeacher(String teacherId) {
        tasksReference.orderByChild("userId").equalTo(teacherId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(getContext(), "Failed to retrieve tasks", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to retrieve tasks: " + error.getMessage());
            }
        });
    }

    public void refreshTasks() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            fetchTasksByTeacher(user.getUid());
        }
    }
}
