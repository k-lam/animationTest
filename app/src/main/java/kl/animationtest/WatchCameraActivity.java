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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import bolts.Task;

/**
 * 由于android camera分为preview size，picture size。而且两个size的aspect ratio会不一样（如：4:3,16:9）
 * 而用来显示preview的view的大小比例必须和preview size的一样，如果不是，会出现扭曲形变。
 * 所以我们允许preview size比屏幕大，屏幕内的叫做显示区。
 * 实现在FourZone这个类中
 * Created by KL on 2014/11/13.
 */
public class WatchCameraActivity extends Activity implements Camera.PictureCallback{

    CameraPreview mPreview;
    //Camera mCamera;
    CoverPlate coverPlate;
    View mPreview_ly;
    FourZone mZone;
//    Bitmap bitmap_tmp;
    CameraEasyManager mCameraMgr;
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
                Log.i("debugC","mCameraMgr is null:" + (mCameraMgr == null));
                if(mCameraMgr != null){Log.i("debugC"," has released ? " + mCameraMgr.hasReleased());}
                if(mCameraMgr != null && !mCameraMgr.hasReleased()) {
                    if(mZone.manner == 3){
                       mCameraMgr.takePictureFromPreview();
                    }else {
                        mCameraMgr.mCamera.takePicture(null, null, WatchCameraActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("debugC","onResume");
        //if(mCamera == null) {
            mCameraMgr = getCameraInstance();
            mPreview.init(mCameraMgr,this);
            if (mCameraMgr != null) {
                Camera.Size size = mCameraMgr.mCamera.getParameters().getPreviewSize();
                ViewGroup.LayoutParams params = mPreview_ly.getLayoutParams();
                params.height = size.height;
                params.width = size.width;
                mPreview_ly.setLayoutParams(params);
                Log.i("debugC", "reLayout");
                coverPlate.setClipRect(mZone.clipRect);
            }
       // }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("debugC","onPause");
        mCameraMgr.release();
    }

    Runnable takePhoneRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    /** A safe way to get an instance of the Camera object. */
    public  CameraEasyManager getCameraInstance() {
        CameraEasyManager cm = new CameraEasyManager();
        Camera c = null;
        try {
            c = cm.open(); // attempt to get a Camera instance
            if(c != null){
                Camera.Parameters parameters = c.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                Camera.Area area = new Camera.Area(new Rect(-300,-300,300,300),800 );
                List<Camera.Area> ls_a = new ArrayList<Camera.Area>();
                ls_a.add(area);
                parameters.setFocusAreas(ls_a);
                //下面这两句有问题，要判断屏幕方向,但是不同的机器测试的结果不一样！
                int h = getResources().getDisplayMetrics().heightPixels;
                int w = getResources().getDisplayMetrics().widthPixels;
                if(w < h){
                    int tmp = w;
                    w = h;
                    h = tmp;
                }
                mZone = new FourZone(c);
                mZone.caculateBestSize(w,h,w - 150);
                //sizeResult = caculateBestSize(c,w,h,w - 150);
                Log.i("debugC","window:" + w + "*" + h);
                Camera.Size size = mZone.size_preview;
                Log.i("debugC","preview:" + size.width + "*" + size.height);
                parameters.setPreviewSize(size.width,size.height);
                //Camera.Size tSize = c.new Size(w,h);
                if(mZone.manner != 3){
                    parameters.setPictureSize(mZone.size_picture.width,mZone.size_picture.height);
                }
                c.setParameters(parameters);
//                clipRect = new ClipRect();
//                clipRect.size = new Size();
//                clipRect.size = calculateClipZone(tSize);
//                clipRect.setClipRect(size);
//                released = false;
                return  cm;
            }
        } catch (Exception e) {
            Log.e("debugC",e.getMessage());
            e.printStackTrace();
        }
        return null; // returns null if camera is unavailable
    }

//    Camera.Size findBestSize(Camera camera,final int LARGEST_W,final int LARGEST_H){
//        int bestW = 0,bestH = 0;
//        for(Camera.Size size : camera.getParameters().getSupportedPreviewSizes()){
//            if(size.width >= bestW && size.width <= LARGEST_W && size.height >= bestH && size.height <= LARGEST_H){
//                bestH = size.height;
//                bestW = size.width;
//            }
//        }
//        return camera.new Size(bestW,bestH);
//    }

    /**
     * sizes[0] preview size
     * sizes[1] picture size,当manner = 3时，为空
     * sizes[2] 显示区size
     * manner 1 : preview size 和picture size的aspect ratio一样，preview size 大于等于显示区
     * （停用）manner 2:  preview size 和picture size的aspect ratio一样, preview size 等比例缩放后， 不会超过预设的控制区
     * manner 3： preview size 大于等于显示区，sizes[1] 为空，应该用preview的图。
     */
    public static class SizeResult{
        Camera.Size[] sizes;
        int manner;

        public SizeResult(Camera.Size[] sizes,int manner){
            this.sizes = sizes;
            this.manner = manner;
        }
    }

    public Camera.Size getJustLargerSzie(Camera c,int W,int H,List<Camera.Size> ls){
        int w=0,h=0;
        for(Camera.Size size : ls){
            if(size.height >= H && size.width >= W){
                if(w == 0 ||(size.width <= w && size.height <= h)) {
                    w = size.width;
                    h = size.height;
                }
            }
        }
        return c.new Size(w,h);
    }


    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        if(mZone.manner != 3){
            Bitmap bitmap_Source =  BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            ClipRect clipRectInSource = new ClipRect(mZone.clipRect);
            Camera.Size size_preview = camera.getParameters().getPreviewSize();
            clipRectInSource.x += (mZone.size_preview.width - mPreview.getWidth()) / 2;
            clipRectInSource.y += (mZone.size_preview.height - mPreview.getHeight()) / 2;
            float scaleFactor = ((float)camera.getParameters().getPictureSize().height) / size_preview.height;
            clipRectInSource.x *= scaleFactor;
            clipRectInSource.y *= scaleFactor;
            clipRectInSource.size.h *= scaleFactor;
            clipRectInSource.size.w *= scaleFactor;
            Log.i("debugC","picture w:" +bitmap_Source.getWidth() + ",h:"+bitmap_Source.getHeight());
            Matrix matrix = new Matrix();
            matrix.setRotate(90f);
            Bitmap bitmap_clip = Bitmap.createBitmap(bitmap_Source,clipRectInSource.x,clipRectInSource.y,clipRectInSource.size.w,clipRectInSource.size.h,matrix,true);
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
        }
       //upload_img();
    }

    public void upload_img(){
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

//    static class ClipRect{
//        Size size;
//        int x,y;
//
//        public void setClipRect(Camera.Size outSize){
//            if(outSize.height >= size.h && outSize.width >= size.w){
//                x = (outSize.width - size.w) >> 1;
//                y = (outSize.height - size.h) >> 1;
//            }
//        }
//
//        public ClipRect(){}
//
//        public ClipRect(ClipRect crect){
//            this.size = new Size(crect.size.w,crect.size.h);
//            this.x = crect.x;
//            this.y = crect.y;
//        }
//    }

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
