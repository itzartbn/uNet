package com.s22010536.mini_project;

import android.content.Intent;
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

public class CommonActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

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
            Intent intent = new Intent(CommonActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
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
               return new HomeActivity();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
