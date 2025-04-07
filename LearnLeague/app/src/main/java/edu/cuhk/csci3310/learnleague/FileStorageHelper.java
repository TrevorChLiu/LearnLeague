package edu.cuhk.csci3310.learnleague;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileStorageHelper {

    private static final String FILE_NAME = "courses.json";

    /**
     * Save a new course.
     * @param course  New course infomation
     */
    public static void saveCourses(Course course) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.addCourse(course.getTitle(), course.getThumbnailUrl(), course.getWatchTimeInSeconds(), course.getVideoUrl());
        });

    }

    /**
     * 從本地文件中讀取保存的課程列表
     *
     * @param context 上下文
     * @return 存儲的課程列表，如果文件不存在或者解析失敗，返回 null
     */
    public static void loadCourses(ApiClient.ApiCallback<List<Course>> listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getCourses(listener);
        });
    }
}