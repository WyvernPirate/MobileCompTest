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

public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerViewNewsFeed;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private FirebaseFirestore db;

    public NewsFeedFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);

        recyclerViewNewsFeed = rootView.findViewById(R.id.recyclerViewNewsFeed);
        recyclerViewNewsFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), newsList);
        recyclerViewNewsFeed.setAdapter(newsAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch news from Firestore
        fetchNews();

        return rootView;
    }

    private void fetchNews() {
        CollectionReference newsRef = db.collection("news");
        newsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            newsList.clear();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                News newsItem = documentSnapshot.toObject(News.class);
                                newsList.add(newsItem);
                            }
                            newsAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                        }
                    } else {
                        Toast.makeText(getContext(), "Error fetching news", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
