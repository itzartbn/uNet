// HomeActivity.java
package com.s22010536.mini_project;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
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
import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseAuth auth;
    private DatabaseReference tasksReference;
    private DatabaseReference usersReference;

    private static final String TAG = "HomeFragment";

    private static final long FETCH_INTERVAL_MS = 3 * 1000; // Fetch interval: 3 seconds
    private Handler handler;
    private Runnable fetchRunnable;

    // Other existing member variables
    private Set<String> fetchedTaskIds; // To store IDs of fetched tasks


    @Override
    public void onResume() {
        super.onResume();
        // Start periodic task fetching when the fragment is resumed
        handler.post(fetchRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop periodic task fetching when the fragment is paused
        handler.removeCallbacks(fetchRunnable);
    }
    // Fetch tasks from the database
    private void fetchTasks() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            fetchUserProgram(user.getUid());
        } else {
            Log.d(TAG, "User not authenticated");
        }
    }


    private void showNotification(Task task) {
        // Build notification
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        // Create a unique notification channel ID and name
        String channelId = "task_notification_channel";
        CharSequence channelName = "Task Notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(R.drawable.default_dp)
                .setContentTitle(task.getTaskTitle())
                .setContentText(task.getTaskDescription())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Generate a unique ID for this notification
        int notificationId = generateNotificationId(task.getTaskId());

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }

    // Generate a unique notification ID based on the task ID
    private int generateNotificationId(String taskId) {
        //generate a unique ID
        return taskId.hashCode();
    }
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
        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "User authenticated: " + user.getUid());
            fetchUserProgram(user.getUid());
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User not authenticated");
        }

        handler = new Handler();
        fetchRunnable = new Runnable() {
            @Override
            public void run() {
                fetchTasks();
                handler.postDelayed(this, FETCH_INTERVAL_MS);
            }
        };

        // Initialize fetched task IDs set
        fetchedTaskIds = new HashSet<>();

        return view;

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
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User data not found for ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to retrieve user data: " + error.getMessage());
            }
        });
    }

    private void fetchTasksByDepartment(String department) {
        tasksReference.orderByChild("department").equalTo(department).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> newTasks = new ArrayList<>();

                // Clear the existing task list to avoid duplicates
                taskList.clear();

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    taskList.add(task); // Add task to local list
                    newTasks.add(task); // Add task to list for new notifications
                    Log.d(TAG, "Task added: " + task.getTaskTitle());
                }
                taskAdapter.notifyDataSetChanged(); // Notify adapter of data change
                Log.d(TAG, "Tasks loaded, total count: " + taskList.size());

                // Check for new tasks and show notifications
                for (Task task : newTasks) {
                    if (!fetchedTaskIds.contains(task.getTaskId())) {
                        fetchedTaskIds.add(task.getTaskId()); // Mark as fetched
                        showNotification(task); // Show notification for new task
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve tasks", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to retrieve tasks: " + error.getMessage());
            }
        });
    }


}
