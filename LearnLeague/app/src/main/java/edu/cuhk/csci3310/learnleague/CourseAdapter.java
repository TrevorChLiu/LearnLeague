package edu.cuhk.csci3310.learnleague;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private Context context;
    private List<Course> courseList;
    private OnStudyNowClickListener listener;

    public CourseAdapter(Context context, List<Course> courseList, OnStudyNowClickListener listener) {
        this.context = context;
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseTitleTextView.setText(course.getTitle());
        Glide.with(context).load(course.getThumbnailUrl()).into(holder.courseThumbImageView);

        long hours = course.getWatchTimeInSeconds() / 3600;
        long minutes = (course.getWatchTimeInSeconds() % 3600) / 60;
        holder.watchTimeTextView.setText(String.format("watch time：%d hours %d minutes", hours, minutes));

        // 點擊study now 跳轉
        holder.studyNowButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStudyNowClicked(course, position);
            }
        });
    }

    // 提取視頻ID
    public String extractVideoId(String youtubeLink) {
        String videoId = null;
        if (youtubeLink.contains("youtu.be/")) {
            videoId = youtubeLink.substring(youtubeLink.lastIndexOf("/") + 1);
            int queryIndex = videoId.indexOf("?");
            if (queryIndex != -1) {
                videoId = videoId.substring(0, queryIndex);
            }
        } else if (youtubeLink.contains("youtube.com/") && youtubeLink.contains("v=")) {
            String query = youtubeLink.substring(youtubeLink.indexOf("v=") + 2);
            int ampIndex = query.indexOf("&");
            if (ampIndex != -1) {
                videoId = query.substring(0, ampIndex);
            } else {
                videoId = query;
            }
        }
        return videoId;
    }

    public void updateData(List<Course> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView courseThumbImageView;
        TextView courseTitleTextView;
        TextView watchTimeTextView;
        Button studyNowButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseThumbImageView = itemView.findViewById(R.id.courseThumbImageView);
            courseTitleTextView = itemView.findViewById(R.id.courseTitleTextView);
            watchTimeTextView = itemView.findViewById(R.id.watchTimeTextView);
            studyNowButton = itemView.findViewById(R.id.studyNowButton);
        }
    }

    public interface OnStudyNowClickListener {
        void onStudyNowClicked(Course course, int position);
    }
}