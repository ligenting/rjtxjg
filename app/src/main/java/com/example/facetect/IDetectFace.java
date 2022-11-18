package com.example.facetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import org.opencv.android.CameraActivity;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public interface IDetectFace {
    public  Mat preprocess(Mat inputMat);

    public MatOfRect FaceDetect(Mat inputMat );
    //
    public Mat FeatureExtract(Mat inputMat , MatOfRect outputRect);
    //
    public String FeatureRecognize(Mat features);
    public void init(CameraActivity RunningObjet, Context context);
    public  boolean SaveFeature(Bitmap bitmap,String name);
}
