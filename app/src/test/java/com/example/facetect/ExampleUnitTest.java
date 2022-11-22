package com.example.facetect;

import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.floodFill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.example.facetect.bean.Features;

import org.junit.Test;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

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
    @Test
    public void floattest(){
        float [] floatsList = new float[]{(float) 0.145234534,(float) 0.22323};
        for (int i = 0; i < floatsList.length; i++) {
            System.out.println(floatsList[i]);
        }
        byte [] bytes = FloatArrayToByteArray(floatsList);
        for (int i = 0; i <  bytes.length; i++) {
            System.out.println( bytes[i]);
        }
        float [] newfloat = ByteArrayToFloatArray(bytes);
        for (int i = 0; i <  newfloat.length; i++) {
            System.out.println(newfloat[i]);
        }

    }

    @Test
    public void testbyte2float(){
        byte [] b  = new byte[]{Byte.parseByte("\\x00\\x00\\x00@\\x00\\x00\\x80?\\x00\\x00\\x00?")};
        float [] f = ByteArrayToFloatArray(b);

    }
    // 输入 Mat 类型 CV_8UC4 960*1280  1280*960 CV_8UC4
    @Test
    public void MattypeTest(){
        if (!OpenCVLoader.initDebug()) {

        }
        else{
            Mat srcCV_32UC4 = new Mat(1,2,CvType.CV_32FC3,new Scalar(0.1,0.3,0.4));
            float [] send = new float[srcCV_32UC4.rows()*srcCV_32UC4.cols()*srcCV_32UC4.channels()];
            srcCV_32UC4.get(0,0,send);
            byte [] da = FloatArrayToByteArray(send);
            for (byte d:
                    da) {
                System.out.println(d);
            }
        }


    }

    @Test
    public void addition_isCorrect() throws IOException {
        Socket socket = new Socket("222.201.190.153",9001);
        OutputStream os = socket.getOutputStream();
        byte [] send = new  byte[1024];
        for (int i = 0; i < send.length; i++) {
            send[i]= ((byte) i);
        }

        Features features  = new Features();
        float [] floatsList = new float[]{(float) 0.145234534,(float) 0.22323};
        List<float []> floats = new ArrayList<>();
        floats.add(floatsList);
        os.write(send);
        //oos.writeObject(floatsList);
        byte [] rec = new  byte[32];
        //os.write(send);
        byte [] rec16 = new  byte[32];
        InputStream is = socket.getInputStream();
        //is.read(rec16);
        //System.out.println("rc1"+rec16);
        //is.read(rec16);
        //System.out.println("rc2"+rec16);
        is.read(rec);
        //os.close();

        for (int i = 0; i < rec.length; i++) {
            System.out.println(rec[i]);
        }

        is.read(rec);
        //os.close();
        System.out.println("rec2");
        for (int i = 0; i < rec.length; i++) {
            System.out.println(rec[i]);
        }
        is.close();
        socket.close();

    }

}