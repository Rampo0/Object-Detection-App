package me.rampoo.odetection;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import me.rampoo.odetection.Api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{

    Button b1;
    ImageView iv;
    Button uploadBtn;
    private Button colorDetectBtn;
    private Button templateMatchingBtn;
    private static final int kodekamera = 222;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 223;
    private String finalImageString;
    private String stringImageBase64;
    private String prefix = "data:image/png;base64,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        askWritePermission();

        b1 = (Button) findViewById(R.id.button);
        iv = (ImageView) findViewById(R.id.imageView);
        uploadBtn = findViewById(R.id.uploadBtn);
        colorDetectBtn = findViewById(R.id.colorDetectBtn);

        colorDetectBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);

        templateMatchingBtn = findViewById(R.id.templateMatchBtn);
        templateMatchingBtn.setOnClickListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, kodekamera);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (kodekamera):
                    try {
                        prosesKamera(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }}
    }

    private void prosesKamera(Intent datanya) throws IOException{
        Bitmap bm;
        bm = (Bitmap) datanya.getExtras().get("data");
        iv.setImageBitmap(bm);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray(); // convert camera photo to byte array

        // convert to base64
//        finalImageString = Base64.encodeToString(byteArray , Base64.DEFAULT);
        stringImageBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);

//        finalImageString = prefix.concat(stringImageBase64);

//        Toast.makeText(CameraActivity.this , prefix + stringImageBase64, Toast.LENGTH_LONG).show();

        // save it in your external storage.
//        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "HasilFoto_Kamera");
//        imagesFolder.mkdirs();
//        Date d = new Date();
//        CharSequence s  = DateFormat.format("yyyyMMdd-hh-mm-ss", d.getTime());
//        File output = new File(imagesFolder, s.toString() + "foto.png" );
//        FileOutputStream fo = new FileOutputStream(output);
//        fo.write(byteArray);
//        fo.flush();
//        fo.close();

//        Toast.makeText(this,"Data Telah Terload ke ImageView",Toast.LENGTH_SHORT).show();

    }

    private void askWritePermission() {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);}
        }
    }

    @Override
    public void onClick(View v) {
        if(v == uploadBtn){

            Picasso.get().load("http://10.151.254.116/static/res.png").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(iv);

//            Toast.makeText(CameraActivity.this , prefix + stringImageBase64, Toast.LENGTH_SHORT).show();

//            retrofit2.Call<ResponseBody> call = RetrofitClient.GetInstance().GetApi().Upload(prefix + stringImageBase64);
            retrofit2.Call<ResponseBody> call = RetrofitClient.GetInstance().GetApi().Upload(stringImageBase64);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if(response.body() == null){
                            String s = response.errorBody().string();
                            Toast.makeText(CameraActivity.this , s , Toast.LENGTH_LONG).show();
                        }else{
                            String s = response.body().string();
                            Toast.makeText(CameraActivity.this , s , Toast.LENGTH_LONG).show();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(CameraActivity.this , t.getMessage() , Toast.LENGTH_LONG).show();
//                    Toast.makeText(CameraActivity.this , "Upload Failed !! " , Toast.LENGTH_LONG).show();

                }
            });
        }else if(v == colorDetectBtn){
            startActivity(new Intent(CameraActivity.this , MainActivity.class));
        }else if(v == templateMatchingBtn){
            startActivity(new Intent(CameraActivity.this , TemplateMatchingActivity.class));
        }
    }
}
