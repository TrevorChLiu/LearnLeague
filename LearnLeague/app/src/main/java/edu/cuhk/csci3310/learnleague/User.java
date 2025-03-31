package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
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

    public User(String userID, String hashedPassword, String userName, String userEmail, int avatarVersion) {
        this.userID = userID;
        this.hashedPassword = hashedPassword;
        this.userName = userName;
        this.userEmail = userEmail;
        this.avatarVersion = avatarVersion;
    }

    /**
     * Return the user running this app
     * @return The user running this app
     */
    public static User getCurrentUser() {
        return user;
    }

    public static void getFollowingList(String userID,  OnDataLoadedListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getFollowsList(userID, "followee", listener);
        });
    }

    public static void getFollowerList(String userID, OnDataLoadedListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getFollowsList(userID, "follower", listener);
        });
    }



    public static void initUser(String userID, OnDataLoadedListener listener) {
        user = new User();
        user.userID = userID;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getCurrentUser(userID);

            // Refresh the page when user data is loaded
            listener.onUserLoaded(getCurrentUser());
        });
    }

    public static void initUser(String userID) {
        user = new User();
        user.userID = userID;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getCurrentUser(userID);
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
     * Let the user running this app unfollow another
     * @param another The one to be unfollowed
     */
    public static void unfollow(User another) {
        userUnfollow(user, another);
    }

    /**
     * Let one user follow another
     * @param follower The one to follow another
     * @param followee The one to be followed
     */
    public static void userFollow(User follower, User followee) {
        ApiClient.updateFollow(follower.getUserID(), followee.getUserID(), "follow");
    }

    /**
     * Let one user unfollow another
     * @param follower The one to unfollow another
     * @param followee The one to be unfollowed
     */
    public static void userUnfollow(User follower, User followee) {
        ApiClient.updateFollow(follower.getUserID(), followee.getUserID(), "unfollow");
    }

    public static void userFollow(String follower_id, String followee_id) {
        ApiClient.updateFollow(follower_id, followee_id, "follow");
    }

    public static void userUnfollow(String follower_id, String followee_id) {
        ApiClient.updateFollow(follower_id, followee_id, "unfollow");
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

    @NonNull
    @Override
    public String toString() {
        return "@" + userID + ": " + userName + ", email: " + userEmail;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return userID.equals(other.userID);
    }

}

