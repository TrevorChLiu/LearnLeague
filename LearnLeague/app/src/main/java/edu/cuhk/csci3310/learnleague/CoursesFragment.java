package edu.cuhk.csci3310.learnleague;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment implements CourseAdapter.OnStudyNowClickListener {
    private static final int REQUEST_CODE_YOUTUBE = 100;
    private static final int REQUEST_CODE_STUDY = 101;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private static List<Course> courseList;

    private long studyStartMillis;// 用來計時： 開始的時間
    // 用來記錄當前正在學習的course在courseList裡的index
    private int currentCourseIndex = -1;

    public CoursesFragment() {
        // Required empty public constructor
    }

    public static void loadCourseList() {
        courseList = new ArrayList<>();

        FileStorageHelper.loadCourses(new ApiClient.ApiCallback<List<Course>>() {
            @Override
            public void onSuccess(List<Course> result) {
                courseList.clear();
                courseList.addAll(result);
                Log.d("LoadCourseList onSuccess", courseList.toString());
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 從文件中讀取之前保存的數據，如果沒有則初始化 demo 數據

        /*
        if (courseList == null || courseList.isEmpty()) {
            courseList = initializeDemoData();
            FileStorageHelper.saveCourses(getContext(), courseList);
        }*/
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        // 設置 recycler view
        recyclerView = view.findViewById(R.id.coursesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        adapter = new CourseAdapter(getContext(), courseList, this);
        recyclerView.setAdapter(adapter);

        adapter.updateData(courseList);

        // FloatingActionButton 用來新增 YouTube 課程資料
        FloatingActionButton floatingActionButton = view.findViewById(R.id.addYouTubeLink);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), YouTubeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_YOUTUBE);
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 接收增加新課程的info
        if (requestCode == REQUEST_CODE_YOUTUBE && resultCode == getActivity().RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String thumbnailUrl = data.getStringExtra("thumbnailUrl");
            String videoUrl = data.getStringExtra("videoUrl");
            long watchTimeInSeconds = data.getLongExtra("watchTimeInSeconds", 0);

            if (title != null && thumbnailUrl != null && videoUrl != null) {
                Course newCourse = new Course(title, thumbnailUrl, watchTimeInSeconds, videoUrl);
                courseList.add(newCourse);
                adapter.notifyItemInserted(courseList.size() - 1);
                Toast.makeText(getContext(), "Successfully added：" + title, Toast.LENGTH_SHORT).show();

                // 保存更新後的數據到本地文件
                // FileStorageHelper.saveCourses(getContext(), courseList);
                FileStorageHelper.saveCourses(newCourse);
                Toast.makeText(getContext(), "Successfully added: " + title, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Failed to add new course", Toast.LENGTH_SHORT).show();
            }
            // 接收youtuber player的觀看時長
        } else if (requestCode == REQUEST_CODE_STUDY && resultCode == getActivity().RESULT_OK) {
            Log.d("gei back ", "ture");
            long elapsedMillis = System.currentTimeMillis() - studyStartMillis;
            long elapsedSeconds = elapsedMillis / 1000;
            Log.d("觀看時長", "user 看了 " + elapsedSeconds + " seconds.");

            if (currentCourseIndex >= 0 && currentCourseIndex < courseList.size()) {
                // 更新對應課程的觀看時間
                Course course = courseList.get(currentCourseIndex);
                long updatedWatchTime = course.getWatchTimeInSeconds() + elapsedSeconds;
                // Update the users' learning time
                User.insertStudyRecordToday((int)elapsedSeconds);
                course.setWatchTimeInSeconds(updatedWatchTime);
                Log.d("updatedWatchTime", "+"+updatedWatchTime);
                adapter.notifyItemChanged(currentCourseIndex);

                // 保存更新後的data到本地文件
                // FileStorageHelper.saveCourses(getContext(), courseList);
                FileStorageHelper.saveCourses(course);
            }
        }
    }

    @Override
    public void onStudyNowClicked(Course course, int position) {
        // 記錄開始學習的時間
        studyStartMillis = System.currentTimeMillis();
        currentCourseIndex = position; //課程的index
        Log.d("studyStartMillis", "+"+studyStartMillis);

        // go to 播放youtube 視頻
        Intent intent = new Intent(getContext(), YouTubePlayerActivity.class);
        String videoId = adapter.extractVideoId(course.getVideoUrl());
        intent.putExtra("VIDEO_ID", videoId);
        startActivityForResult(intent, REQUEST_CODE_STUDY);
    }
}