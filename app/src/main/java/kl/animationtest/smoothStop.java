package kl.animationtest;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

public class smoothStop extends Activity {

    FlingView view;
    View viewLy;
    final static int Acceleration = 20000;
    OverScroller mScroller;
    int dis = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smooth_stop);
        view = (FlingView)findViewById(R.id.vv);
        viewLy  = findViewById(R.id.ly);
        //mScroller = new OverScroller(this);
        final Interpolator interpolator = new DecelerateInterpolator();
        final GestureDetector gd = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e) {
                //mScroller.forceFinished(true);
                view.stop();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //distanceX向右负，向左正
                //distanceY向下负，向上正
                //distance是初位置减掉末位置的。
                dis = (int)(view.getTranslationY() - distanceY);
                view.setTranslationY(dis);
                //Log.i("ty1",view.getTranslationY()+" X:"+distanceX+",Y:"+distanceY);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                float t;
//                t = velocityY / Acceleration;
//                int s = (int)(Acceleration * t * t / 2 + view.getTranslationY());
//                if(velocityY <=0){
//                    t = -t;
//                    //s += dis;
//                }
//                Log.d("animFling","v:" + velocityX +" s:" + s + " trans:" +  view.getTranslationY()+ " t:" + t);
//                view.animate().setDuration((int)(t*1000)).setInterpolator(interpolator).translationY(s).start();


                //ValueAnimator valueAnimator = ValueAnimator.ofInt(0,(int)(s * 0.75),1);
                //valueAnimator.setInterpolator();

                //the following snippet using the Scroller
                //参考ScrollView的onTouch处的源码
                view.fling((int)view.getTranslationY(),(int)velocityY);
                view.postInvalidateOnAnimation();
                viewLy.postInvalidateOnAnimation();
                return true;
            }
        });

//        Choreographer choreographer = Choreographer.getInstance();
//        choreographer.postFrameCallback(new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long l) {
//                int ty = mScroller.getCurrY();
//                Log.i("ty1","curT:" + view.getTranslationY() + " ,ty:" + ty);
//                view.setTranslationY(ty);
//            }
//        });

        findViewById(R.id.ly).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smooth_stop, menu);
        return true;
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
