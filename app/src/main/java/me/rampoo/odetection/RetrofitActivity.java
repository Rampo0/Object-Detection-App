package me.rampoo.odetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import me.rampoo.odetection.Api.RetrofitClient;
//import me.rampoo.odetection.Models.LoginResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        setTitle("Retrofit");

        retrofit2.Call<ResponseBody> call = RetrofitClient.GetInstance().GetApi().Login("admin", "yeay");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String s = response.body().string();
                    Toast.makeText(RetrofitActivity.this , "asd" , Toast.LENGTH_LONG ).show();
                }catch (IOException e){
                    Log.d("Api" , "Hello");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
