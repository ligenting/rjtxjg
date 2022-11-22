package com.example.facetect;

import static org.opencv.imgproc.Imgproc.cvtColor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.example.facetect.implement.Remote;
import com.example.facetect.implement.Local;

import org.opencv.android.CameraActivity;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;


public class mFaceDetector {
    public final static int LOCAL_MODE = 1,REMOTE_MODE = 2,AUTO_MODE = 3;
    private int detectMode;
    static final int MODULE_NUMBER = 4;
    static final boolean LOCAL_CALL = true;
    static final boolean REMOTE_CALL = false;
    private IDetectFace Localdetector;
    private IDetectFace RemoteDetector;
    private List<Callable> callableList = new ArrayList<>();


    private boolean [] divide = new boolean[MODULE_NUMBER];

    public void  init(CameraActivity RunningObject,Context context){
        Localdetector.init(RunningObject,context);
        RemoteDetector.init(RunningObject,context);
        //todo 更改划分
        setDivide(new boolean[]{LOCAL_CALL,LOCAL_CALL,REMOTE_CALL,REMOTE_CALL});
    }

    //根据划分建立函数
    public void setDivide(boolean[] divide) {
        this.divide = divide;
        int start = 0;
        for (int last = 0; last < MODULE_NUMBER;) {
            boolean flag  = divide[last];
            while (last< MODULE_NUMBER&&flag==divide[last]){
                last++;
            }

            if(flag==true){
                callableList.add(new Callable(start,last,true));
            }
            else{
                callableList.add(new Callable(start,last,false));
            }
            start = last;
        }
    }

    //构造方法
    mFaceDetector(){
       detectMode= LOCAL_MODE;
       Localdetector = new Local();
       RemoteDetector = new Remote();
   }


   //保存图像
    public boolean SaveFeature(Bitmap bitmap,String name) {
        return Localdetector.SaveFeature(bitmap ,name)&&RemoteDetector.SaveFeature(bitmap,name);

    }


    boolean IsString(Object obj){
        if(obj==null){
            return false;
        }
        return obj.getClass().equals(String.class);
    }
    boolean IsValidMat(Mat mat){
        if(mat==null){
            return false;
        }
        return !mat.empty();
    }
    //主函数 调用划分后的函数
    public Pair<String,Mat> detectFace(Mat img){
        Object[] res = callableList.get(0).Call(new Object[]{img.clone()});
        if(!IsString(res[0])&&!IsValidMat((Mat) res[0])){
            return null;
        }
        for (int i = 1; i < callableList.size(); i++) {

            res = callableList.get(i).Call(res.clone());
            if(!IsString(res[0])&&!IsValidMat((Mat) res[0])){
                return null;
            }
        }
        String name = (String) res[0];
        Mat mat = (Mat) res[1];
        return new Pair<>(name,mat);
    }
    //内部类 实现函数调用，不需要知道底层细节，只知道过程调用
    class Callable{

        private int start = 0;
        private int end = 0;
        IDetectFace detectorImplement = null;

        public Callable(int start,int end,boolean isLocal){
            if(isLocal){
                detectorImplement =Localdetector;
            }
            else {
                detectorImplement = RemoteDetector;
            }
            this.start = start;
            this.end =end;
        }
        public Object[] Call(Object[] input){
            Object[] res =  detectorImplement.callFunction(start,end,input);
            return  res;
        }
    }
}
