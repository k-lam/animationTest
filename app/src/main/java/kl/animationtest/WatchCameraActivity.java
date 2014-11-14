package kl.animationtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/11/13.
 */
public class WatchCameraActivity extends Activity implements Camera.PictureCallback{
    CameraPreview mPreview;
    Camera mCamera;
    CoverPlate coverPlate;
    View mPreview_ly;
    Size clipSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchcamera);
        mPreview = (CameraPreview)findViewById(R.id.preview);
        mPreview_ly = findViewById(R.id.preview_ly);
        coverPlate = (CoverPlate)findViewById(R.id.coverPlate);
        findViewById(R.id.btn_takephoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null,null,WatchCameraActivity.this);
            }
        });
        //mPreview.init(getCameraInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        // if(mCamera == null){
        mCamera = getCameraInstance();
        mPreview.init(mCamera);
        if(mCamera != null){
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            ViewGroup.LayoutParams params = mPreview_ly.getLayoutParams();
            params.height = size.height;
            params.width = size.width;
            mPreview_ly.setLayoutParams(params);
            coverPlate.setClipRect(clipSize);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.release();
    }

    /** A safe way to get an instance of the Camera object. */
    public  Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            if(c != null){
                Camera.Parameters parameters = c.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                Camera.Area area = new Camera.Area(new Rect(-300,-300,300,300),800 );
                List<Camera.Area> ls_a = new  ArrayList<Camera.Area>();
                ls_a.add(area);
                parameters.setFocusAreas(ls_a);
                for(Camera.Area a : parameters.getFocusAreas()){
                    Log.i("debugC","weight:"+a.weight + a.rect.toShortString());
                }

                c.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Log.i("debugC",b+",autoFocus");
                    }
                });
                int h = getResources().getDisplayMetrics().heightPixels;
                int w = getResources().getDisplayMetrics().widthPixels;

                Log.i("debugC","h:" +h + ",w:"+w);
                mPreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.i("debugC","p h:" +mPreview.getHeight() + ",w:"+mPreview.getWidth());
                        mPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
                Camera.Size size = findBestSize(c,w,h);
                parameters.setPreviewSize(size.width,size.height);
                c.setParameters(parameters);
                Camera.Size tSize = c.new Size(size.width,size.height);
                if(size.height > size.width){
                    tSize.width = size.height;
                    tSize.height = size.width;
                }

                clipSize = calculateClipZone(tSize);
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            //Log.e("debug",e.getMessage());
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    Camera.Size findBestSize(Camera camera,final int LARGEST_W,final int LARGEST_H){
        int bestW = 0,bestH = 0;
        for(Camera.Size size : camera.getParameters().getSupportedPreviewSizes()){
            //Log.d("debugCS","w:" + size.width + "," + size.height);
            if(size.width >= bestW && size.width <= LARGEST_W && size.height >= bestH && size.height <= LARGEST_H){
                bestH = size.height;
                bestW = size.width;
            }
        }
        return camera.new Size(bestW,bestH);
    }

    //size 必须width比height大
    Size calculateClipZone(Camera.Size size){
        final int decrement = size.width >> 4;
        Size result = new Size(size.width,0);
        //result.w = size.width;
       // result.h = size.height;
        while(true){
            result.w -= decrement;
            result.h = (result.w >> 2) * 3;
            if(result.h < size.height){
                return result;
            }
            if(result.h <= 0 || result.w <= 0){
                return new Size(300,400);
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Camera.Size size = camera.getParameters().getPictureSize();
        byte[] clipBytes = new byte[clipSize.h * clipSize.w];
        int lines = clipSize.h;
        int step = clipSize.w;
        //while(lines)
        //Bitmap bitmap = Bitmap.createBitmap()
    }

    static class Size{
        int w;
        int h;
        public Size(){}
        public Size(int w,int h){
            this.w = w;
            this.h = h;
        }
    }
}
