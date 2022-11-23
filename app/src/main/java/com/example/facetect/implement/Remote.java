package com.example.facetect.implement;

import static com.example.facetect.bean.faces_location.getFaceslocation;
import static com.example.facetect.bean.faces_location.setFaceslocation;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.core.CvType.CV_8UC4;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;
import static org.opencv.imgproc.Imgproc.calcHist;
import static org.opencv.imgproc.Imgproc.cvtColor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.example.facetect.IDetectFace;
import com.example.facetect.R;

import org.opencv.android.CameraActivity;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.objdetect.FaceRecognizerSF;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;

import static com.example.facetect.Utils.*;
public class Remote implements IDetectFace {

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    int  port = 9001;
    String address = "222.201.190.153";

    private byte [] byte4 = new byte[4];
    private byte [] byte1 = new byte[1];
    private ByteArrayInputStream send(ByteArrayInputStream bis,Integer bufferSize) throws IOException {

        Socket socket = new Socket(address,port);
        OutputStream os = socket.getOutputStream();
        InputStream  is = socket.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        os.write(Int2byte(bufferSize));
        int read = -1;
        while ((read =  bis.read(bytes))!=-1){
            os.write(bytes,0,read);
        }
        while ((read =  is.read(bytes))!=-1){
            bos.write(bytes,0,read);
        }
        return new ByteArrayInputStream(bos.toByteArray());

    }


    @Override
    public Object[] callFunction(int start, int end, Object[] objects) {
        Integer bufferSize = -1;
        Object[] result = new Object[3];
//        if(!objects[0].getClass().equals((Mat.class))){
//            //不支持非Mat类型传输哦
//            return null;
//        }
        try {
        ByteArrayInputStream bis;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write((byte) 1);
        bos.write((byte) 1);
        bos.write((byte) start);
        bos.write((byte) end);

        bufferSize = 4;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                byte[] buffer = Mat2Buffer((Mat) objects[i]);
                bufferSize+=buffer.length;
                bos.write(buffer);
            }
        }
        //getBuffer = send(head);
        bis = send(new ByteArrayInputStream(bos.toByteArray()),bufferSize);

        Integer insertIndex = 0;
        bis.read(byte4);
        if(byte4[0]=='0'&&byte4[1]=='0'&&byte4[2]=='0'&&byte4[3]=='0'){
            return result;
        }
        if(end==4){
                bis.read(byte4);
                Integer Name_len = byte2Int(byte4);
                byte[] bytesName = new byte[Name_len];
                bis.read(bytesName);
                String name = new String((bytesName));
                result[insertIndex]=name;
                ++insertIndex;

                Mat faces_loc = getFaceslocation();
                if(faces_loc!=null&&!faces_loc.empty()){
                    result[insertIndex] = faces_loc;
                    return result;
                }
        }
        while(bis.read(byte4)!=-1){
            int row = byte2Int(byte4);
            bis.read(byte4);
            int col= byte2Int(byte4);
            bis.read(byte1);
            int Channel = byte1[0];
            bis.read(byte1);
            int Type = byte1[0];
            Mat res ;
            if(Type==CV_8UC3){
                byte [] byteMat = new byte[row*col*Channel];
                bis.read(byteMat);
                res = new Mat(row,col,CV_8UC3);
                res.put(0,0,byteMat);
            }
            else if(Type==CV_32FC1){
                byte [] byteMat = new byte[row*col*Channel*4];
                bis.read(byteMat);
                res = new Mat(row,col,CV_32FC1);
                res.put(0, 0, ByteArrayToFloatArray(byteMat));
            }
            else{
                break;
            }
            result[insertIndex]=res;
            ++insertIndex;
        }
        if(result[1]!=null&&!((Mat)result[1]).empty()){
            setFaceslocation((Mat) result[1]);
        }
        return result;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void init(CameraActivity RunningObject, android.content.Context context) {

    }

    @Override
    public boolean SaveFeature(Bitmap bitmap, String name) {
        return false;
    }
}
