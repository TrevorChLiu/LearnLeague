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
import edu.cuhk.csci3310.learnleague.models.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private OnReplyClickListener replyClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }

    public CommentAdapter(Context context, List<Comment> commentList, OnReplyClickListener replyClickListener) {
        this.context = context;
        this.commentList = commentList;
        this.replyClickListener = replyClickListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.userName.setText(comment.getUserName());
        holder.commentTime.setText(dateFormat.format(comment.getCreatedAt()));
        holder.commentContent.setText(comment.getContent());
        holder.likeButton.setText(String.format("Likes (%d)", comment.getLikeCount()));

        // 设置点赞按钮状态
        if (comment.isLikedByCurrentUser()) {
            holder.likeButton.setBackgroundTintList(context.getColorStateList(R.color.colorPrimary));
            holder.likeButton.setTextColor(context.getColor(android.R.color.white));
        } else {
            holder.likeButton.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
            holder.likeButton.setTextColor(context.getColor(android.R.color.black));
        }

        // 加载用户头像
        if (comment.getUserAvatarUrl() != null && !comment.getUserAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(comment.getUserAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.userAvatar);
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 点赞按钮点击事件
        holder.likeButton.setOnClickListener(v -> {
            ApiClient.toggleCommentLike(comment.getId(), new ApiClient.ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean isLiked) {
                    // 更新UI需要在主线程中执行
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (isLiked) {
                            comment.setLikeCount(comment.getLikeCount() + 1);
                            comment.setLikedByCurrentUser(true);
                            holder.likeButton.setBackgroundTintList(context.getColorStateList(R.color.colorPrimary));
                            holder.likeButton.setTextColor(context.getColor(android.R.color.white));
                        } else {
                            comment.setLikeCount(comment.getLikeCount() - 1);
                            comment.setLikedByCurrentUser(false);
                            holder.likeButton.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
                            holder.likeButton.setTextColor(context.getColor(android.R.color.black));
                        }
                        holder.likeButton.setText(String.format("Likes (%d)", comment.getLikeCount()));
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        // 回复按钮点击事件
        holder.replyButton.setOnClickListener(v -> {
            if (replyClickListener != null) {
                replyClickListener.onReplyClick(comment);
            }
        });

        // 查看用户资料按钮点击事件
        holder.viewProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("USER_ID", comment.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        this.commentList.add(0, comment);
        notifyItemInserted(0);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userAvatar;
        TextView userName, commentTime, commentContent;
        Button likeButton, replyButton, viewProfileButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            commentTime = itemView.findViewById(R.id.comment_time);
            commentContent = itemView.findViewById(R.id.comment_content);
            likeButton = itemView.findViewById(R.id.like_button);
            replyButton = itemView.findViewById(R.id.reply_button);
            viewProfileButton = itemView.findViewById(R.id.view_profile_button);
        }
    }
}