package kl.animationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.LinkedList;
import java.util.List;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Administrator on 2014/12/5.
 */
public class ShaderView extends View {

    public ShaderView(Context context) {
        super(context);
        init();
    }

    public ShaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Bitmap mBitmap = null;
    Shader shader;
    Paint paint;
    Bitmap bitmapMask;
    List<Coordinate> points = new LinkedList<Coordinate>();
    Bitmap bgBitmap;
    Coordinate tPoints;
    Canvas bgCanvas;
    void init(){
//        bgBitmap = Bitmap.createBitmap(467,467,Bitmap.Config.ARGB_8888);
//        bgCanvas = new Canvas(bgBitmap);
        mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iwc);
        Log.i("shader",mBitmap.getWidth() + "*" + mBitmap.getHeight());
        shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        options.inScaled = true;
        final Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(),R.drawable.glow,options);
        bitmapMask = Bitmap.createBitmap(bitmapTmp.getWidth(),bitmapTmp.getHeight(),Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmapMask);
        canvas.drawBitmap(bitmapTmp,0f,0f,null);
        bitmapTmp.recycle();
        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                points.add(new Coordinate(event.getX(),event.getY()));
                tPoints = new Coordinate(event.getX() - 100,event.getY() - 100);
//                tPoints = new Coordinate(event.getX() - bitmapMask.getWidth() / 2,event.getY() - bitmapMask.getHeight() / 2);
                invalidate();
                return true;
            }
        });

    }

    Matrix matrix = new Matrix();
    boolean isFirst = true;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bgCanvas == null){
            bgBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
            bgCanvas = new Canvas(bgBitmap);
        }
        if(tPoints != null) {
            bgCanvas.drawCircle(tPoints.x,tPoints.y,100f,paint);
        }
        canvas.drawBitmap(bgBitmap,0,0,null);
    }

    static class Coordinate{
        float x,y;
        Coordinate(float x,float y){this.x = x; this.y = y;}
    }
}
