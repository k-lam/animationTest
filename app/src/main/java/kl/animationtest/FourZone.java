package kl.animationtest;

import android.graphics.Rect;
import android.hardware.Camera;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class FourZone {

    public Camera.Size size_picture;
    public Camera.Size size_preview;
    public Camera.Size size_displayZone;
    public int manner = -1;//manner = 1,manner = 2停用
    /**
     * 相对于displayZone的
     */
    public ClipRect clipRect;
    Camera mCamera;

    public FourZone(Camera camera) {
        this.mCamera = camera;
    }

    /**
     *
     * @param W  希望显示区域的阔度
     * @param H  希望显示区域的宽度
     * @param controlAreaLine  控制区位置
     * @return
     */
    FourZone caculateBestSize(final int W,final int H,final int controlAreaLine){
        //1.aspect ratio一样的，比全屏大的
        //2.aspect ratio一样的，不超过deadline的
        //3.不管ratio了，直接用preview的图像，不用相机返回的数据
        RatioMap<Camera.Size> preview_Map = new RatioMap<Camera.Size>();
        RatioMap<Camera.Size> picture_Map = new RatioMap<Camera.Size>();

        //计算preview 和 picture 的aspect ratio
        for(Camera.Size size : mCamera.getParameters().getSupportedPreviewSizes()){
            preview_Map.putIn(new Float_Here((float)size.width / size.height), size);
        }
        for(Camera.Size size : mCamera.getParameters().getSupportedPictureSizes()){
            picture_Map.putIn(new Float_Here((float)size.width / size.height),size);
        }

        //获取preview和picture中相等的aspect ratio
        List<Float_Here> ls_ratio = new LinkedList<Float_Here>();
        for(Float_Here key : preview_Map.keySet()){
            if(picture_Map.containsKey(key)){
                ls_ratio.add(key);
            }
        }
//        String s = "the same ratio:";
//        for(Float_Here key : ls_ratio){
//            s +=(" ratio:" + key.getTranslation() + "pre:");
//            for(Camera.Size size : preview_Map.get(key)){
//                s += (size.width + "*" + size.height + ",");
//            }
//            s += "; pic:";
//            for(Camera.Size size : preview_Map.get(key)){
//                s += (size.width + "*" + size.height + ",");
//            }
//            s += "||||";
//        }
//        Log.i("debugC",s);
//        Camera.Size[] sizes = new Camera.Size[3];

//        1.aspect ratio一样的，比全屏大的
//        for(Float_Here key : ls_ratio){
//            size_preview = getJustLargerSzie(mCamera,W,H,preview_Map.get(key));
//            if(size_preview.width != 0){
//                size_displayZone =mCamera.new Size(W,H);
//                size_picture = getJustLargerSzie(mCamera,size_preview.width,size_preview.height,picture_Map.get(key));
//            }
//            if(size_preview.width != 0 && size_picture.width != 0){
//                //clip_size = calculateClipZone(size_preview);
//                clipRect = new ClipRect();
//                clipRect.size = calculateClipZone(size_preview);
//                clipRect.setClipRect(size_displayZone);
//                manner = 1;
//                return this;
//            }else {
//                //clean
//               cleanSize();
//            }
//        }

        //2.aspect ratio一样的，不超过deadline的
//        for(Float_Here key : ls_ratio){
//            if(key.getTranslation() * W <= controlAreaLine){
//                for(Camera.Size size : preview_Map.get(key)){
//                    if(size.height == H){
//                        sizes[0] = size;
//                        break;
//                    }
//                }
//                sizes[1] = getJustLargerSzie(camera,sizes[0].width,sizes[0].height,picture_Map.get(key));
//                if(sizes[0].width != 0 && sizes[1].width != 0){
//                    return new SizeResult(sizes,2);
//                }else {
//                    //clean
//                    sizes[0] = sizes[1] = null;
//                }
//            }
//        }
        //3.不管ratio了，直接用preview的图像，不用相机返回的数据
        // sizes[0] = findBestSize(camera,W,H);
        size_preview = getJustLargerSzie(mCamera,W,H,mCamera.getParameters().getSupportedPreviewSizes());
        manner = 3;
        size_displayZone = mCamera.new Size(W,H);
        clipRect = new ClipRect();
        clipRect.size = calculateClipZone(size_preview);
        clipRect.setClipRect(size_displayZone);
        return this;
    }

    void cleanSize(){
        size_picture = size_displayZone = size_preview = null;
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

    /**
     * @param size  显示空间
     * @return 裁剪大小size
     */
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

    Rect getClipRectInPreview(){
        Rect rect = new Rect();
        rect.left = (size_preview.width - size_displayZone.width) / 2 + clipRect.x;
        rect.top = (size_preview.height - size_displayZone.height) / 2 + clipRect.y;
        rect.right = rect.left + clipRect.size.w;
        rect.bottom = rect.top + clipRect.size.h;
        return  rect;
    }
}

class Size{
    int w;
    int h;
    public Size(){}
    public Size(int w,int h){
        this.w = w;
        this.h = h;
    }
}

class ClipRect{
    Size size;
    int x,y;

    public void setClipRect(Camera.Size outSize){
        if(outSize.height >= size.h && outSize.width >= size.w){
            x = (outSize.width - size.w) >> 1;
            y = (outSize.height - size.h) >> 1;
        }
    }

    public ClipRect(){}

    public ClipRect(ClipRect crect){
        this.size = new Size(crect.size.w,crect.size.h);
        this.x = crect.x;
        this.y = crect.y;
    }
}

class Float_Here{
    float value;
    public Float_Here(float f){
        value = f;
    }

    float getValue(){
        return value;
    }

    @Override
    public String toString() {
        return "" +(float)(Math.round(value * 100)) / 100;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Float_Here){
            if(value - ((Float_Here)o).getValue() < 0.01f){
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

class RatioMap<V> extends HashMap<Float_Here, List<V>> {

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
