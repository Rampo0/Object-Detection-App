package me.rampoo.odetection.Api;

import android.util.Base64;

//import me.rampoo.odetection.Models.LoginResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("/api/login/")
    Call<ResponseBody> Login(@Field("username") String username,
                             @Field("password") String password);

//    @Headers({"Accept: application/json"})
//    @FormUrlEncoded
//    @POST("api/dataset/3/weapon")
//    Call<ResponseBody> Upload(@Field("image") String imageString);

//    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/image/store/")
    Call<ResponseBody> Upload(@Field("image") String imageString);

}
