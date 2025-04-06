package edu.cuhk.csci3310.learnleague.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.cuhk.csci3310.learnleague.ApiClient;
import edu.cuhk.csci3310.learnleague.PostDetailActivity;
import edu.cuhk.csci3310.learnleague.R;
import edu.cuhk.csci3310.learnleague.UserProfileActivity;
import edu.cuhk.csci3310.learnleague.models.Post;

import android.widget.ImageButton;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private boolean isDetailMode;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public PostAdapter(Context context, List<Post> postList, boolean isDetailMode) {
        this.context = context;
        this.postList = postList;
        this.isDetailMode = isDetailMode;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.userName.setText(post.getUserName());
        holder.postTime.setText(dateFormat.format(post.getCreatedAt()));
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getContent());
        holder.likeButton.setText(String.format("Likes (%d)", post.getLikeCount()));
        holder.commentButton.setText(String.format("Comments (%d)", post.getCommentCount()));

        // 设置点赞按钮状态
        if (post.isLikedByCurrentUser()) {
            holder.likeButton.setBackgroundTintList(context.getColorStateList(R.color.colorPrimary));
            holder.likeButton.setTextColor(context.getColor(android.R.color.white));
        } else {
            holder.likeButton.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
            holder.likeButton.setTextColor(context.getColor(android.R.color.black));
        }

        // 加载用户头像
        if (post.getUserAvatarUrl() != null && !post.getUserAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getUserAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.userAvatar);
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 设置点击事件
        if (!isDetailMode) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                context.startActivity(intent);
            });
        }

        // 点赞按钮点击事件
        holder.likeButton.setOnClickListener(v -> {
            ApiClient.togglePostLike(post.getId(), new ApiClient.ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean isLiked) {
                    // 更新UI需要在主线程中执行
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (isLiked) {
                            post.setLikeCount(post.getLikeCount() + 1);
                            post.setLikedByCurrentUser(true);
                            holder.likeButton.setBackgroundTintList(context.getColorStateList(R.color.colorPrimary));
                            holder.likeButton.setTextColor(context.getColor(android.R.color.white));
                        } else {
                            post.setLikeCount(post.getLikeCount() - 1);
                            post.setLikedByCurrentUser(false);
                            holder.likeButton.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
                            holder.likeButton.setTextColor(context.getColor(android.R.color.black));
                        }
                        holder.likeButton.setText(String.format("Likes (%d)", post.getLikeCount()));
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        // 评论按钮点击事件
        holder.commentButton.setOnClickListener(v -> {
            if (!isDetailMode) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                intent.putExtra("FOCUS_COMMENT", true);
                context.startActivity(intent);
            }
        });

        // 查看用户资料按钮点击事件
        holder.viewProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("USER_ID", post.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userAvatar;
        TextView userName, postTime, postTitle, postContent;
        Button likeButton, commentButton;  // 这两个是正常的Button
        ImageButton viewProfileButton;     // 这个是ImageButton

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            postTime = itemView.findViewById(R.id.post_time);
            postTitle = itemView.findViewById(R.id.post_title);
            postContent = itemView.findViewById(R.id.post_content);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            viewProfileButton = itemView.findViewById(R.id.view_profile_button);
        }
    }
}