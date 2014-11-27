package kl.animationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Administrator on 2014/11/13.
 */
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    CameraEasyManager mCameraMgr;
    SurfaceHolder mHolder;
    WatchCameraActivity mActivity;
    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(CameraEasyManager cm,WatchCameraActivity watchCameraActivity) {
        mActivity = watchCameraActivity;
        mCameraMgr = cm;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try {
            cm.mCamera.setPreviewDisplay(mHolder);
            cm.mCamera.startPreview();
        } catch (Exception e) {
            Log.d("debug", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
           // if(mActivity.mZone == null || mActivity.mZone.manner == -1 || mActivity.mZone.manner == 3) {
                mCameraMgr.mCamera.setPreviewCallback(previewCallback);
           // }
            mCameraMgr.mCamera.setPreviewDisplay(holder);
            mCameraMgr.mCamera.startPreview();
        } catch (Exception e) {
            Log.d("debug", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.i("debug", "camera surfaceChanged");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCameraMgr.mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
           // if(mActivity.mZone == null || mActivity.mZone.manner == -1 || mActivity.mZone.manner == 3) {
                mCameraMgr.mCamera.setPreviewCallback(previewCallback);
            //}
            mCameraMgr.mCamera.setPreviewDisplay(mHolder);
            mCameraMgr.mCamera.startPreview();

        } catch (Exception e) {
            Log.d("debug", "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
//        mCameraMgr.mCamera.stopPreview();
        mCameraMgr.release();
        Log.i("debug", "camera release");
    }

    final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if(mCameraMgr.shouldTakePreView()){
                mCameraMgr.mCamera.takePicture(null, null,mActivity);
                if(!mCameraMgr.getPreviewClipZone(data,mActivity.mZone.getClipRectInPreview(),mActivity)){
                    Toast.makeText(mActivity,"截图失败，manner = 3",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
