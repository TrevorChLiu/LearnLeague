package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Timestamp;



public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private String hashedPassword;
    // Used to control Glide's cache
    private long avatarVersion;

    private int numFollowers = 0;
    private int numFollowing = 0;


    private static User user;

    private LinkedList<User> followingList = new LinkedList<User>();
    private LinkedList<User> followersList = new LinkedList<User>();

    public LinkedList<User> getFollowingList() {
        return followingList;
    }
    public LinkedList<User> getFollowersList() {
        return followersList;
    }

    /**
     * A helper class only be called by another Constructor
     */
    private User() {
        // Simply do nothing
    }

    /**
     * Return true if the other user is followed by current user
     * @param other other user
     * @return other user is followed by current user or not
     */
    public boolean hasFollowed(User other) {
        return followingList.contains(other);
    }

    /**
     * Initialize the user of current account.
     * @param userID User id of current account.
     * @param hashedPassword Hashed password of current account.
     * @param userName Current account name.
     * @param userEmail Current account emial.
     * @param avatarVersion Current account avatar updated time in long.
     * @param numFollowing Number of users followed by current account.
     * @param numFollowers Number of users following current account.
     */
    protected void userSetup(String userID, String hashedPassword, String userName, String userEmail,
                             long avatarVersion, int numFollowing, int numFollowers) {
        User.user.userID = userID;
        User.user.hashedPassword = hashedPassword;
        User.user.userName = userName;
        User.user.userEmail = userEmail;
        User.user.avatarVersion = avatarVersion;
        User.user.numFollowing = numFollowing;
        User.user.numFollowers = numFollowers;
    }

    public User(String userID, String hashedPassword, String userName, String userEmail, long avatarVersion,
                int numFollowing, int numFollowers) {
        this.userID = userID;
        this.hashedPassword = hashedPassword;
        this.userName = userName;
        this.userEmail = userEmail;
        this.avatarVersion = avatarVersion;
        this.numFollowing = numFollowing;
        this.numFollowers = numFollowers;
    }

    /**
     * Return the user running this app
     * @return The user running this app
     */
    public static User getCurrentUser() {
        return user;
    }

    public static void getFollowingList(String userID,  OnFollowsListLoadedListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getFollowsList(userID, "followee", listener);
        });
    }

    public static void getFollowersList(String userID, OnFollowsListLoadedListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getFollowsList(userID, "follower", listener);
        });
    }

    public void setFollowingList(LinkedList<User> followingList) {
        this.followingList.clear();
        this.followingList.addAll(followingList);
    }

    public void setFollowersList(LinkedList<User> followersList) {
        this.followersList.clear();
        this.followersList.addAll(followersList);
    }

    public static void initUser(String userID) {
        user = new User();
        user.userID = userID;


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getCurrentUser(userID);
        });
        user.loadFollowsList();
    }

    public static void reloadCurrentUser() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApiClient.getCurrentUser(User.user.getUserID());
        });
    }

    public void loadFollowsList() {
        followingList = new LinkedList<>();
        followersList = new LinkedList<>();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            ApiClient.getFollowsList(userID, "followee", new OnFollowsListLoadedListener() {
                @Override
                public void onFollowsListLoaded(LinkedList<User> followsList) {
                    User.this.setFollowingList(followsList);
                }
            });
        });

        executor.execute(() -> {
            ApiClient.getFollowsList(userID, "follower", new OnFollowsListLoadedListener() {
                @Override
                public void onFollowsListLoaded(LinkedList<User> followsList) {
                    User.this.setFollowersList(followsList);
                }
            });
        });
    }

    /**
     * Let the user running this app follow another
     * @param another The one to be followed
     */
    public static void follow(User another) {
        User.user.followingList.add(another);
        userFollow(user, another);
        User.user.numFollowing++;
    }

    /**
     * Let the user running this app unfollow another
     * @param another The one to be unfollowed
     */
    public static void unfollow(User another) {
        if (User.user.followingList.contains(another))
            User.user.followingList.remove(another);
        userUnfollow(user, another);
        User.user.numFollowing--;
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
                avatarVersion = new Timestamp(System.currentTimeMillis()).getTime();
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

    public long getAvatarVersion() {
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

