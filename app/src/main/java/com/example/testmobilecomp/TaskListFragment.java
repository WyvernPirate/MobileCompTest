package com.example.testmobilecomp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FirebaseFirestore db;

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        recyclerViewTasks = rootView.findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch tasks from Firestore
        fetchTasks();

        return rootView;
    }

    private void fetchTasks() {
        CollectionReference tasksRef = db.collection("tasks");
        tasksRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            taskList.clear();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                Task taskData = documentSnapshot.toObject(Task.class);
                                taskList.add(taskData);
                            }
                            taskAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                        }
                    } else {
                        Toast.makeText(getContext(), "Error fetching tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
