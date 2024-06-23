// TaskDetailDialogFragment.java
package com.s22010536.mini_project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TaskDetailDialogFragment extends DialogFragment {

    private static final String ARG_TASK_TITLE = "taskTitle";
    private static final String ARG_TASK_DESCRIPTION = "taskDescription";
    private static final String ARG_TASK_DURATION = "taskDuration";

    public static TaskDetailDialogFragment newInstance(String taskTitle, String taskDescription, String taskDuration) {
        TaskDetailDialogFragment fragment = new TaskDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASK_TITLE, taskTitle);
        args.putString(ARG_TASK_DESCRIPTION, taskDescription);
        args.putString(ARG_TASK_DURATION, taskDuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail_dialog, container, false);
        TextView taskTitle = view.findViewById(R.id.textView_task_title);
        TextView taskDescription = view.findViewById(R.id.textView_task_description);
        TextView taskDuration = view.findViewById(R.id.textView_task_duration);

        if (getArguments() != null) {
            taskTitle.setText(getArguments().getString(ARG_TASK_TITLE));
            taskDescription.setText(getArguments().getString(ARG_TASK_DESCRIPTION));
            taskDuration.setText(getArguments().getString(ARG_TASK_DURATION));
        }

        return view;
    }
}
