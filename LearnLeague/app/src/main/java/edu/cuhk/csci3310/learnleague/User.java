package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.util.Log;

public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private int hashedPassword;
    private Bitmap avatar;

    private int numFollowers;
    private int numFollowing;

    public User(String userID) {
        this.userID = userID;
        hashedPassword = 12345;
        userName = "Trevor Liu";
        userEmail = "trevor_chenhe_liu@foxmail.com";
        avatar = null;
        numFollowers = 1200;
        numFollowing = 100;
    }

    public static User createUser(String userID, String password) {
        ApiClient.createUser(userID, password.hashCode());
        return new User(userID);
    }

    public void updatePassword(String password) {
        hashedPassword = password.hashCode();
        updateUser();
    }

    public void updateUserName(String newUserName) {
        userName = newUserName;
        updateUser();
    }

    public void updateEmail(String newEmail) {
        userEmail = newEmail;
        updateUser();
    }

    public void updateAvatar(Bitmap newAvatar) {
        avatar = newAvatar;
        updateUser();
    }

    private void updateUser() {
        ApiClient.updateUser(this);
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

    public int getHashedPassword() {
        return hashedPassword;
    }

    public Bitmap getAvatar() {
        return avatar;
    }
}
