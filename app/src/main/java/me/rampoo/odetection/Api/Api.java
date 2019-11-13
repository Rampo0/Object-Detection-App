package me.rampoo.odetection.Api;

import me.rampoo.odetection.Models.LoginResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("/api/login/")
    Call<ResponseBody> Login(@Field("username") String username,
                             @Field("password") String password);

}
