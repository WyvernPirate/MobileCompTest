package com.example.testmobilecomp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TaskDetailsFragment extends Fragment {

    private String taskId;
    private String taskName;
    private FirebaseFirestore db;

    private EditText editTextName;
    private TimePicker timePicker;
    private Button buttonConfirm;
    private TextView textViewTaskTitle;

    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    // Static factory method to create a new instance with task data
    public static TaskDetailsFragment newInstance(String taskId, String taskName) {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        args.putString("taskId", taskId);
        args.putString("taskName", taskName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString("taskId");
            taskName = getArguments().getString("taskName");
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        // Initialize UI components
        textViewTaskTitle = view.findViewById(R.id.textViewTaskTitle);
        editTextName = view.findViewById(R.id.editTextName);
        timePicker = view.findViewById(R.id.timePicker);
        buttonConfirm = view.findViewById(R.id.buttonConfirm);

        // Set 24-hour format for better UX
        timePicker.setIs24HourView(true);

        // Set task name in title
        textViewTaskTitle.setText(taskName);

        // Set up button click listener
        buttonConfirm.setOnClickListener(v -> signUpForTask());

        return view;
    }

    private void signUpForTask() {
        String volunteerName = editTextName.getText().toString().trim();

        // Validate input
        if (volunteerName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format time
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String preferredTime = String.format("%02d:%02d", hour, minute);

        // Create volunteer data
        Map<String, Object> volunteerData = new HashMap<>();
        volunteerData.put("name", volunteerName);
        volunteerData.put("preferredTime", preferredTime);

        // Reference to the task document
        DocumentReference taskRef = db.collection("tasks").document(taskId);

        // Add volunteer to the volunteers array and increment count
        taskRef.update(
                "volunteers", FieldValue.arrayUnion(volunteerData),
                "volunteerCount", FieldValue.increment(1)
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Successfully signed up for " + taskName, Toast.LENGTH_SHORT).show();

            // Check if we've reached 3 volunteers to auto-approve
            taskRef.get().addOnSuccessListener(documentSnapshot -> {
                Task task = documentSnapshot.toObject(Task.class);
                if (task != null && task.getVolunteerCount() >= 3 && !"approved".equals(task.getStatus())) {
                    // Auto-approve the task
                    taskRef.update("status", "approved")
                            .addOnSuccessListener(unused -> {
                                // Add a news entry about the approval
                                Map<String, Object> newsData = new HashMap<>();
                                newsData.put("content", taskName + " has been approved with 3+ volunteers!");
                                newsData.put("timestamp", System.currentTimeMillis());

                                db.collection("news").add(newsData);
                            });
                }
            });

            // Go back to task list
            getParentFragmentManager().popBackStack();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error signing up: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}