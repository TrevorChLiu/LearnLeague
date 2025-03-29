package edu.cuhk.csci3310.learnleague;

public class User {
    private String userID;
    private String userName;
    private String userEmail;

    private int numFollowers;
    private int numFollowing;

    public User(String userID) {
        this.userID = userID;
        userName = "Trevor Liu";
        userEmail = "trevor_chenhe_liu@foxmail.com";
        numFollowers = 1200;
        numFollowing = 100;
    }

    public static void createUser(String userID, String password) {
        ApiClient.createUser(userID, password.hashCode());
    }

    public int getNumFollowing() {
        return numFollowing;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }
}
