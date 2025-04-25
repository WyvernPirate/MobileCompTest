package com.example.testmobilecomp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private boolean animateNewsFeed;
    private String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        loadPreferences();

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tasks) {
                // Pass sort order to fragment
                Bundle args = new Bundle();
                args.putString("sort_order", sortOrder);

                selectedFragment = new TaskListFragment();
                selectedFragment.setArguments(args);
            } else if (itemId == R.id.nav_news) {
                // Pass animation preference to fragment
                Bundle args = new Bundle();
                args.putBoolean("animate_news", animateNewsFeed);

                selectedFragment = new NewsFeedFragment();
                selectedFragment.setArguments(args);
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });

        // Set the initial fragment
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString("sort_order", sortOrder);

            TaskListFragment initialFragment = new TaskListFragment();
            initialFragment.setArguments(args);

            loadFragment(initialFragment);
            // Set the initial selected item
            bottomNavigationView.setSelectedItemId(R.id.nav_tasks);
        }
    }

    private void loadPreferences() {
        animateNewsFeed = sharedPreferences.getBoolean("animate_news", true);
        sortOrder = sharedPreferences.getString("sort_order", "name");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_task) {
            addDummyTask();
            return true;
        } else if (id == R.id.action_settings) {
            // Open settings activity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_refresh) {
            refreshCurrentFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshCurrentFragment() {
        // Refresh current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof TaskListFragment) {
            ((TaskListFragment) currentFragment).fetchTasks();
            Toast.makeText(this, "Tasks refreshed", Toast.LENGTH_SHORT).show();
        } else if (currentFragment instanceof NewsFeedFragment) {
            ((NewsFeedFragment) currentFragment).fetchNews();
            Toast.makeText(this, "News refreshed", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDummyTask() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new task
        Map<String, Object> task = new HashMap<>();
        task.put("name", "New Dog Walking Task");
        task.put("organizer", "Pet Care Coordinator");
        task.put("volunteerCount", 0);
        task.put("status", "pending");

        db.collection("tasks").add(task)
                .addOnSuccessListener(documentReference -> {
                    // Update the task with its ID
                    documentReference.update("id", documentReference.getId());

                    Toast.makeText(this, "New task added!", Toast.LENGTH_SHORT).show();

                    // Refresh task list if we're on the task list fragment
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof TaskListFragment) {
                        ((TaskListFragment) currentFragment).fetchTasks();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("animate_news")) {
            animateNewsFeed = sharedPreferences.getBoolean(key, true);

            // Update the current fragment if it's the news feed
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof NewsFeedFragment) {
                Bundle args = new Bundle();
                args.putBoolean("animate_news", animateNewsFeed);
                currentFragment.setArguments(args);
                loadFragment(currentFragment);
            }
        } else if (key.equals("sort_order")) {
            sortOrder = sharedPreferences.getString(key, "name");

            // Update the current fragment if it's the task list
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof TaskListFragment) {
                Bundle args = new Bundle();
                args.putString("sort_order", sortOrder);
                currentFragment.setArguments(args);
                ((TaskListFragment) currentFragment).fetchTasks();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}