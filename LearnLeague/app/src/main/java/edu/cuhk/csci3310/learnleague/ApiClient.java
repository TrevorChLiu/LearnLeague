package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Used for communicating with remote database
 */
public class ApiClient {
    // remote server's address
    private static final String BASE_URL = "http://192.168.31.41:5000";

    /**
     * Initialize the database
     */
    public static void initializeDB() {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(
                "",
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/initialize_db")
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
     * Write the new user's info into database.
     * @param userID New user's id.
     * @param hashedPassword Hashed password of the new user.
     */
    public static void createUser(String userID, String hashedPassword) {
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

    /**
     * Setup a user given user id. This is asynchronous.
     * @param userID User id.
     */
    public static void getCurrentUser(String userID) {
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
                                jsonResponse.getInt("avatarversion")
                        );
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
                                    userArray.getInt(4)
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

}

