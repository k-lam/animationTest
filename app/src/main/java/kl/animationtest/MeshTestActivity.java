package kl.animationtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

/**
 * Created by Administrator on 2014/11/28.
 */
public class MeshTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meshtest);

        MeshImageView img = (MeshImageView)findViewById(R.id.img);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.watch);

        img.setBitmap(bitmap);
    }
}
