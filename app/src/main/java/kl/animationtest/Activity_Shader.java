package kl.animationtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2014/12/5.
 */
public class Activity_Shader extends Activity{
    GifDrawable gifDrawable = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_shader);
        GifImageView gifImageView = new GifImageView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gifImageView.setLayoutParams(params);
        gifImageView.setImageResource(R.drawable.sleepless);

        try {
            gifDrawable = new GifDrawable(getAssets(), "sleepless.gif");
            gifImageView.setImageDrawable(gifDrawable);
        }catch (IOException exp){
            exp.printStackTrace();
        }
        setContentView(gifImageView);

        gifImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifDrawable.stop();
            }
        }, 5000);
    }
}
