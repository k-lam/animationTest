package kl.animationtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2014/11/25.
 */
public class mainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mainActivity.this,WatchCameraActivity.class);
                startActivity(intent);
            }
        });

       final ImageView imageView = (ImageView)findViewById(R.id.img);
       // File file = new File(getFilesDir().getPath() + "/tmp.jpg");
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("tmp.jpg"));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("tmp.jpg"));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
