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


    ClipRect clipRect;
    Rect rect = new Rect();
    public void setClipRect(ClipRect clipRect){
       this.clipRect = clipRect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight();
        int w = getWidth();
        Log.i("debugC","onDraw Cover:w"+getWidth() + ",h:"+getHeight());
        rect.left = clipRect.x;
        rect.top = clipRect.y;
        rect.right = clipRect.size.w + clipRect.x;
        rect.bottom = clipRect.size.h + clipRect.y;

        paint.setColor(Color.argb(200,200,200,0));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,paint);
    }
}
