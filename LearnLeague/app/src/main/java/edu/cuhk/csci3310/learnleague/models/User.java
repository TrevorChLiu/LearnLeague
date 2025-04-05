package edu.cuhk.csci3310.learnleague.models;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String email;
    private String avatarUrl;
    private String bio;
    private Date joinDate;
    private int coursesCount;
    private int postsCount;
    private int likesReceived;

    // 无参构造函数
    public User() {
    }

    // 带参数的构造函数
    public User(String id, String name, String email, String avatarUrl, String bio, Date joinDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.joinDate = joinDate;
        this.coursesCount = 0;
        this.postsCount = 0;
        this.likesReceived = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public int getCoursesCount() {
        return coursesCount;
    }

    public void setCoursesCount(int coursesCount) {
        this.coursesCount = coursesCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getLikesReceived() {
        return likesReceived;
    }

    public void setLikesReceived(int likesReceived) {
        this.likesReceived = likesReceived;
    }
}