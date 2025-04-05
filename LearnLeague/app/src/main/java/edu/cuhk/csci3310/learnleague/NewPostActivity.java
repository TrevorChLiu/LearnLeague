package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.cuhk.csci3310.learnleague.models.Post;

/**
 * 发布新帖子页面
 */
public class NewPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText titleInput, contentInput;
    private FloatingActionButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // 初始化视图
        toolbar = findViewById(R.id.toolbar);
        titleInput = findViewById(R.id.post_title_input);
        contentInput = findViewById(R.id.post_content_input);
        submitButton = findViewById(R.id.submit_button);

        // 设置工具栏
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Posts");
        }

        // 提交按钮点击事件
        submitButton.setOnClickListener(this::submitPost);
    }

    /**
     * 提交帖子
     */
    private void submitPost(View view) {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();

        // 验证输入
        if (title.isEmpty()) {
            titleInput.setError("Please input title");
            titleInput.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            contentInput.setError("Please input content");
            contentInput.requestFocus();
            return;
        }

        // disable submit button
        submitButton.setEnabled(false);

        // 调用API创建帖子
        ApiClient.createPost(title, content, new ApiClient.ApiCallback<Post>() {
            @Override
            public void onSuccess(Post result) {
                runOnUiThread(() -> {
                    Toast.makeText(NewPostActivity.this, "发布成功!", Toast.LENGTH_SHORT).show();
                    finish(); // 关闭当前页面，返回到社区
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(NewPostActivity.this, "Post failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    submitButton.setEnabled(true); // 重新启用提交按钮
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