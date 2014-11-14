package kl.animationtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;

/**
 * Created by Administrator on 2014/11/13.
 */
public class FlingView extends View {

    OverScroller mScroller;

    public FlingView(Context context) {
        super(context);
        init();
    }

    public FlingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        mScroller = new OverScroller(getContext());
    }

    public void fling(int startY,int velocityY){
        Log.i("ty1","startY:"+startY + ",velocityY:" + velocityY);
        mScroller.fling(0,startY,0,velocityY,0,0,-10000,10000,0,0);
    }

    @Override
    public void computeScroll() {
        Log.i("ty1", mScroller.getCurrY() + "");
        setTranslationY(mScroller.getCurrY());
    }

    public void stop(){
        mScroller.forceFinished(true);
    }
}
