package kl.animationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2014/11/13.
 */
public class CoverPlate extends View {
    Paint paint = new Paint();
    public CoverPlate(Context context) {
        super(context);
    }

    public CoverPlate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverPlate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void init(){
        paint.setARGB(80,200,200,200);
        //paint.setColor(Color.BLUE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    WatchCameraActivity.Size size;
    Rect rect = new Rect();
    public void setClipRect(WatchCameraActivity.Size size){
       this.size = size;
    }




    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight();
        int w = getWidth();
        Log.i("debugC","Cover:w"+getWidth() + ",h:"+getHeight());
//        int w_padding = w / 8;
//        int h_padding = (h - (((w - w_padding << 1) * 3) >> 2)) >> 1;

//        rect.set(0,0,w,h_padding);
//
//        canvas.drawRect(rect,paint);
//
//        rect.set(0,h - h_padding,w,h);
//
//        canvas.drawRect(rect,paint);
        int h_padding = (h - size.h) >> 1;
        int w_padding = (w - size.w) >> 1;
        rect.left = w_padding;
        rect.top = h_padding;
        rect.right = w - w_padding;
        rect.bottom = h - h_padding;

        paint.setColor(Color.argb(200,200,200,0));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,paint);
    }
}
