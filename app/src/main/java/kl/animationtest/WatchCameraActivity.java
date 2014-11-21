package kl.animationtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by Administrator on 2014/11/13.
 */
public class WatchCameraActivity extends Activity implements Camera.PictureCallback{

    CameraPreview mPreview;
    Camera mCamera;
    CoverPlate coverPlate;
    View mPreview_ly;
    ClipRect clipRect;
    //由于camera没有查询是否release的状态
    boolean released = true;

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
                if(!released) {
                    mCamera.takePicture(null, null, WatchCameraActivity.this);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("debugC","onResume");
        //if(mCamera == null) {
            mCamera = getCameraInstance();
            mPreview.init(mCamera);
            if (mCamera != null) {
                Camera.Size size = mCamera.getParameters().getPreviewSize();
                ViewGroup.LayoutParams params = mPreview_ly.getLayoutParams();
                params.height = size.height;
                params.width = size.width;
                mPreview_ly.setLayoutParams(params);
                Log.i("debugC","reLayout");
                coverPlate.setClipRect(clipRect);
            }
       // }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("debugC","onPause");
        mCamera.release();
        released = true;
    }

    /** A safe way to get an instance of the Camera object. */
    public  Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            if(c != null){
                Camera.Parameters parameters = c.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                Camera.Area area = new Camera.Area(new Rect(-300,-300,300,300),800 );
                List<Camera.Area> ls_a = new  ArrayList<Camera.Area>();
                ls_a.add(area);
                parameters.setFocusAreas(ls_a);
                c.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Log.i("debugC",b+",autoFocus");
                    }
                });
                int h = getResources().getDisplayMetrics().heightPixels;
                int w = getResources().getDisplayMetrics().widthPixels;

                Log.i("debugC","h:" +h + ",w:"+w);
                Camera.Size size = findBestSize(c,w,h);
                Log.i("debugC","preview:" + size.width + "*" + size.height);
                findBestPictureSize(c);
                parameters.setPreviewSize(size.width,size.height);
                calculateBestSize(c,new Size[]{new Size(w,h)},w);
                //parameters.setPictureSize(size.width,size.height);
                //parameters.setPictureFormat(ImageFormat.RGB_565);
                c.setParameters(parameters);
                Camera.Size tSize = c.new Size(size.width,size.height);
                if(size.height > size.width){
                    tSize.width = size.height;
                    tSize.height = size.width;
                }
                clipRect = new ClipRect();
                clipRect.size = new Size();
                clipRect.size = calculateClipZone(tSize);
                clipRect.setClipRect(size);
                released = false;
                return  c;
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e("debugC",e.getMessage());
            e.printStackTrace();
        }
        return null; // returns null if camera is unavailable
    }

    Camera.Size findBestSize(Camera camera,final int LARGEST_W,final int LARGEST_H){
        int bestW = 0,bestH = 0;
        for(Camera.Size size : camera.getParameters().getSupportedPreviewSizes()){
            if(size.width >= bestW && size.width <= LARGEST_W && size.height >= bestH && size.height <= LARGEST_H){
                bestH = size.height;
                bestW = size.width;
            }
        }
        return camera.new Size(bestW,bestH);
    }

    Camera.Size findBestPictureSize(Camera camera){
        for(Camera.Size size : camera.getParameters().getSupportedPictureSizes()){
            Log.i("debugC","supported picture size : " + size.width + " * " + size.height);
        }
        return  null;
    }

    Camera.Size[] calculateBestSize(Camera camera,final Size[] preferSizes,int deadline){
        //先找一个最合适屏幕size的preview，然后找一个比例和preview相符的picture size
        //最好的size：preview aspect ratio和picture aspect ratio都等于HOPE_W / HOPE_H，且size大于等于HOPE
        //如果没有,在相等的ratio中选一个ratio，根据屏幕高压缩后，最接近deadline，但不超过的。且size必须大于等于300*400
        Camera.Size[] c_sizes = new Camera.Size[]{camera.new Size(0,0),camera.new Size(0,0)};
       // Float_Here bestRatio = new Float_Here(HOPE_W / HOPE_H);
//        RatioMap<Size> prefer_Map = new RatioMap<Size>();
        RatioMap<Camera.Size> preview_Map = new RatioMap<Camera.Size>();
        RatioMap<Camera.Size> picture_Map = new RatioMap<Camera.Size>();

//        for(Size size : preferSizes){
//            prefer_Map.putIn(new Float_Here((float)size.w / size.h),size);
//        }

        for(Camera.Size size : camera.getParameters().getSupportedPreviewSizes()){
            preview_Map.putIn(new Float_Here((float)size.width / size.height), size);
        }

        for(Camera.Size size : camera.getParameters().getSupportedPictureSizes()){
            picture_Map.putIn(new Float_Here((float)size.width / size.height),size);
        }

        List<Float_Here> ls_ratio = new LinkedList<Float_Here>();
        for(Float_Here key : preview_Map.keySet()){
            if(picture_Map.containsKey(key)){
                ls_ratio.add(key);
            }
        }

        for(Size preferSize : preferSizes){
            Float_Here key = new Float_Here((float)preferSize.w/preferSize.h);
            if(ls_ratio.contains(key)){
                //找第一个比preferSize大的
                int w = preferSize.w , h = preferSize.h;
                boolean flag = false;
                for(Camera.Size size : preview_Map.get(key)){
//                    if(size.height >= preferSize.h && size.width >= preferSize.w &&){
//                        flag = true;
//                    }
                }
            }
        }
//        String s = "";
//        for(Float_Here key : preview_Map.keySet()){
//            s += "|||ratio:" + key.toString();
//            if(picture_Map.containsKey(key)){
//                s += " pre:";
//                for(Camera.Size size : preview_Map.get(key)){
//                    s += (size.width + "*" + size.height + " ");
//                }
//                s += ";pic:";
//                for(Camera.Size size : picture_Map.get(key)){
//                    s += (size.width + "*" + size.height + " ");
//                }
//            }
//        }
//        Log.i("debugC",s);

        return  c_sizes;
    }

    static class Float_Here{
        float value;
        public Float_Here(float f){
            value = f;
        }

        float getValue(){
            return value;
        }

        @Override
        public String toString() {
            return "" +(float)(Math.round(value * 1000)) / 1000;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Float_Here){
                if(value - ((Float_Here)o).getValue() < 0.001f){
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            float f  =  (float)(Math.round(value * 1000)) / 1000;
            return new Float(f).hashCode();
        }
    }

    static class RatioMap<V> extends HashMap<Float_Here, List<V>> {

        public void putIn(Float_Here key, V value) {
            List list = null;
            if (containsKey(key)) {
                list = get(key);
                list.add(value);
            } else {
                list = new ArrayList<V>();
                list.add(value);
                put(key,list);
            }
        }
    }

    //size 必须width比height大
    Size calculateClipZone(Camera.Size size){
        final int decrement = size.width >> 4;
        Size result = new Size(size.width,0);
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
        Bitmap bitmap_Source = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Log.i("debugC","picture w:" +bitmap_Source.getWidth() + ",h:"+bitmap_Source.getHeight());
        Matrix matrix = new Matrix();
        matrix.setRotate(90f);
        Bitmap bitmap_clip = Bitmap.createBitmap(bitmap_Source,clipRect.x,clipRect.y,clipRect.size.w,clipRect.size.h,matrix,true);
        bitmap_Source.recycle();
        Bitmap bitmap_compressed = Bitmap.createScaledBitmap(bitmap_clip,300,400,true);
        bitmap_clip.recycle();
        //改成另外一条线程执行
        try {
            FileOutputStream fos = this.openFileOutput("tmp.jpg",
                    Context.MODE_PRIVATE);
            final BufferedOutputStream bos = new BufferedOutputStream(fos,
                    16384);
            if(bitmap_compressed.compress(Bitmap.CompressFormat.JPEG, 50, bos)){
                Log.i("debugC","bos success");
            }else {
                Log.i("debugC","bos failes");
            }
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        upload_img(bitmap_compressed);
    }

    public void upload_img(final Bitmap bitmap){
        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    HttpClient client = new DefaultHttpClient();
                    //wb.tensquare.hk/wb/api/idv/  121.41.108.239/wb/benchmark/
                    HttpPost post = new HttpPost("http://wb.tensquare.hk/wb/api/idv/");
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    File file = new File(getFilesDir().getPath() + "/"+ "tmp.jpg");
                    entityBuilder.addBinaryBody("image", file);
                    HttpEntity entity = entityBuilder.build();
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    HttpEntity httpEntity = response.getEntity();
                    String result = EntityUtils.toString(httpEntity);
                    Log.v("debugC", result);
                }catch (Exception ex){
                    Log.e("debugC",ex.getMessage());
                }
                return null;
            }
        });
    }


    static class ClipRect{
        Size size;
        int x,y;

        public void setClipRect(Camera.Size outSize){
            if(outSize.height >= size.h && outSize.width >= size.w){
                x = (outSize.width - size.w) >> 1;
                y = (outSize.height - size.h) >> 1;
            }
        }
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
