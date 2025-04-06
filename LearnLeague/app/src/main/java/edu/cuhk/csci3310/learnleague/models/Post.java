package edu.cuhk.csci3310.learnleague.models;

import java.util.Date;

public class Post {
    private String id;
    private String title;
    private String content;
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private long userAvatarVersion;
    private Date createdAt;
    private int likeCount;
    private int commentCount;
    private boolean isLikedByCurrentUser;

    // 无参构造函数
    public Post() {
    }

    // 带参数的构造函数
    public Post(String id, String title, String content, String userId, String userName,
                String userAvatarUrl, long userAvatarVersion, Date createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
        this.userAvatarVersion = userAvatarVersion;
        this.createdAt = createdAt;
        this.likeCount = 0;
        this.commentCount = 0;
        this.isLikedByCurrentUser = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setUserAvatarVersion(long userAvatarVersion) {
        this.userAvatarVersion = userAvatarVersion;
    }

    public long getUserAvatarVersion() {
        return userAvatarVersion;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }
}