package edu.cuhk.csci3310.learnleague;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cuhk.csci3310.learnleague.models.Comment;
import edu.cuhk.csci3310.learnleague.models.Post;
import edu.cuhk.csci3310.learnleague.models.User;

/**
 * Used for communicating with remote database
 */
public class ApiClient {
    // remote server's address
    private static final String BASE_URL = "http://10.0.2.2:3000";
    // private static final String BASE_URL = "http://10.0.2.2:3000";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public static void testConn() {
        JSONObject json = new JSONObject();

        try {
            json.put("message", "Welcome!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                JSON
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/test_conn")
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

    // 获取用户资料
    public static void getUserProfile(String userId, ApiCallback<User> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/users/" + userId)
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
                    JSONObject jsonUser = jsonResponse.getJSONObject("user");

                    User user = new User();
                    user.setId(jsonUser.getString("id"));
                    user.setName(jsonUser.getString("name"));
                    user.setEmail(jsonUser.getString("email"));
                    user.setAvatarUrl(jsonUser.getString("avatarUrl"));
                    user.setBio(jsonUser.getString("bio"));
                    user.setJoinDate(new Date(jsonUser.getLong("joinDate")));
                    user.setCoursesCount(jsonUser.getInt("coursesCount"));
                    user.setPostsCount(jsonUser.getInt("postsCount"));
                    user.setLikesReceived(jsonUser.getInt("likesReceived"));

                    callback.onSuccess(user);
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
}