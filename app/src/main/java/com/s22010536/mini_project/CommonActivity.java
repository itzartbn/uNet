package com.s22010536.mini_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

public class CommonActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView profileImageView;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        tabLayout = findViewById(R.id.tabsLayout);
        viewPager = findViewById(R.id.viewPager);
        profileImageView = findViewById(R.id.home_pp);

        viewPager.setAdapter(new FragmentAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Home");
                    } else {
                        tab.setText("Settings");
                    }
                }).attach();

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(CommonActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        if (firebaseUser != null) {
            loadUserProfile(firebaseUser);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(CommonActivity.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(CommonActivity.this, "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommonActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(CommonActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new HomeActivity();
            } else {
                return new SettingsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
