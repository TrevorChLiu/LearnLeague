package edu.cuhk.csci3310.learnleague;

public class Course {
    private String title; // 標題
    private String thumbnailUrl;
    private long watchTimeInSeconds;
//    private long lastWatchedTimestamp;
    private String videoUrl; // 视频 URL

    public Course(String title, String thumbnailUrl, long watchTimeInSeconds, String videoUrl) {// , long lastWatchedTimestamp
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.watchTimeInSeconds = watchTimeInSeconds;
//        this.lastWatchedTimestamp = lastWatchedTimestamp;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public long getWatchTimeInSeconds() {
        return watchTimeInSeconds;
    }

//    public long getLastWatchedTimestamp() {
//        return lastWatchedTimestamp;
//    }
    public void setWatchTimeInSeconds(long updatedWatchTime) {
        this.watchTimeInSeconds = updatedWatchTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void addWatchTimeInSeconds(long additionalSeconds) {
        this.watchTimeInSeconds += additionalSeconds;
    }
}