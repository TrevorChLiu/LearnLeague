package edu.cuhk.csci3310.learnleague;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Used for communicating with remote database
 */
public class ApiClient {
    // remote server's address
    private static final String BASE_URL = "http://10.0.2.2:5000";

    public static void testConn() {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            json.put("message", "Welcome!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
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
}

