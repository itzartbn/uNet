package com.s22010536.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class TeacherCommonActivity extends AppCompatActivity implements OnTaskAdded {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView profileImageView;
    private FragmentAdapter fragmentAdapter;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_common);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        tabLayout = findViewById(R.id.tabsLayout);
        viewPager = findViewById(R.id.viewPager);
        profileImageView = findViewById(R.id.home_pp);

        fragmentAdapter = new FragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Home");
                            break;
                        case 1:
                            tab.setText("Notifications");
                            break;
                        case 2:
                            tab.setText("Settings");
                            break;
                    }
                });
        tabLayoutMediator.attach();

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherCommonActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        if (firebaseUser != null) {
            loadUserProfile(firebaseUser);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onTaskAddedToList() {
        // Switch to the Home tab
        viewPager.setCurrentItem(0);

        // Refresh the TeacherHomeActivity fragment
        Fragment fragment = fragmentAdapter.getFragment(0);
        if (fragment != null && fragment instanceof TeacherHomeActivity) {
            ((TeacherHomeActivity) fragment).refreshTasks();
        }
    }

    private void loadUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profilePictureUrl = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
                    if (profilePictureUrl != null) {
                        Picasso.get()
                                .load(profilePictureUrl)
                                .placeholder(R.drawable.person_icon_topbar)
                                .into(profileImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Handle success
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Handle error
                                        Toast.makeText(TeacherCommonActivity.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(TeacherCommonActivity.this, "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherCommonActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class FragmentAdapter extends FragmentStateAdapter {

        private Fragment[] fragments;

        public FragmentAdapter(TeacherCommonActivity activity) {
            super(activity);
            fragments = new Fragment[] {
                    new TeacherHomeActivity(),
                    new TaskFormFragment(),
                    new SettingsFragment()
            };
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return 3; // Number of tabs
        }

        public Fragment getFragment(int position) {
            return fragments[position];
        }
    }
}
