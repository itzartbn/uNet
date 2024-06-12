// TaskFormFragment.java
package com.s22010536.mini_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TaskFormFragment extends Fragment {

    private EditText taskTitle;
    private EditText taskDuration;
    private EditText taskDescription;
    private Button sendTaskButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private OnTaskAdded taskAddedListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            taskAddedListener = (OnTaskAdded) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTaskAddedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        taskAddedListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_form, container, false);

        taskTitle = view.findViewById(R.id.task_title);
        taskDuration = view.findViewById(R.id.task_duration);
        taskDescription = view.findViewById(R.id.task_description);
        sendTaskButton = view.findViewById(R.id.send_task_button);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks");

        taskDuration.setOnClickListener(v -> showDateTimePicker());

        sendTaskButton.setOnClickListener(v -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                createTask(userId);
            } else {
                Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (timeView, hourOfDay, minute) -> taskDuration.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " " + hourOfDay + ":" + minute),
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void createTask(String userId) {
        String title = taskTitle.getText().toString();
        String duration = taskDuration.getText().toString();
        String description = taskDescription.getText().toString();

        if (title.isEmpty() || duration.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = databaseReference.push().getKey();
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("taskId", taskId);
        taskData.put("taskTitle", title);
        taskData.put("taskDateTime", duration);
        taskData.put("taskDescription", description);
        taskData.put("userId", userId);

        if (taskId != null) {
            databaseReference.child(taskId).setValue(taskData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Task created successfully", Toast.LENGTH_SHORT).show();
                            taskAddedListener.onTaskAddedToList();
                        } else {
                            Toast.makeText(getContext(), "Failed to create task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
