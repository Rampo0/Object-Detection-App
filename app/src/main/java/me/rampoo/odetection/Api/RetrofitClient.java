package me.rampoo.odetection.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private final String BaseURL = "https://rampoo.me";
    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder().baseUrl(BaseURL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized RetrofitClient GetInstance(){
        if(instance == null){
            instance = new RetrofitClient();
        }
        return  instance;
    }

    public Api GetApi(){
        return retrofit.create(Api.class);
    }

}