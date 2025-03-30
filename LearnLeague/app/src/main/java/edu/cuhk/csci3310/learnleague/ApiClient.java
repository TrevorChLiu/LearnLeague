package edu.cuhk.csci3310.learnleague;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
                "".toString(),
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

    public static void createUser(String userID, int hashedPassword) {
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

    public static void updateUser(User user) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("userid", user.getUserID());
            json.put("hashedpassword", user.getHashedPassword());
            json.put("username", user.getUserName());
            json.put("email", user.getUserEmail());
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

    public static void getUser(String userID) {
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

                        User.getUser().userSetup(
                                userID,
                                jsonResponse.getInt("hashedpassword"),
                                jsonResponse.getString("username"),
                                jsonResponse.getString("email")
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

    public static void getAvatar(String userID) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + "/get_avatar/" + userID)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    User.setAvatar(bitmap);
                } else {
                        Log.d("Failed to fetch user info from the server:", "This might be due to user has no avatar");
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

}

