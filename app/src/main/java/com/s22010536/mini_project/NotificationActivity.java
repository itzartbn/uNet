package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class NotificationActivity extends AppCompatActivity implements OnTaskAdded {

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolBar = findViewById(R.id.toolbar);
        authProfile = FirebaseAuth.getInstance();


        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Home");

        FloatingActionButton fab = findViewById(R.id.fab_add_task);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment taskFormFragment = new TaskFormFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.task_list_fragment, taskFormFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onTaskAddedToList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(); // Go back to the previous state, which should be the TaskListFragment

        TaskListFragment taskListFragment = new TaskListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.task_list_fragment, taskListFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh){
            //refreash
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);

    }
}
