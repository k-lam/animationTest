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
        final Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(),R.drawable.spot_mask);
        bitmapMask = Bitmap.createBitmap(bitmapTmp.getWidth(),bitmapTmp.getHeight(),Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmapMask);
        canvas.drawBitmap(bitmapTmp,0f,0f,null);
        bitmapTmp.recycle();
        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                points.add(new Coordinate(event.getX(),event.getY()));
                tPoints = new Coordinate(event.getX() - bitmapMask.getWidth() / 2,event.getY() - bitmapMask.getHeight() / 2);
                invalidate();
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
       RectF rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        //paint.setColor(Color.BLUE);
        //canvas.drawRoundRect(rect,30f,30f,paint);
       // canvas.drawBitmap(bitmapMask,(getWidth() / 2f) - 50,(getHeight() / 2f) - 50,paint);
//        for(Coordinate c : points){
//            canvas.drawBitmap(bitmapMask,c.x,c.y,paint);
//        }
        //bgCanvas.drawBitmap(bi);
        if(bgCanvas == null){
            bgBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
            bgCanvas = new Canvas(bgBitmap);
            //bgCanvas.setMatrix(new Matrix());
            //paint.getShader().setLocalMatrix(new Matrix());
        }
        if(tPoints != null) {
            bgCanvas.drawColor(Color.BLACK);
            Matrix matrix = new Matrix();
            matrix.setTranslate(-50,-50);
            paint.getShader().setLocalMatrix(matrix);
            bgCanvas.drawBitmap(bitmapMask, (getWidth() / 2f) - 50, (getHeight() / 2f) - 50, paint);
            bgCanvas.drawBitmap(bitmapMask,(getWidth() / 2f) - 100,(getHeight() / 2f) - 100,paint);
           // matrix.setTranslate(100,100);
            //paint.getShader().setLocalMatrix(matrix);
            bgCanvas.drawBitmap(bitmapMask,(getWidth() / 2f),(getHeight() / 2f),paint);
           // matrix.setTranslate(50,50);
           // paint.getShader().setLocalMatrix(matrix);
            bgCanvas.drawBitmap(bitmapMask,(getWidth() / 2f) + 50,(getHeight() / 2f) + 50,paint);
            //bgCanvas.drawBitmap(bitmapMask, tPoints.x, tPoints.y, paint);
            //bgCanvas.drawRoundRect(rect,30f,30f,paint);
            //Log.i("shader","bgCanvas:" + bgCanvas.getWidth() + "*" + bgCanvas.getHeight() + "tPoints:(" + tPoints.x + "," + tPoints.y+")");
        }
        canvas.drawBitmap(bgBitmap,0,0,null);
        if(tPoints != null) {
//            canvas.drawBitmap(bitmapMask,(getWidth() / 2f) - 50,(getHeight() / 2f) - 50,paint);
//            canvas.drawBitmap(bitmapMask,(getWidth() / 2f) - 100,(getHeight() / 2f) - 100,paint);
//            canvas.drawBitmap(bitmapMask,(getWidth() / 2f),(getHeight() / 2f),paint);
//            canvas.drawBitmap(bitmapMask,(getWidth() / 2f) + 50,(getHeight() / 2f) + 50,paint);
            //canvas.drawBitmap(bitmapMask, tPoints.x, tPoints.y, paint);
        }
    }

    static class Coordinate{
        float x,y;
        Coordinate(float x,float y){this.x = x; this.y = y;}
    }
}
