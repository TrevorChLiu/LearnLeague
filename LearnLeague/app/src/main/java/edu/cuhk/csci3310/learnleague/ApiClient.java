package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import edu.cuhk.csci3310.learnleague.models.Comment;
import edu.cuhk.csci3310.learnleague.models.Post;
// import edu.cuhk.csci3310.learnleague.models.User;

/**
 * Used for communicating with remote database
 */
public class ApiClient {
    // remote server's address
    public static final String BASE_URL = "http://192.168.31.41:5000";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    /**
     * Write the new user's info into database.
     * @param userID New user's id.
     * @param hashedPassword Hashed password of the new user.
     */
    public static void createUser(String userID, String hashedPassword, OnUserCreationResultListener listener) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("userid", userID);
            json.put("hashedpassword", hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/create_user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.onResult(false);
                } else if (response.code() == 409) {
                    listener.onResult(true);
                } else
                    System.out.println(response.body().string());
            }
        });
    }

    /**
     * Update all information of a user.
     * @param user User item.
     */
    public static void updateUser(User user) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("userid", user.getUserID());
            json.put("hashedpassword", user.getHashedPassword());
            json.put("username", user.getUserName());
            json.put("email", user.getUserEmail());
            json.put("avatarversion", user.getAvatarVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/update_user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    public static void getUserForProcess(String userID, OnSingleUserLoadedListener listener) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("userid", userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/get_user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());

                        User user = new User(userID,
                                jsonResponse.getString("hashedpassword"),
                                jsonResponse.getString("username"),
                                jsonResponse.getString("email"),
                                jsonResponse.getLong("avatarversion"),
                                jsonResponse.getInt("numfollowee"),
                                jsonResponse.getInt("numfollower"));
                        if (listener != null)
                            listener.onLoaded(user);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else if (response.code() == 404) {
                    if (listener != null)
                        listener.onLoaded(null);
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        Log.e("Failed to fetch user info from the server:", jsonResponse.getString("message"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * Setup a user given user id. This is asynchronous.
     * @param userID User id.
     */
    public static void getCurrentUser(String userID, OnSingleUserLoadedListener listener) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("userid", userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/get_user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());

                        User.getCurrentUser().userSetup(
                                userID,
                                jsonResponse.getString("hashedpassword"),
                                jsonResponse.getString("username"),
                                jsonResponse.getString("email"),
                                jsonResponse.getLong("avatarversion"),
                                jsonResponse.getInt("numfollowee"),
                                jsonResponse.getInt("numfollower")
                        );
                        if (listener != null)
                            listener.onLoaded(User.getCurrentUser());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else if (response.code() == 404) {
                    if (listener != null)
                        listener.onLoaded(null);
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        Log.e("Failed to fetch user info from the server:", jsonResponse.getString("message"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * Retrieve a follows list base on the method
     * @param userID The user id
     * @param method Either "followee" or "follower"
     * @param listener What to do after getting the list
     */
    public static void getFollowsList(String userID, String method, OnFollowsListLoadedListener listener) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        LinkedList<User> followsList = new LinkedList<>();

        try {
            json.put("userid", userID);
            json.put("method", method);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/get_follows_list")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonResponse = new JSONArray(responseBody);

                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONArray userArray = jsonResponse.getJSONArray(i);

                            User user = new User(
                                    userArray.getString(0),
                                    userArray.getString(1),
                                    userArray.getString(2),
                                    userArray.getString(3),
                                    userArray.getLong(4),
                                    userArray.getInt(5),
                                    userArray.getInt(6)
                            );

                            followsList.add(user);
                        }
                        if (listener != null)
                            listener.onFollowsListLoaded(followsList);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        Log.e("Failed to fetch user info from the server:", jsonResponse.getString("message"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }


    public static void updateAvatar(String userID, Bitmap bitmap) {
        OkHttpClient client = new OkHttpClient();

        // Convert the Bitmap to a byte array (PNG format)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] avatarBytes = byteArrayOutputStream.toByteArray();

        RequestBody body = RequestBody.create(MediaType.parse("image/jpg"), avatarBytes);

        Request request = new Request.Builder()
                .url(BASE_URL + "/update_avatar/" + userID)
                .put(body)  // Sending the raw byte array directly
                .build();

        // Make the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Avatar Update", "Avatar updated successfully");
                } else {
                    Log.e("Avatar Update", "Failed to update avatar: " + response.message());
                }
            }
        });
    }

    /**
     * Make the follower follows/unfollows the followee based on the method.
     * @param followerID The one to follow another.
     * @param followeeID The one to be followed.
     * @param method Either "follow" or "unfollow"
     */
    public static void updateFollow(String followerID, String followeeID, String method) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("followerid", followerID);
            json.put("followeeid", followeeID);
            json.put("method", method);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/update_follow")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    /**
     * Add a study record to database.
     * @param userID The user ID to be added.
     * @param seconds Time of study in seconds.
     */
    public static void insertStudyRecordToday(String userID, int seconds) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("userid", userID);
            json.put("seconds", seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/insert_study_record_today")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    /**
     * Get study record, and the result will be set to User class.
     * @param listener Optional listener to act after getting the result.
     */
    public static void getStudyRecords(OnRankingLoadedListener listener) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + "/get_study_records") // No query params
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        LinkedList<User> dayRanking = new LinkedList<>();
                        LinkedList<User> weekRanking = new LinkedList<>();
                        LinkedList<User> monthRanking = new LinkedList<>();

                        String responseBody = response.body().string();
                        JSONArray jsonResponse = new JSONArray(responseBody);

                        parseUserList(jsonResponse.getJSONArray(0), dayRanking);
                        parseUserList(jsonResponse.getJSONArray(1), weekRanking);
                        parseUserList(jsonResponse.getJSONArray(2), monthRanking);

                        if (listener != null) {
                            listener.onLoaded(dayRanking, weekRanking, monthRanking);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e("Failed to get study records", response.message());
                }
            }
        });
    }

    /**
     * Helper function to parse the json array into User.
     * @param jsonArray json array from the server.
     * @param ranking A list to hold these users.
     * @throws JSONException In case of error.
     */
    private static void parseUserList(JSONArray jsonArray, LinkedList<User> ranking) throws JSONException {
        ranking.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray userArray = jsonArray.getJSONArray(i);
            User user = new User(
                    userArray.getString(0),
                    userArray.getString(1),
                    userArray.getString(2),
                    userArray.getString(3),
                    userArray.getLong(4),
                    userArray.getInt(5),
                    userArray.getInt(6)
            );
            user.setTmp_seconds(userArray.getInt(7));
            ranking.add(user);
        }
    }

    // 获取帖子列表
    public static void getPosts(ApiCallback<List<Post>> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/posts")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
                    List<Post> posts = new ArrayList<>();

                    for (int i = 0; i < jsonPosts.length(); i++) {
                        JSONObject jsonPost = jsonPosts.getJSONObject(i);
                        Post post = new Post();
                        post.setId(jsonPost.getString("id"));
                        post.setTitle(jsonPost.getString("title"));
                        post.setContent(jsonPost.getString("content"));
                        post.setUserId(jsonPost.getString("userId"));
                        post.setUserName(jsonPost.getString("userName"));
                        post.setUserAvatarUrl(jsonPost.getString("userAvatarUrl"));
                        post.setUserAvatarVersion(jsonPost.getLong("userAvatarVersion"));
                        post.setCreatedAt(new Date(jsonPost.getLong("createdAt")));
                        post.setLikeCount(jsonPost.getInt("likeCount"));
                        post.setCommentCount(jsonPost.getInt("commentCount"));
                        post.setLikedByCurrentUser(jsonPost.getBoolean("isLikedByCurrentUser"));
                        posts.add(post);
                    }

                    callback.onSuccess(posts);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // get Post detail
    public static void getPostDetail(String postId, ApiCallback<Post> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/posts/" + postId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONObject jsonPost = jsonResponse.getJSONObject("post");

                    Post post = new Post();
                    post.setId(jsonPost.getString("id"));
                    post.setTitle(jsonPost.getString("title"));
                    post.setContent(jsonPost.getString("content"));
                    post.setUserId(jsonPost.getString("userId"));
                    post.setUserName(jsonPost.getString("userName"));
                    post.setUserAvatarUrl(jsonPost.getString("userAvatarUrl"));
                    post.setUserAvatarVersion(jsonPost.getLong("userAvatarVersion"));
                    post.setCreatedAt(new Date(jsonPost.getLong("createdAt")));
                    post.setLikeCount(jsonPost.getInt("likeCount"));
                    post.setCommentCount(jsonPost.getInt("commentCount"));
                    post.setLikedByCurrentUser(jsonPost.getBoolean("isLikedByCurrentUser"));

                    callback.onSuccess(post);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // 创建新帖子
    public static void createPost(String title, String content, ApiCallback<Post> callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", User.getCurrentUser().getUserID());
            jsonBody.put("username", User.getCurrentUser().getUserName());
            jsonBody.put("userAvatarUrl", ApiClient.BASE_URL + "/get_avatar/" + User.getCurrentUser().getUserID());
            jsonBody.put("userAvatarVersion", User.getCurrentUser().getAvatarVersion());
            jsonBody.put("title", title);
            jsonBody.put("content", content);
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/posts")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONObject jsonPost = jsonResponse.getJSONObject("post");

                    Post post = new Post();
                    post.setId(jsonPost.getString("id"));
                    post.setTitle(jsonPost.getString("title"));
                    post.setContent(jsonPost.getString("content"));
                    post.setUserId(jsonPost.getString("userId"));
                    post.setUserName(jsonPost.getString("userName"));
                    post.setUserAvatarUrl(jsonPost.getString("userAvatarUrl"));
                    post.setCreatedAt(new Date(jsonPost.getLong("createdAt")));
                    post.setLikeCount(0);
                    post.setCommentCount(0);
                    post.setLikedByCurrentUser(false);

                    callback.onSuccess(post);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // 获取帖子评论
    public static void getComments(String postId, ApiCallback<List<Comment>> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/posts/" + postId + "/comments")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray jsonComments = jsonResponse.getJSONArray("comments");
                    List<Comment> comments = new ArrayList<>();

                    for (int i = 0; i < jsonComments.length(); i++) {
                        JSONObject jsonComment = jsonComments.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setId(jsonComment.getString("id"));
                        comment.setPostId(jsonComment.getString("postId"));
                        comment.setContent(jsonComment.getString("content"));
                        comment.setUserId(jsonComment.getString("userId"));
                        comment.setUserName(jsonComment.getString("userName"));
                        comment.setUserAvatarUrl(jsonComment.getString("userAvatarUrl"));
                        comment.setUserAvatarVersion(jsonComment.getLong("userAvatarVersion"));
                        comment.setCreatedAt(new Date(jsonComment.getLong("createdAt")));
                        comment.setLikeCount(jsonComment.getInt("likeCount"));
                        comment.setLikedByCurrentUser(jsonComment.getBoolean("isLikedByCurrentUser"));
                        if (!jsonComment.isNull("parentCommentId")) {
                            comment.setParentCommentId(jsonComment.getString("parentCommentId"));
                        }
                        comments.add(comment);
                    }

                    callback.onSuccess(comments);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // 添加评论
    public static void addComment(String postId, String content, String parentCommentId, ApiCallback<Comment> callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", User.getCurrentUser().getUserID());
            jsonBody.put("username", User.getCurrentUser().getUserName());
            jsonBody.put("userAvatarUrl", ApiClient.BASE_URL + "/get_avatar/" + User.getCurrentUser().getUserID());
            jsonBody.put("userAvatarVersion", User.getCurrentUser().getAvatarVersion());
            jsonBody.put("postId", postId);
            jsonBody.put("content", content);
            if (parentCommentId != null) {
                jsonBody.put("parentCommentId", parentCommentId);
            }
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/comments")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONObject jsonComment = jsonResponse.getJSONObject("comment");

                    Comment comment = new Comment();
                    comment.setId(jsonComment.getString("id"));
                    comment.setPostId(jsonComment.getString("postId"));
                    comment.setContent(jsonComment.getString("content"));
                    comment.setUserId(jsonComment.getString("userId"));
                    comment.setUserName(jsonComment.getString("userName"));
                    comment.setUserAvatarUrl(jsonComment.getString("userAvatarUrl"));
                    comment.setUserAvatarVersion(jsonComment.getLong("userAvatarVersion"));
                    comment.setCreatedAt(new Date(jsonComment.getLong("createdAt")));
                    comment.setLikeCount(0);
                    comment.setLikedByCurrentUser(false);
                    if (!jsonComment.isNull("parentCommentId")) {
                        comment.setParentCommentId(jsonComment.getString("parentCommentId"));
                    }

                    callback.onSuccess(comment);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // 点赞/取消点赞帖子
    public static void togglePostLike(String postId, ApiCallback<Boolean> callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("postId", postId);
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/posts/" + postId + "/like")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    boolean isLiked = jsonResponse.getBoolean("isLiked");
                    callback.onSuccess(isLiked);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }



    // 点赞/取消点赞评论
    public static void toggleCommentLike(String commentId, ApiCallback<Boolean> callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("commentId", commentId);
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/comments/" + commentId + "/like")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    boolean isLiked = jsonResponse.getBoolean("isLiked");
                    callback.onSuccess(isLiked);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // 获取用户发布的帖子
    public static void getUserPosts(String userId, ApiCallback<List<Post>> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/users/" + userId + "/posts")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
                    List<Post> posts = new ArrayList<>();

                    for (int i = 0; i < jsonPosts.length(); i++) {
                        JSONObject jsonPost = jsonPosts.getJSONObject(i);
                        Post post = new Post();
                        post.setId(jsonPost.getString("id"));
                        post.setTitle(jsonPost.getString("title"));
                        post.setContent(jsonPost.getString("content"));
                        post.setUserId(jsonPost.getString("userId"));
                        post.setUserName(jsonPost.getString("userName"));
                        post.setUserAvatarUrl(jsonPost.getString("userAvatarUrl"));
                        post.setUserAvatarVersion(jsonPost.getLong("userAvatarVersion"));
                        post.setCreatedAt(new Date(jsonPost.getLong("createdAt")));
                        post.setLikeCount(jsonPost.getInt("likeCount"));
                        post.setCommentCount(jsonPost.getInt("commentCount"));
                        post.setLikedByCurrentUser(jsonPost.getBoolean("isLikedByCurrentUser"));
                        posts.add(post);
                    }
                    callback.onSuccess(posts);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    public static void getUserComments(String userId, ApiCallback<List<Comment>> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/users/" + userId + "/comments")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray jsonComments = jsonResponse.getJSONArray("comments");
                    List<Comment> comments = new ArrayList<>();

                    for (int i = 0; i < jsonComments.length(); i++) {
                        JSONObject jsonComment = jsonComments.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setId(jsonComment.getString("id"));
                        comment.setPostId(jsonComment.getString("postId"));
                        comment.setContent(jsonComment.getString("content"));
                        comment.setUserId(jsonComment.getString("userId"));
                        comment.setUserName(jsonComment.getString("userName"));
                        comment.setUserAvatarUrl(jsonComment.getString("userAvatarUrl"));
                        comment.setUserAvatarVersion(jsonComment.getLong("userAvatarVersion"));
                        comment.setCreatedAt(new Date(jsonComment.getLong("createdAt")));
                        comment.setLikeCount(jsonComment.getInt("likeCount"));
                        comment.setLikedByCurrentUser(jsonComment.getBoolean("isLikedByCurrentUser"));
                        if (!jsonComment.isNull("parentCommentId")) {
                            comment.setParentCommentId(jsonComment.getString("parentCommentId"));
                        }
                        comments.add(comment);
                    }

                    callback.onSuccess(comments);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    public static void getUserReplies(String userId, ApiCallback<List<Comment>> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/users/" + userId + "/replies")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray jsonComments = jsonResponse.getJSONArray("replies");
                    List<Comment> comments = new ArrayList<>();

                    for (int i = 0; i < jsonComments.length(); i++) {
                        JSONObject jsonComment = jsonComments.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setId(jsonComment.getString("id"));
                        comment.setPostId(jsonComment.getString("postId"));
                        comment.setContent(jsonComment.getString("content"));
                        comment.setUserId(jsonComment.getString("userId"));
                        comment.setUserName(jsonComment.getString("userName"));
                        comment.setUserAvatarUrl(jsonComment.getString("userAvatarUrl"));
                        comment.setUserAvatarVersion(jsonComment.getLong("userAvatarVersion"));
                        comment.setCreatedAt(new Date(jsonComment.getLong("createdAt")));
                        comment.setLikeCount(jsonComment.getInt("likeCount"));
                        comment.setLikedByCurrentUser(jsonComment.getBoolean("isLikedByCurrentUser"));
                        if (!jsonComment.isNull("parentCommentId")) {
                            comment.setParentCommentId(jsonComment.getString("parentCommentId"));
                        }
                        comments.add(comment);
                    }

                    callback.onSuccess(comments);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
}

