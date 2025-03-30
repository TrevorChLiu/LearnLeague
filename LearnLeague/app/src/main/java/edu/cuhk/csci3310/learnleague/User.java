package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private int hashedPassword;
    private Bitmap avatar;

    private int numFollowers;
    private int numFollowing;

    private static User user;

    /**
     * A helper class only be called by another Constructor
     */
    private User() {
        // Simply do nothing
    }

    protected void userSetup(String userID, int hashedPassword, String userName, String userEmail) {
        User.user.userID = userID;
        User.user.hashedPassword = hashedPassword;
        User.user.userName = userName;
        User.user.userEmail = userEmail;
    }

    public static void setAvatar(Bitmap avatar) {
        User.user.avatar = avatar;
    }

    public static User getUser() {
        return user;
    }

    public static void initUser(String userID) {
        user = new User();
        user.userID = userID;

        ApiClient.getUser(userID);
    }



    public static void createUser(String userID, String password) {
        ApiClient.createUser(userID, password.hashCode());
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
        ApiClient.updateAvatar(userID, newAvatar);
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

