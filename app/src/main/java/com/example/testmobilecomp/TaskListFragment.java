package com.example.testmobilecomp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FirebaseFirestore db;
    private TextView textViewNewsFeed;
    private String sortOrder = "name"; // Default sort

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortOrder = getArguments().getString("sort_order", "name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        recyclerViewTasks = rootView.findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        textViewNewsFeed = rootView.findViewById(R.id.textViewNewsFeed);
        textViewNewsFeed.setVisibility(View.VISIBLE);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch tasks from Firestore
        fetchTasks();

        // Fetch news for the news feed section
        fetchLatestNews();

        return rootView;
    }

    public void fetchTasks() {
        CollectionReference tasksRef = db.collection("tasks");

        // Create query based on sort order
        Query query;
        if ("volunteer_count".equals(sortOrder)) {
            query = tasksRef.orderBy("volunteerCount", Query.Direction.DESCENDING);
        } else if ("status".equals(sortOrder)) {
            query = tasksRef.orderBy("status", Query.Direction.ASCENDING);
        } else {
            // Default to name
            query = tasksRef.orderBy("name", Query.Direction.ASCENDING);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            taskList.clear();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                Task taskData = documentSnapshot.toObject(Task.class);
                                if (taskData != null) {
                                    // Ensure ID is set
                                    if (taskData.getId() == null) {
                                        taskData.setId(documentSnapshot.getId());
                                    }
                                    taskList.add(taskData);
                                }
                            }
                            taskAdapter.notifyDataSetChanged(); // Refresh the RecyclerView

                            // Also save to SQLite for offline use
                            saveTasksToLocalDb();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error fetching tasks", Toast.LENGTH_SHORT).show();
                        // Try to load from SQLite if available
                        loadTasksFromLocalDb();
                    }
                });
    }

    private void fetchLatestNews() {
        db.collection("news")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        News news = queryDocumentSnapshots.getDocuments().get(0).toObject(News.class);
                        if (news != null) {
                            textViewNewsFeed.setText(news.getContent());
                        }
                    }
                });
    }

    private void saveTasksToLocalDb() {
        if (getContext() == null) return;

        TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(getContext());
        for (Task task : taskList) {
            dbHelper.insertTask(task);
        }
    }

    private void loadTasksFromLocalDb() {

    }
}