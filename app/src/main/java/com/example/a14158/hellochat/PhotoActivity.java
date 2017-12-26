package com.example.a14158.hellochat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class PhotoActivity extends AppCompatActivity {
    public static int PHOTO_OK = 1;
    private String photo_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ImageView picture = (ImageView)findViewById(R.id.picture_imageView);
        Button cancel = (Button)findViewById(R.id.photo_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button ok_photo = (Button)findViewById(R.id.photo_ok_button);
        ok_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击确认返回到聊天界面，传递图片路径，联系人的头像和点击确认的标识
                Intent intent = new Intent(PhotoActivity.this,ChatActivity.class);
                intent.putExtra("photo_ok",PHOTO_OK);
                intent.putExtra("photo_path",photo_path);//将图片的路径传递回到ChatActivity中
                intent.putExtra("name",getIntent().getStringExtra("name"));
                startActivity(intent);
                finish();
            }
        });
        if (getIntent().getIntExtra("flag",0) == ChatActivity.TAKE_PHOTO){//如果图片来源照相机，获得intent传递过来的图片的Uri imageUri
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(getIntent().getStringExtra("imageUri"))));
                picture.setImageBitmap(bitmap);
                photo_path = getIntent().getStringExtra("outputImage_path");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (getIntent().getIntExtra("flag",0) == ChatActivity.CHOOSE_PHOTO){//如果图片来源相册，获取照片的路径
            Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("imagePath"));
            picture.setImageBitmap(bitmap);
            photo_path = getIntent().getStringExtra("imagePath");
        }else
            Toast.makeText(this,"can not get the image",Toast.LENGTH_SHORT).show();
    }
}
