package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.csci3310.learnleague.adapters.CommentAdapter;
import edu.cuhk.csci3310.learnleague.adapters.PostAdapter;
import edu.cuhk.csci3310.learnleague.models.Comment;
import edu.cuhk.csci3310.learnleague.models.Post;

/**
 * 帖子详情页面
 */
public class PostDetailActivity extends AppCompatActivity implements CommentAdapter.OnReplyClickListener {

    private Toolbar toolbar;
    private View postItemView;
    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private TextView sendButton;

    private String postId;
    private Post currentPost;
    private PostAdapter postAdapter;
    private CommentAdapter commentAdapter;
    private List<Post> postList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();

    private Comment replyingToComment; // 当前正在回复的评论

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 获取传入的帖子ID
        postId = getIntent().getStringExtra("POST_ID");
        boolean focusComment = getIntent().getBooleanExtra("FOCUS_COMMENT", false);

        if (postId == null) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化视图
        toolbar = findViewById(R.id.toolbar);
        postItemView = findViewById(R.id.post_item);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);

        // 设置工具栏
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("View detailed");
        }

        // 设置评论列表
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, commentList, this);
        commentsRecyclerView.setAdapter(commentAdapter);

        // 设置帖子视图
        postList.add(new Post()); // 添加一个空的帖子占位
        postAdapter = new PostAdapter(this, postList, true);
        // 这里不需要设置点击事件，因为这已经是帖子详情页面

        // 加载帖子详情和评论
        loadPostDetail();
        loadComments();

        // 设置发送评论按钮点击事件
        sendButton.setOnClickListener(v -> submitComment());

        // 如果需要聚焦评论输入框
        if (focusComment) {
            commentInput.requestFocus();
        }
    }

    /**
     * 加载帖子详情
     */
    private void loadPostDetail() {
        ApiClient.getPostDetail(postId, new ApiClient.ApiCallback<Post>() {
            @Override
            public void onSuccess(Post result) {
                runOnUiThread(() -> {
                    currentPost = result;
                    postList.clear();
                    postList.add(result);
                    postAdapter.notifyDataSetChanged();

                    // 更新帖子视图（因为我们使用了include布局，需要手动绑定数据）
                    // 这部分代码在实际项目中可能需要根据布局细节调整
                    PostAdapter.PostViewHolder holder = new PostAdapter.PostViewHolder(postItemView);
                    postAdapter.onBindViewHolder(holder, 0);
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(PostDetailActivity.this, "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 加载评论列表
     */
    private void loadComments() {
        ApiClient.getComments(postId, new ApiClient.ApiCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> result) {
                runOnUiThread(() -> {
                    commentList.clear();
                    commentList.addAll(result);
                    commentAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(PostDetailActivity.this, "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 提交评论
     */
    private void submitComment() {
        String content = commentInput.getText().toString().trim();
        if (content.isEmpty()) {
            commentInput.setError("Please comment...");
            return;
        }

        // 禁用发送按钮，防止重复提交
        sendButton.setEnabled(false);

        // 获取回复的评论ID（如果有）
        String parentCommentId = null;
        if (replyingToComment != null) {
            parentCommentId = replyingToComment.getId();
        }

        // 调用API提交评论
        ApiClient.addComment(postId, content, parentCommentId, new ApiClient.ApiCallback<Comment>() {
            @Override
            public void onSuccess(Comment result) {
                runOnUiThread(() -> {
                    // 清空输入框
                    commentInput.setText("");
                    // 添加新评论到列表顶部
                    commentList.add(0, result);
                    commentAdapter.notifyItemInserted(0);
                    commentsRecyclerView.scrollToPosition(0);
                    // 增加帖子的评论数
                    if (currentPost != null) {
                        currentPost.setCommentCount(currentPost.getCommentCount() + 1);
                        postAdapter.notifyDataSetChanged();
                    }
                    // 重置回复状态
                    cancelReply();
                    // 重新启用发送按钮
                    sendButton.setEnabled(true);
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(PostDetailActivity.this, "Failed to submits comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true);
                });
            }
        });
    }

    /**
     * 当用户点击回复按钮时
     */
    @Override
    public void onReplyClick(Comment comment) {
        replyingToComment = comment;
        commentInput.setHint("Reply " + comment.getUserName() + ": ");
        commentInput.requestFocus();

        // 显示取消回复按钮
        Snackbar.make(commentInput, "isReplying " + comment.getUserName(), Snackbar.LENGTH_INDEFINITE)
                .setAction("Cancel", v -> cancelReply())
                .show();
    }

    /**
     * cancel reply
     */
    private void cancelReply() {
        replyingToComment = null;
        commentInput.setHint("Leave your comments...");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // back to last page
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}