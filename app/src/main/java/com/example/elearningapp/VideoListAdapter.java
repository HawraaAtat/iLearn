package com.example.elearningapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    Context context;
    ArrayList<ModelVideo> videoArrayList = new ArrayList<>();
    HashMap<String, Boolean> completionStatusMap = new HashMap<>();
    String courseTitle;

    public VideoListAdapter(Context context, ArrayList<ModelVideo> videoArrayList, HashMap<String, Boolean> completionStatusMap, String courseTitle) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        this.completionStatusMap = completionStatusMap;
        this.courseTitle = courseTitle;
    }

    public void setCompletionStatus(String videoTitle, boolean isCompleted) {
        completionStatusMap.put(videoTitle, isCompleted);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_videos, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelVideo video = videoArrayList.get(position);
        holder.tv_title.setText(video.getTitle());

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference()
                    .child("UserProgress")
                    .child(userId)
                    .child("courses")
                    .child(courseTitle)
                    .child("videos")
                    .child(video.getTitle())
                    .child("completed");

            videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isCompleted = dataSnapshot.getValue(Boolean.class);
                    if (isCompleted != null && isCompleted) {
                        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
                        setCompletionStatus(video.getTitle(), true); // Update the completion status in the adapter
                    } else {
                        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                        setCompletionStatus(video.getTitle(), false); // Update the completion status in the adapter
                    }

                    // Calculate progress
                    calculateProgress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that may occur
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayVideo.class);
                intent.putExtra("courseTitle", courseTitle); // Pass the course title to PlayVideo activity
                intent.putExtra("title", video.getTitle());
                intent.putExtra("videoUrl", video.getVideoUrl());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.titleTv);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void calculateProgress() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the reference to the user's progress in the database
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference progressRef = FirebaseDatabase.getInstance().getReference()
                    .child("UserProgress")
                    .child(userId)
                    .child("videos");

            progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int completedVideos = 0;
                    int totalVideos = videoArrayList.size();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String videoTitle = ds.getKey();
                        Boolean isCompleted = ds.child("completed").getValue(Boolean.class);

                        if (isCompleted != null && isCompleted) {
                            if (completionStatusMap.containsKey(videoTitle) && completionStatusMap.get(videoTitle)) {
                                completedVideos++;
                            }
                        }
                    }

                    int progress = (int) (((float) completedVideos / totalVideos) * 100);
                    // Update the progress UI here, e.g., using a callback or event
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors that may occur
                }
            });
        }
    }
}