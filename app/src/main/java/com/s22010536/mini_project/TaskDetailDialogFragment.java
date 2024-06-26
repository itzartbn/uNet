// TaskDetailDialogFragment.java
package com.s22010536.mini_project;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetailDialogFragment extends DialogFragment {

    private static final String ARG_TASK_ID = "taskId";
    private static final String ARG_TASK_TITLE = "taskTitle";
    private static final String ARG_TASK_DESCRIPTION = "taskDescription";
    private static final String ARG_TASK_DURATION = "taskDuration";

    private ImageView taskDeleteButton;
    private FirebaseAuth auth;
    private DatabaseReference tasksReference;

    public static TaskDetailDialogFragment newInstance(String taskId, String taskTitle, String taskDescription, String taskDuration) {
        TaskDetailDialogFragment fragment = new TaskDetailDialogFragment();
        Bundle args = new Bundle();
        // Pass taskId, taskTitle, taskDescription, and taskDuration to the fragment
        args.putString(ARG_TASK_ID, taskId);
        args.putString(ARG_TASK_TITLE, taskTitle);
        args.putString(ARG_TASK_DESCRIPTION, taskDescription);
        args.putString(ARG_TASK_DURATION, taskDuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set transparent background for the dialog
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = inflater.inflate(R.layout.fragment_task_detail_dialog, container, false);
        TextView taskTitle = view.findViewById(R.id.textView_task_title);
        TextView taskDescription = view.findViewById(R.id.textView_task_description);
        TextView taskDuration = view.findViewById(R.id.textView_task_duration);
        taskDeleteButton = view.findViewById(R.id.task_delete);

        if (getArguments() != null) {
            taskTitle.setText(getArguments().getString(ARG_TASK_TITLE));
            taskDescription.setText(getArguments().getString(ARG_TASK_DESCRIPTION));
            taskDuration.setText(getArguments().getString(ARG_TASK_DURATION));
        }

        auth = FirebaseAuth.getInstance();
        tasksReference = FirebaseDatabase.getInstance().getReference("Tasks");

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            checkUserRole(user.getUid());
        }

        taskDeleteButton.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String taskId = getArguments().getString(ARG_TASK_ID);
                deleteTask(taskId, currentUser.getUid());
            }
        });

        return view;
    }

    private void checkUserRole(String userId) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //hide deletebutton for students and show for teachers
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("teacher".equals(role)) {
                        taskDeleteButton.setVisibility(View.VISIBLE);
                    } else {
                        taskDeleteButton.setVisibility(View.GONE);
                    }
                } else {
                    taskDeleteButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskDeleteButton.setVisibility(View.GONE);
            }
        });
    }

    private void deleteTask(String taskId, String userId) {
        tasksReference.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String taskUserId = snapshot.child("userId").getValue(String.class);
                    if (userId.equals(taskUserId)) {
                        tasksReference.child(taskId).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                dismiss();
                                FragmentActivity activity = getActivity();
                                if (activity instanceof TeacherCommonActivity) {
                                    ((TeacherCommonActivity) activity).onTaskAddedToList();
                                }
                            } else {
                                Toast.makeText(getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "You are not authorized to delete this task", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Task not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
