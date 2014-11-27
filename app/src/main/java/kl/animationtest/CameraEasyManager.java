package kl.animationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2014/11/25.
 */
public class CameraEasyManager {
    Camera mCamera;
    boolean hasReleased = true;

    public CameraEasyManager(){
    }

    public Camera open(){
        mCamera = Camera.open();
        return mCamera;
    }

    public void release(){
        hasReleased = true;
        mCamera.release();
    }

    public boolean hasReleased(){
        return hasReleased;
    }

    public Bitmap getPreviewFrame(byte[] data){
        //不要修改PreviewFormat
        //if(mCamera.getParameters().getPreviewFormat() == ImageFormat.NV21 )
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        final YuvImage yuvImage = new YuvImage(data,mCamera.getParameters().getPreviewFormat(),size.width,size.height,null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if(!yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, os)){
            return null;
        }
        byte[] tmp = os.toByteArray();
        return BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
    }

    public void takePictureFromPreview(){
        shouldTakePreView = true;
    }

    public void finishTakePreview(){
        shouldTakePreView = false;
    }

    public boolean shouldTakePreView(){
        return  shouldTakePreView;
    }

    private boolean shouldTakePreView;


    public boolean getPreviewClipZone(byte[] data,Rect clipRect,Context context){

        return  false;
    }
}