package edu.cuhk.csci3310.learnleague;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YouTubeActivity extends AppCompatActivity {

    private EditText youtubeLinkEditText;
    private Button submitButton;
    private ImageView thumbnailImageView;
    private TextView titleTextView;
    private String API_KEY = "AIzaSyCN6PbCy_ujmP0dhqeQaT9NBIf09-YyYG0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_addyoutube);

        youtubeLinkEditText = findViewById(R.id.youtubeLinkEditText);
        submitButton = findViewById(R.id.submitButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // back button to course page
        backButton.setOnClickListener(v -> finish());

        // click the submit button to retrieve the video information
        submitButton.setOnClickListener(v -> {
            String youtubeLink = youtubeLinkEditText.getText().toString().trim();

            if (youtubeLink.isEmpty()) {
                Toast.makeText(this, "Please paste a valid YouTube link！", Toast.LENGTH_SHORT).show();
            } else {
                new GetYouTubeVideoInfoTask().execute(youtubeLink);
            }
        });
    }

    public class GetYouTubeVideoInfoTask extends AsyncTask<String, Void, JSONObject> {
        private String youtubeLink;
        @Override
        protected JSONObject doInBackground(String... params) {
            youtubeLink = params[0];
            String videoId = extractVideoId(youtubeLink);

            if (videoId == null) {
//                Log.e("YouTubeActivity", "視頻ID提取不了");
                return null;
            }

            try {
                // 用Google YouTube API來獲取這個視頻的相關信息
                String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="
                        + videoId + "&key=" + API_KEY;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);

                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                Log.d("InputStreamReader", "連上了");
                char[] buffer = new char[1024];
                StringBuilder stringBuilder = new StringBuilder();
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    stringBuilder.append(buffer, 0, bytesRead);
                }
                reader.close();
                connection.disconnect();

                return new JSONObject(stringBuilder.toString());
            } catch (Exception e) {
                Log.e("YouTubeActivity", "獲取視頻信息失敗: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    Log.d("Result", result.toString());

                    if (result.has("items") && result.getJSONArray("items").length() > 0) {
                        JSONObject snippet = result.getJSONArray("items")
                                .getJSONObject(0)
                                .getJSONObject("snippet");
                        String title = snippet.getString("title");
                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        String thumbnailUrl = "";

                        if (thumbnails.has("medium")) {
                            thumbnailUrl = thumbnails.getJSONObject("medium").getString("url");
                        } else if (thumbnails.has("default")) {
                            thumbnailUrl = thumbnails.getJSONObject("default").getString("url");
                        }

                        // 添加一個新的course ，一開始默認觀看世間是0
                        Course newCourse = new Course(title, thumbnailUrl, 0, youtubeLink);

                        // 將新課程data 放盡 Intent裡
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("title", newCourse.getTitle());
                        resultIntent.putExtra("thumbnailUrl", newCourse.getThumbnailUrl());
                        resultIntent.putExtra("videoUrl", newCourse.getVideoUrl());
                        resultIntent.putExtra("watchTimeInSeconds", newCourse.getWatchTimeInSeconds());

                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }else {
                        Toast.makeText(YouTubeActivity.this, "沒找到視頻信息", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("YouTubeActivity", "JSON有問題 " + e.getMessage());
                }
            } else {
                Toast.makeText(YouTubeActivity.this, "獲取視頻信息失敗", Toast.LENGTH_SHORT).show();
            }
        }


        // 輸入一個視頻鏈接 提取裡面的video ID
        // 有兩種不同格式 要分別處理
        private String extractVideoId(String youtubeLink) {
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
    }
}