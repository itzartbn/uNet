// TaskAdapter.java
package com.s22010536.mini_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private FragmentActivity activity;

    public TaskAdapter(FragmentActivity activity, List<Task> taskList) {
        this.activity = activity;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyle_view_row, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTaskTitle());
        holder.taskDateTime.setText(task.getTaskDateTime());

        holder.teacherImage.setImageResource(R.drawable.default_dp);

        holder.itemView.setOnClickListener(v -> {
            TaskDetailDialogFragment dialogFragment = TaskDetailDialogFragment.newInstance(
                    task.getTaskId(), // Pass taskId
                    task.getTaskTitle(),
                    task.getTaskDescription(),
                    task.getTaskDateTime()
            );
            dialogFragment.show(activity.getSupportFragmentManager(), "TaskDetailDialogFragment");
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDateTime;
        ImageView teacherImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.textView_task_card);
            taskDateTime = itemView.findViewById(R.id.textView_time_card);
            teacherImage = itemView.findViewById(R.id.imageView_teacher_pp_card);
        }
    }
}
