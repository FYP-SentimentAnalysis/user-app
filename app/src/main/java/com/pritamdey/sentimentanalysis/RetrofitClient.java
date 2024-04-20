package com.pritamdey.sentimentanalysis;

import static com.pritamdey.sentimentanalysis.App.baseURL;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static ApiInterface getClient(){
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    public static String parseError(@Nullable ResponseBody response) {
        if (response != null) {
            try {
                JSONObject obj = new Gson().fromJson(response.string(), JSONObject.class);
                return obj.getString("message");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Something went wrong!";
            }
        } else {
            return "Server not responding!";
        }
    }
}
