package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.cuhk.csci3310.learnleague.adapters.PostAdapter;
import edu.cuhk.csci3310.learnleague.models.Post;
import edu.cuhk.csci3310.learnleague.models.User;

/**
 * 用户资料页面
 */
public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView backdrop;
    private CircleImageView userAvatar;
    private TextView userName, userId, joinDate, userBio;
    private TextView coursesCount, postsCount, likesCount;
    private RecyclerView userPostsRecyclerView;

    private String userIdStr;
    private User currentUser;
    private PostAdapter postAdapter;
    private List<Post> userPosts = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 获取传入的用户ID
        userIdStr = getIntent().getStringExtra("USER_ID");
        if (userIdStr == null) {
            Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化视图
        toolbar = findViewById(R.id.toolbar);
        backdrop = findViewById(R.id.backdrop);
        userAvatar = findViewById(R.id.user_avatar);
        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        joinDate = findViewById(R.id.join_date);
        userBio = findViewById(R.id.user_bio);
        coursesCount = findViewById(R.id.courses_count);
        postsCount = findViewById(R.id.posts_count);
        likesCount = findViewById(R.id.likes_count);
        userPostsRecyclerView = findViewById(R.id.user_posts_recycler_view);

        // 设置工具栏
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Information");
        }

        // 设置用户帖子列表
        userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, userPosts, false);
        userPostsRecyclerView.setAdapter(postAdapter);

        // 加载用户资料
        loadUserProfile();

        // 加载用户发布的帖子
        loadUserPosts();
    }

    /**
     * 加载用户资料
     */
    private void loadUserProfile() {
        ApiClient.getUserProfile(userIdStr, new ApiClient.ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                runOnUiThread(() -> {
                    currentUser = result;

                    // 设置用户基本资料
                    userName.setText(result.getName());
                    userId.setText("UserID: " + result.getId());
                    joinDate.setText("Joined Time: " + dateFormat.format(result.getJoinDate()));
                    userBio.setText(result.getBio());

                    // 设置统计数据
                    coursesCount.setText(String.valueOf(result.getCoursesCount()));
                    postsCount.setText(String.valueOf(result.getPostsCount()));
                    likesCount.setText(String.valueOf(result.getLikesReceived()));

                    // 加载用户头像
                    if (result.getAvatarUrl() != null && !result.getAvatarUrl().isEmpty()) {
                        Glide.with(UserProfileActivity.this)
                                .load(result.getAvatarUrl())
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(userAvatar);

                        // 设置背景图片（这里用模糊处理的头像作为背景）
                        Glide.with(UserProfileActivity.this)
                                .load(result.getAvatarUrl())
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(backdrop);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 加载用户发布的帖子
     */
    private void loadUserPosts() {
        ApiClient.getUserPosts(userIdStr, new ApiClient.ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                runOnUiThread(() -> {
                    userPosts.clear();
                    userPosts.addAll(result);
                    postAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 返回上一页
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}