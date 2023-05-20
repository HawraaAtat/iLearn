package com.example.elearningapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elearningapp.tutor.AddCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private Context context;
    private List<ModelCourse> videoArrayList;

    public CourseListAdapter(Context context, List<ModelCourse> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelCourse modelVideo = videoArrayList.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(modelVideo.getTimestamp()));
        String formattedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.tvTitle.setText(modelVideo.getTitle());
        holder.tvTime.setText(formattedDate);
        holder.tvCategory.setText(modelVideo.getCategory());
        holder.tvTutor.setText(modelVideo.getTutor());

        DatabaseReference lessonsRef = FirebaseDatabase.getInstance().getReference(AddCourse.COURSES)
                .child(modelVideo.getTutor())
                .child(modelVideo.getTitle())
                .child("videos");
        lessonsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long lessonCount = dataSnapshot.getChildrenCount();
                holder.tvTotalLessons.setText(lessonCount + " Lessons");
                Log.v("TAG", String.valueOf(lessonCount));

                if (lessonCount >= 10) {
                    holder.enrollButton.setEnabled(false);
                } else {
                    holder.enrollButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayCourse.class);
                intent.putExtra("tutor", modelVideo.getTutor());
                intent.putExtra("courseTitle", modelVideo.getTitle());
                context.startActivity(intent);
            }
        });

        holder.enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollInCourse(modelVideo.getTutor(), modelVideo.getTitle());
            }
        });
    }

    private void enrollInCourse(String tutor, String courseTitle) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference enrollmentRef = FirebaseDatabase.getInstance().getReference("enrollments")
                .child(userId)
                .child(tutor)
                .child(courseTitle);

        enrollmentRef.setValue(true);
        // You can add a success message or perform additional actions after enrollment
        Log.d("CourseListAdapter", "Enrollment successful");
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvTime, tvTotalLessons, tvCategory, tvTutor;
        Button enrollButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.titleTv);
            tvTime = itemView.findViewById(R.id.timeTv);
            tvTotalLessons = itemView.findViewById(R.id.totlaLessonsTv);
            tvCategory = itemView.findViewById(R.id.categoryTv);
            tvTutor = itemView.findViewById(R.id.tutorTv);
            enrollButton = itemView.findViewById(R.id.enrollButton);
        }
    }
}