package edu.cuhk.csci3310.learnleague.models;

import java.util.Date;

public class Comment {
    private String id;
    private String postId;
    private String content;
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private Date createdAt;
    private int likeCount;
    private boolean isLikedByCurrentUser;
    private String parentCommentId; // 如果是回复其他评论的话

    // 无参构造函数
    public Comment() {
    }

    // 带参数的构造函数
    public Comment(String id, String postId, String content, String userId, String userName,
                   String userAvatarUrl, Date createdAt, String parentCommentId) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
        this.createdAt = createdAt;
        this.likeCount = 0;
        this.isLikedByCurrentUser = false;
        this.parentCommentId = parentCommentId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}