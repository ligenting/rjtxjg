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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Remote implements IDetectFace {



    public static byte[] FloatArrayToByteArray(float[] data)
    {
        byte[] Resutl = {};
        for (int i = 0; i < data.length; i++)
        {
            int intToBytes2 = Float.floatToIntBits(data[i]);
            byte[] temp = new byte[4];
            temp[0] =  (byte) (intToBytes2>>0);
            temp[1] = (byte) (intToBytes2>>8);
            temp[2] =  (byte) (intToBytes2>>16);
            temp[3] = (byte) (intToBytes2>>24);
            Resutl = concat(Resutl,temp);
        }
        return Resutl;
    }
    byte [] Int2byte(int integer){
        byte [] bytes = new byte[4];
        bytes[3] = (byte) (integer>>24);
        bytes[2] = (byte) (integer>>16);
        bytes[1] = (byte) (integer>>8);
        bytes[0] = (byte) integer;
        return bytes;
    }
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
    public static float[] ByteArrayToFloatArray(byte[] data)
    {
        float[] result = new float[data.length / 4];
        for (int i = 0; i < data.length; i += 4)
        {
            int temp = 0;
            temp = temp | (data[i] & 0xff) << 0;
            temp = temp | (data[i+1] & 0xff) << 8;
            temp = temp | (data[i+2] & 0xff) << 16;
            temp = temp | (data[i+3] & 0xff) << 24;
            result[i / 4] = Float.intBitsToFloat(temp);
        }
        return result;
    }
    public Remote(){
    }
    //






    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    protected byte [] send(byte [] sendData) throws IOException {
        int  port = 9001;
        String address = "222.201.190.153";
        Socket socket = new Socket(address,port);
        socket.setSendBufferSize(1024*1024*8);
        OutputStream os = socket.getOutputStream();
        BufferedInputStream is = new BufferedInputStream(socket.getInputStream()) ;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte [] rec = new byte[1024*1024*8];
        Integer len = sendData.length;
       //ByteArrayOutputStream oss = new ByteArrayOutputStream(rec);
        int _paks = len;
        os.write(Int2byte(_paks));
        byte[] bytes = new byte[1024];
//        int read ;
//        while ((read = in.read(bytes))!=-1){
//                os.write(bytes,0,read);
//        }
        os.write(sendData);
        int read ;
//        while ((read = is.read(bytes))!=-1){
//                obs.write(bytes,0,read);
//        }
        byte[] rec_pak = new   byte[4];
//        is.read(rec_pak);
//        int rec_pak_size = byte2Int(rec_pak);
        while ((read =  is.read(bytes))!=-1){
            bos.write(bytes,0,read);
        }
        //is.read(rec);
        rec= bos.toByteArray();
        System.out.println(rec);
        return rec;
    }

    public byte[] Mat2Buffer(Mat mat){
        int t = mat.type();
        byte [] head = new byte[10];
        head[3] = (byte) (mat.rows()>>24);
        head[2] = (byte) (mat.rows()>>16);
        head[1] = (byte) (mat.rows()>>8);
        head[0] = ((byte) mat.rows());
        head[7] = (byte) (mat.cols()>>24);
        head[6] = (byte) (mat.cols()>>16);
        head[5] = (byte) (mat.cols()>>8);
        head[4] = ((byte) mat.cols());
        head[8] = ((byte) mat.channels());
        head[9] = ((byte) mat.type());
        if(mat.type()==CV_32FC1){
            float [] matData = new float[mat.rows()*mat.cols()*mat.channels()];
            mat.get(0,0,matData);
            return concat(head,FloatArrayToByteArray(matData));
        }
        else if (mat.type()==CV_8UC4||mat.type()==CV_8UC3){
            byte [] matData = new byte[mat.rows()*mat.cols()*mat.channels()];
            mat.get(0,0,matData);
            return concat(head,matData);
        }
        mat.convertTo(mat,CV_32F);
        float [] matData = new float[mat.rows()*mat.cols()*mat.channels()];
        mat.get(0,0,matData);
        System.out.println(matData);
        byte [] bt = FloatArrayToByteArray(matData);
        //System.out.println(matData);
        return concat(head,bt);
    }
    Integer byte2Int(byte [] bytes){
        Integer res = 0;
        res = res|((bytes[3] & 0xff)<<24);
        res = res|((bytes[2] & 0xff)<<16);
        res = res|((bytes[1] & 0xff)<<8);
        res = res|(bytes[0] & 0xff);
        return res;
    }
    Mat face_location=null;
    @Override
    public Object[] callFunction(int start, int end, Object[] objects) {
//        if(!objects[0].getClass().equals((Mat.class))){
//            //不支持非Mat类型传输哦
//            return null;
//        }

        byte[] getBuffer = null;
        Object[] result = new Object[3];
        byte[] head = new byte[4];
        head[0] = (byte) 1;
        head[1] = (byte) 1;
        head[2] = (byte) start;
        head[3] = (byte) end;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                head = concat(head, Mat2Buffer((Mat) objects[i]));
            }
        }
        try {
            getBuffer = send(head);
        } catch (IOException e) {
            return null;
        }
        Integer it = 4;
        Integer insertIndex = 0;
        if(end==4){
            Integer namesize = byte2Int(Arrays.copyOfRange(getBuffer,it,it+4));
            it+=4;
            String sname = new String(Arrays.copyOfRange(getBuffer,it,it+namesize));
            it+=namesize;
            result[insertIndex]=sname;
            ++insertIndex;
            Mat faces_loc = getFaceslocation();
            if(faces_loc!=null&&!faces_loc.empty()){
                result[insertIndex] = faces_loc;
                return result;
            }
            System.out.println(sname);
        }
        int len = getBuffer.length;
        while(it<getBuffer.length-1){
            int row = byte2Int(Arrays.copyOfRange(getBuffer,it,it+4));
            it+=4;
            int col= byte2Int(Arrays.copyOfRange(getBuffer,it,it+4));
            it+=4;
            int Channel =getBuffer[it];
            ++it;
            int Type = getBuffer[it];
            ++it;
            Mat res = null;
            int _end = 0;

            if(Type==CV_8UC3){
                _end = it+row*col*Channel;
                res = new Mat(row,col,CV_8UC3);
                res.put(0,0,Arrays.copyOfRange(getBuffer,it,_end));
                it=_end;
            }
            else if(Type==CV_32FC1){
                _end = it+row*col*Channel*4;
                res = new Mat(row,col,CV_32FC1);
                byte [] b = Arrays.copyOfRange(getBuffer, it, _end);
                res.put(0, 0, ByteArrayToFloatArray(b));
                it=_end;
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
    }

    @Override
    public void init(CameraActivity RunningObject, android.content.Context context) {

    }

    @Override
    public boolean SaveFeature(Bitmap bitmap, String name) {
        return false;
    }
}
