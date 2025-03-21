package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Hold those fragments object until process ends
    Fragment profileFragment = new ProfileFragment();
    Fragment coursesFragment = new CoursesFragment();
    Fragment communityFragment = new CommunityFragment();
    Fragment rankingFragment = new RankingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Implement bottom nav bar's functionality
        replaceFragment(coursesFragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.profile) {
                replaceFragment(profileFragment);
            } else if (item.getItemId() == R.id.courses) {
                replaceFragment(coursesFragment);
            } else if (item.getItemId() == R.id.community) {
                replaceFragment(communityFragment);
            } else {
                replaceFragment(rankingFragment);
            }

            return true;
        });
    }

    /**
     * Replace the current shown fragment with another.
     * @param newFragment New fragment.
     */
    private void replaceFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, newFragment)
                .commit();
    }

}

