// TaskListFragment.java
package com.s22010536.mini_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    public static final String TAG = "TaskListFragment";


    private ListView taskListView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private List<String> taskTitles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        taskListView = view.findViewById(R.id.task_list_view);
        taskTitles = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks");

        loadTasksByCurrentUser();

        return view;
    }

    public void loadTasksByCurrentUser() {
        Log.d(TAG, "Loading tasks by current user");
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.orderByChild("userId").equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            taskTitles.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String taskTitle = snapshot.child("taskTitle").getValue(String.class);
                                if (taskTitle != null) {
                                    taskTitles.add(taskTitle);
                                }
                            }
                            updateTaskList();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTaskList() {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) taskListView.getAdapter();
        if (adapter == null) {
            adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    taskTitles
            );
            taskListView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(taskTitles);
            adapter.notifyDataSetChanged();
        }
    }
}
