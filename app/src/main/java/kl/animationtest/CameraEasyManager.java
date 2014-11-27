package kl.animationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2014/11/25.
 */
public class CameraEasyManager {
    Camera mCamera;
    boolean hasReleased = true;

    public CameraEasyManager(){
    }

    public Camera open(){
        hasReleased = false;
        mCamera = Camera.open();
        return mCamera;
    }

    public void release(){
        hasReleased = true;
        try {
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }catch (Exception ex){
            ex.printStackTrace();
        }
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


    public boolean getPreviewClipZone(byte[] data,Rect clipRect,Context context,FourZone zone){
        if(mCamera.getParameters().getPreviewFormat() != ImageFormat.YUY2 && mCamera.getParameters().getPreviewFormat() != ImageFormat.NV21){
            return false;
        }
        YuvImage yuvImage = new YuvImage(data,mCamera.getParameters().getPreviewFormat(),zone.size_preview.width,zone.size_preview.height,null);
        try {
            FileOutputStream fos = context.openFileOutput( "tmp11",Context.MODE_PRIVATE);
            final BufferedOutputStream bos = new BufferedOutputStream(fos,
                    16384);
            if(!yuvImage.compressToJpeg(clipRect,50,bos)){
                return false;
            }
            bos.flush();
            bos.close();
            fos.close();
            yuvImage = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Bitmap bitmapCliped = BitmapFactory.decodeStream(context.openFileInput("tmp11"));
            Matrix matrix = new Matrix();
            matrix.setRotate(90f);
            float scale = bitmapCliped.getHeight() / 400f;
           // matrix.postScale(scale,scale);
            Bitmap bitmap_rotate = Bitmap.createBitmap(bitmapCliped,0,0,bitmapCliped.getWidth(),bitmapCliped.getHeight(),matrix,true);
            bitmapCliped.recycle();
            Bitmap bitmap = Bitmap.createScaledBitmap(bitmap_rotate,300,400,true);
            bitmap_rotate.recycle();;
            FileOutputStream fos = context.openFileOutput("tmp.jpg",
                    Context.MODE_PRIVATE);
            final BufferedOutputStream bos = new BufferedOutputStream(fos,
                    16384);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)){
                Log.i("debugC", "bos success");
            }else {
                Log.i("debugC","bos failes");
            }
            bos.flush();
            bos.close();
            fos.close();
            bitmap.recycle();
            return  true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  false;
    }
}