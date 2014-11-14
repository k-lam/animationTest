package kl.animationtest;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;


public class MyActivity extends Activity implements View.OnClickListener{
    ValueAnimator valueAnimator;
    Interpolator interpolator = new DecelerateInterpolator();
    View mBtn_pv,mBtn_vv;
    final static int DISTANCE = 1000;
    final static int DURATION = 1000;
    ViewPropertyAnimator animator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mBtn_pv = findViewById(R.id.btnPv);
        mBtn_vv = findViewById(R.id.btnVv);
        valueAnimator = ValueAnimator.ofInt(0,DISTANCE);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setInterpolator(interpolator);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBtn_pv.setTranslationY(((Integer)valueAnimator.getAnimatedValue()));
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mBtn_pv.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
       // animator = mBtn_vv.animate().setInterpolator(interpolator).setDuration(DURATION).translationY(DISTANCE);
        mBtn_pv.setOnClickListener(this);
        mBtn_vv.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPv:
                valueAnimator.start();
                break;
            case R.id.btnVv:
                mBtn_vv.animate().setInterpolator(interpolator).setDuration(DURATION).translationY(DISTANCE).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mBtn_vv.setTranslationY(0);
                    }
                });
               // animator.start();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
