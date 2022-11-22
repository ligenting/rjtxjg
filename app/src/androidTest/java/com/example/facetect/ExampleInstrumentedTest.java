package com.example.facetect;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import static org.junit.Assert.*;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_32FC3;
import static org.opencv.core.CvType.CV_8UC4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    String TAG ="lgtTest";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
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
    public static final int
            CV_8U = 0,
            CV_8S = 1,
            CV_16U = 2,
            CV_16S = 3,
            CV_32S = 4,
            CV_32F = 5,
            CV_64F = 6,
            CV_16F = 7;

        byte [] Int2byte(int integer){
            byte [] bytes = new byte[4];
            bytes[3] = (byte) (integer>>24);
            bytes[2] = (byte) (integer>>16);
            bytes[1] = (byte) (integer>>8);
            bytes[0] = (byte) integer;
            return bytes;
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
        else if (mat.type()==CV_8UC4){
            byte [] matData = new byte[mat.rows()*mat.cols()*mat.channels()];
            mat.get(0,0,matData);
            return concat(head,matData);
        }
        mat.convertTo(mat,CV_32F);
        float [] matData = new float[mat.rows()*mat.cols()*mat.channels()];
        mat.get(0,0,matData);
        return concat(head,FloatArrayToByteArray(matData));
    }
    protected byte [] send(byte [] sendData) throws IOException {
        int  port = 9001;
        String address = "222.201.190.153";
        Socket socket = new Socket(address,port);
        socket.setSendBufferSize(1024*1024*8);
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        byte [] rec = new byte[1024*1024*8];
        Integer len = sendData.length;
        ByteArrayInputStream in = new ByteArrayInputStream(sendData);
        int _paks = len;
        os.write(Int2byte(_paks));
//        byte[] bytes = new byte[1024*16];
//        int read ;
//        while ((read = in.read(bytes))!=-1){
//                os.write(bytes,0,read);
//        }
        os.write(sendData);
        is.read(rec);
        System.out.println(rec);
        return rec;
    }
    Integer byte2Int(byte [] bytes){
        Integer res = 0;
        res = res|((bytes[3] & 0xff)<<24);
        res = res|((bytes[2] & 0xff)<<16);
        res = res|((bytes[1] & 0xff)<<8);
        res = res|(bytes[0] & 0xff);
        return res;
    }
    @Test
    public void CallTest(){
        if (!OpenCVLoader.initDebug()) {
            return;
        }
        Mat srcCV_8UC3 = new Mat(1200,900,CvType.CV_8UC4,new Scalar(1,3,4));
        Mat srcCV_32UC4 = new Mat(1,2, CV_32FC1,new Scalar(0.1453));
        Object[] send = new Object[]{srcCV_8UC3,srcCV_32UC4};
        callFunction(2,3, send);
    }
    public Object[] callFunction(int start, int end, Object[] objects) {
//        if(!objects[0].getClass().equals((Mat.class))){
//            //不支持非Mat类型传输哦
//            return null;
//        }
        byte[] getBuffer = new byte[]{};
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
            e.printStackTrace();
        }
        Integer it = 4;
        Integer insertIndex = 0;
        if(end==4){
            Integer namesize = byte2Int(Arrays.copyOfRange(getBuffer,it,it+4));
            it+=4;
            byte [] name = Arrays.copyOfRange(getBuffer,it,it+namesize);
            String sname = new String(name);
            it+=namesize;
            result[insertIndex]=sname;
            ++insertIndex;
            System.out.println(sname);
        }
        while(getBuffer[it]!=0){
            int row = byte2Int(Arrays.copyOfRange(getBuffer,it,it+4));
            it+=4;
            int col= byte2Int(Arrays.copyOfRange(getBuffer,it,it+4));
            it+=4;
            int Channel =getBuffer[it];
            ++it;
            int Type = getBuffer[it];
            ++it;
            Mat res = null;
            if(Type==CV_8UC4){
                res = new Mat(row,col,CV_8UC4);
                res.get(0,0,Arrays.copyOfRange(getBuffer,it,it+row*col*Channel));
            }
            if(Type==CV_32FC1){
                res = new Mat(row,col,CV_32FC1);
                byte [] b = Arrays.copyOfRange(getBuffer, it, it + row * col * Channel * 4);
                res.get(0, 0, ByteArrayToFloatArray(b));
            }
            result[insertIndex]=res.clone();
            ++insertIndex;
        }
        return null;
    }
    @Test
    public void MattypeTest() throws IOException {

        if (!OpenCVLoader.initDebug()) {
            return;
        } else {
            Mat srcCV_8UC3 = new Mat(2,3,CvType.CV_8UC3,new Scalar(1,3,4));
            Mat srcCV_32UC4 = new Mat(1,2, CV_32FC3,new Scalar(0.1,0.3,0.4));
            byte [] head = new byte[5];
            head[0] = 4;
            head[1] = ((byte) srcCV_32UC4.rows());
            head[2] = ((byte) srcCV_32UC4.cols());
            head[3] = ((byte) srcCV_32UC4.channels());
            head[4] = ((byte) srcCV_32UC4.type());
            float [] send = new float[srcCV_32UC4.rows()*srcCV_32UC4.cols()*srcCV_32UC4.channels()];
            srcCV_32UC4.get(0,0,send);
            byte [] da = FloatArrayToByteArray(send);
            for (byte d:
                    da) {
                System.out.println(TAG+"byte"+d);
            }
            Socket socket = new Socket("222.201.190.153",9001);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            os.write(head);
            os.write(FloatArrayToByteArray(send));


            byte [] rec = new byte[32];
            is.read(rec);
            System.out.println(TAG+rec[0]);
            System.out.println(TAG+rec[1]);
            is.read(rec);
            float [] list =  ByteArrayToFloatArray(rec);
            for (float d:
                    list) {
                System.out.println(TAG+"float"+d);
            }

        }
    }
}