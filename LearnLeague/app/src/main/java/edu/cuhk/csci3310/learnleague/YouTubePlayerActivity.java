package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;



public class YouTubePlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtubeplayer);

        // back button to return to course page
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        // 獲取視頻id
        String videoIdTemp = getIntent().getStringExtra("VIDEO_ID");
//        Log.d("videoIdTemp", videoIdTemp);
        final String videoId = (videoIdTemp == null || videoIdTemp.isEmpty())
                ? "A3Ffwsnad0k" : videoIdTemp;
        Log.d("videoId final", videoId);

        //
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);

        // 自動管理player的lifecycle
        getLifecycle().addObserver(youTubePlayerView);
        Log.d("youtube player", "true");

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // 從頭開始播視頻
//                Log.d("播放之前的videoId", videoId);
                youTubePlayer.cueVideo(videoId, 0);
//                youTubePlayer.loadVideo(videoId, 0);
//                Log.d("start", "yes");
            }
        });
    }
}