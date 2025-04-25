package com.example.testmobilecomp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

        // Show status if available
        if (task.getStatus() != null && !task.getStatus().isEmpty()) {
            holder.statusTextView.setVisibility(View.VISIBLE);
            holder.statusTextView.setText("Status: " + task.getStatus());
        } else {
            holder.statusTextView.setVisibility(View.GONE);
        }

        holder.signUpButton.setOnClickListener(v -> {
            // Navigate to task details fragment
            AppCompatActivity activity = (AppCompatActivity) context;
            TaskDetailsFragment detailsFragment = TaskDetailsFragment.newInstance(
                    task.getId(), task.getName());

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskNameTextView, organizerTextView, volunteerCountTextView, statusTextView;
        Button signUpButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.textViewTaskName);
            organizerTextView = itemView.findViewById(R.id.textViewTaskOrganizer);
            volunteerCountTextView = itemView.findViewById(R.id.textViewVolunteerCount);
            statusTextView = itemView.findViewById(R.id.textViewStatus);
            signUpButton = itemView.findViewById(R.id.buttonSignUp);
        }
    }
}