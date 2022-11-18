package com.example.facetect.implment;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.example.facetect.IDetectFace;

import org.opencv.android.CameraActivity;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Remote implements IDetectFace {
    private int port;
    private String address;

    private OutputStream os;
    private InputStream is;
    public Remote(){
    }
    //
    @Override
    public Mat preprocess(Mat inputMat) {
        return null;
    }

    @Override
    public MatOfRect FaceDetect(Mat inputMat) {
        return null;
    }

    @Override
    public Mat FeatureExtract(Mat inputMat, MatOfRect outputRect) {
        return null;
    }

    @Override
    public String FeatureRecognize(Mat features) {
        return null;
    }

    @Override
    public void init(CameraActivity RunningObjet, Context context) {
        return ;
    }

    @Override
    public boolean SaveFeature(Bitmap bitmap, String name) {
        return false;
    }


    protected void send(byte [] sendData,byte [] getData) throws IOException {
        Socket socket = new Socket(address,port);
        os = socket.getOutputStream();
        os.write(sendData);
        is = socket.getInputStream();
        is.read(getData);
    }
}
