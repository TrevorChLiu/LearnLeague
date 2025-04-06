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

public class FileStorageHelper {

    private static final String FILE_NAME = "courses.json";

    /**
     * 將課程列表保存為 JSON 存到本地文件中
     *
     * @param context  上下文
     * @param courses  課程列表
     */
    public static void saveCourses(Context context, List<Course> courses) {
        Gson gson = new Gson();
        String json = gson.toJson(courses);

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 從本地文件中讀取保存的課程列表
     *
     * @param context 上下文
     * @return 存儲的課程列表，如果文件不存在或者解析失敗，返回 null
     */
    public static List<Course> loadCourses(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Course>>() {}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}