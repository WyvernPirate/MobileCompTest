package com.example.testmobilecomp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.taskNameTextView.setText(task.getName());
        holder.organizerTextView.setText(task.getOrganizer());
        holder.volunteerCountTextView.setText("Volunteers: " + task.getVolunteerCount());

        holder.signUpButton.setOnClickListener(v -> {
            String userName = "User";
            int n = task.getVolunteerCount();
            n++;
            task.setVolunteerCount(n);
            task.setStatus(userName);

            // Update Firestore with the new volunteer count and data
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("tasks")
                    .document(task.getId())
                    .update("volunteerCount", task.getVolunteerCount())
                    .addOnSuccessListener(aVoid -> {
                        // Show toast confirmation
                        Toast.makeText(context, "Signed up for " + task.getName(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                        Toast.makeText(context, "Error signing up", Toast.LENGTH_SHORT).show();
                    });
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskNameTextView, organizerTextView, volunteerCountTextView;
        Button signUpButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.textViewTaskName);
            organizerTextView = itemView.findViewById(R.id.textViewTaskOrganizer);
            volunteerCountTextView = itemView.findViewById(R.id.textViewVolunteerCount);
            signUpButton = itemView.findViewById(R.id.buttonSignUp);
        }
    }
}
