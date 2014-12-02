package kl.animationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2014/11/28.
 */
public class MeshImageView extends View {
    public MeshImageView(Context context) {
        super(context);
        init();
    }

    public MeshImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeshImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
       // initMesh();
    }

    Bitmap mBitmap = null;
    public void setBitmap(Bitmap bitmap){
        this.mBitmap = bitmap;
        initMesh();
    }

    int mMeshHeight = 10;
    int mMeshWidth = 10;

    float[] mVerts = new float[(mMeshHeight + 1) * (mMeshWidth + 1) * 2];
    void initMesh(){
        float w = mBitmap.getWidth();
        float h = mBitmap.getHeight();
        // construct our mesh
        int index = 0;
        float increatment = h / mMeshHeight * 2;
        //这里就是设好mVerts了
        for (int y = 0; y <= mMeshHeight; y++) {
            float fy = h * y / mMeshHeight;
            for (int x = 0; x <= mMeshWidth; x++) {
                float fx = w * x / mMeshWidth;
//                if((x & 1) == 1){
//                    fx += increatment;
//                }else {
//                    fx -= increatment;
//                }
                if((x & 2) == 2){
                    setXY(mVerts, index, fx, fy + 20);
                }else {
                    setXY(mVerts, index, fx, fy - 20);
                }
                index += 1;
            }
        }
    }

    private static void setXY(float[] array, int index, float x, float y) {
        //乘2是因为（x，y）对
        array[index*2 + 0] = x;
        array[index*2 + 1] = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawBitmapMesh(mBitmap,mMeshWidth,mMeshHeight,mVerts,0,null,0,null);
        Rect rectL = new Rect(0,50,300,1067);

        Rect rectLD = new Rect(0,0,300,1067);

        canvas.drawBitmap(mBitmap,rectL,rectLD,null);

        Rect rectR = new Rect(300,-50,681,1067);
        Rect rectRD = new Rect(300,0,681,1067);

        canvas.drawBitmap(mBitmap,rectR,rectRD,null);
    }
}
