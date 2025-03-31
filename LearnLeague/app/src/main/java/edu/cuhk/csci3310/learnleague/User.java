package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private String hashedPassword;
    // Used to control Glide's cache
    private int avatarVersion;

    private int numFollowers;
    private int numFollowing;

    private static User user;

    /**
     * A helper class only be called by another Constructor
     */
    private User() {
        // Simply do nothing
    }

    protected void userSetup(String userID, String hashedPassword, String userName, String userEmail, int avatarVersion) {
        User.user.userID = userID;
        User.user.hashedPassword = hashedPassword;
        User.user.userName = userName;
        User.user.userEmail = userEmail;
        User.user.avatarVersion = avatarVersion;
    }

    /**
     * Return the user running this app
     * @return The user running this app
     */
    public static User getUser() {
        return user;
    }

    public static void initUser(String userID, onDataLoadedListener listener) {
        user = new User();
        user.userID = userID;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getUser(userID);

            // Refresh the page when user data is loaded
            listener.onUserLoaded(getUser());
        });
    }

    public static void initUser(String userID) {
        user = new User();
        user.userID = userID;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getUser(userID);

        });
    }

    /**
     * Let the user running this app follow another
     * @param another The one to be followed
     */
    public static void follow(User another) {
        userFollow(user, another);
    }

    /**
     * Let one user follow another
     * @param follower The one to follow another
     * @param followee The one to be followed
     */
    public static void userFollow(User follower, User followee) {

    }

    public static void createUser(String userID, String password) {
        ApiClient.createUser(userID, Encryption.sha256Hash(password));
    }

    public void updatePassword(String password) {
        hashedPassword = Encryption.sha256Hash(password);
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
        if (newAvatar != null) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                ApiClient.updateAvatar(userID, newAvatar);
                avatarVersion++;
                updateUser();
            });
        }
    }

    private void updateUser() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.updateUser(this);
        });

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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public int getAvatarVersion() {
        return avatarVersion;
    }


}

