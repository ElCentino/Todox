package com.example.todox.services;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationServices {

    public static class WebService {

        private static Retrofit mRetrofit;
        private static String BaseURL = "https://dd456973.ngrok.io";
        private static String API_USERNAME = "test";
        private static String API_PASSWORD = "123456";

        public static Retrofit getInstance() {

            if(mRetrofit == null) {

                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build();

                mRetrofit = new Retrofit.Builder()
                        .baseUrl(BaseURL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            return mRetrofit;
        }

        public static String getAuthToken() {

            byte[] data = new byte[0];

            try {
                data = (API_USERNAME + ":" + API_PASSWORD).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
        }



    }

    public static class Literals {
        public static String ADD_TODO = "0001";
        public static String EDIT_TODO = "0002";
    }

    public enum Constants {

        SUCCESS("SUCCESS", 200),
        REQUESTEND_RELOAD("RELOAD REQUEST", 10),
        BADREQUEST("BAD REQUEST", 400);

        private String key;
        private Object value;

        Constants(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return this.value;
        }
    }
}
