package kl.animationtest;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.security.Key;

/**
 * Created by Administrator on 2014/12/2.
 */
public class ObjAnimationTest extends Activity {
    View btn_hs;
    View btn_hs_ctrler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objanimation_test);
        View view = findViewById(R.id.btn);
        btn_hs = findViewById(R.id.btn_hs);
        btn_hs_ctrler = findViewById(R.id.btn_hs_controler);

        ObjAnimation objAnim = new ObjAnimation(view);
        ObjectAnimator animT = ObjectAnimator.ofInt(objAnim,"translation",0,1000,0);
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

        btn_hs_ctrler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(btn_hs.getVisibility() != View.VISIBLE){
                    btn_hs.setVisibility(View.VISIBLE);
                }else {
                    btn_hs.setVisibility(View.GONE);
                }
            }
        });
        Keyframe keyframe0 = Keyframe.ofFloat(0f,0f);
        Keyframe keyframe1 = Keyframe.ofFloat(0.3f,1800f);
        keyframe1.setInterpolator(new AccelerateDecelerateInterpolator());
        Keyframe keyframe2 = Keyframe.ofFloat(0.6f,-1800f);
        keyframe2.setInterpolator(new DecelerateInterpolator());
        Keyframe keyframe3 = Keyframe.ofFloat(1f,1800f);
        keyframe3.setInterpolator(new AnticipateOvershootInterpolator());

        PropertyValuesHolder pvh = PropertyValuesHolder.ofKeyframe("rotate",keyframe0,keyframe1,keyframe2,keyframe3);
        //PropertyValuesHolder pvh = PropertyValuesHolder.ofKeyframe("rotate",Keyframe.ofFloat(0f,0f),keyframe3);
        final ObjectAnimator rotationAnim = ObjectAnimator.ofPropertyValuesHolder(objAnim, pvh);
        rotationAnim.setDuration(10000);
        //rotationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        btn_hs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotationAnim.start();
            }
        });
    }

    static class ObjAnimation{
        int translation;
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
            //Log.i("anim1", "" + rotate);
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

        int getTranslation(){
            return translation;
        }

        public void setTranslation(int translation) {
            this.translation = translation;
            view.setTranslationX(translation);
            //Log.i("anim1",translation + "");
        }
    }

    static class TypeTest implements TypeEvaluator<ObjAnimation>{
        ObjAnimation objAnim;
        public TypeTest(ObjAnimation objAnim) {
            this.objAnim = objAnim;
        }

        @Override
        public ObjAnimation evaluate(float fraction, ObjAnimation startValue, ObjAnimation endValue) {

            return null;
        }
    }
}
