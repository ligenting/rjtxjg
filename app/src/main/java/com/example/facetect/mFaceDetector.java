package com.example.facetect;

import static org.opencv.imgproc.Imgproc.cvtColor;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.facetect.implment.Remote;
import com.example.facetect.implment.Local;

import org.opencv.android.CameraActivity;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;


public class mFaceDetector implements IDetectFace {
    public final static int LOCAL_MODE = 1,REMOTE_MODE = 2,AUTO_MODE = 3;
    private int detectMode;

    private IDetectFace detector;


    public void  init(CameraActivity RunningObject,Context context){
        detector.init(RunningObject,context);
    }



    mFaceDetector(){
       detectMode= LOCAL_MODE;
       detector = new Local();
   }
    mFaceDetector(int mode){
       switch (mode){
           case REMOTE_MODE:{
               detectMode= LOCAL_MODE;
               detector = new Remote();
               break;
           }
           case AUTO_MODE:{

           }
           default:{
               detectMode= LOCAL_MODE;
               detector = new Local();
           }
       }

    }


    @Override
    public Mat preprocess(Mat inputMat) {
        if(detectMode!=AUTO_MODE){
            return detector.preprocess(inputMat);
        }
        return null;
    }

    @Override
    public MatOfRect FaceDetect(Mat inputMat) {
        if(detectMode!=AUTO_MODE){
            return detector.FaceDetect(inputMat);
        }
        return null;
    }

    @Override
    public Mat  FeatureExtract(Mat inputMat, MatOfRect outputRect) {
        if(detectMode!=AUTO_MODE){
            return detector.FeatureExtract(inputMat, outputRect);
        }
        return null;
    }

    @Override
    public String FeatureRecognize(Mat features) {
        if(detectMode!=AUTO_MODE){
            return detector.FeatureRecognize(features);
        }
        return null;
    }
    @Override
    public boolean SaveFeature(Bitmap bitmap,String name) {
        if(detectMode!=AUTO_MODE){
            return detector.SaveFeature(bitmap ,name);
        }
        return false;
    }
}
