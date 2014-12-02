package kl.animationtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

/**
 * Created by Administrator on 2014/12/2.
 */
public class ObjAnimationTest extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objanimation_test);
        View view = findViewById(R.id.btn);
        ObjAnimation objAnim = new ObjAnimation(view);
        ObjectAnimator animT = ObjectAnimator.ofInt(objAnim,"value",0,1000,0);
        ObjectAnimator animR = ObjectAnimator.ofFloat(objAnim,"rotate",0,1800,0);
        ObjectAnimator animA = ObjectAnimator.ofFloat(objAnim,"alpha",1f,0f,1f);
        ObjectAnimator animS = ObjectAnimator.ofFloat(objAnim,"scale",1f,0f,1f);
        final AnimatorSet animSet = new AnimatorSet();

        animSet.setDuration(5000);
        animSet.play(animT).with(animA).with(animS).before(animR);
        //animSet.setInterpolator(new AnticipateOvershootInterpolator(2.0f));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animSet.start();
            }
        });

    }

    static class ObjAnimation{
        int value;
        View view;
        float rotate;
        float scale;
        float alpha;

        public ObjAnimation(View view) {
            this.view = view;
        }

        public float getAlpha() {
            return alpha;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
            view.setAlpha(alpha);
        }

        public float getRotate() {
            return rotate;
        }

        public void setRotate(float rotate) {
            this.rotate = rotate;
            view.setRotation(rotate);
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
            view.setScaleX(scale);
            view.setScaleY(scale);
        }

        int getValue(){
            return value;
        }

        public void setValue(int value) {
            this.value = value;
            view.setTranslationX(value);
            //Log.i("anim1",value + "");
        }
    }
}
