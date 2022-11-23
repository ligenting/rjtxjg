package com.example.facetect;

import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.core.CvType.CV_8UC4;

import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {
    public static  byte[] Mat2Buffer(Mat mat){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(Int2byte(mat.rows()));
            bos.write(Int2byte(mat.cols()));
            bos.write((byte) mat.channels());
            bos.write((byte) mat.type());
            if(mat.type()==CV_32FC1){
                float [] matData = new float[mat.rows()*mat.cols()*mat.channels()];
                mat.get(0,0,matData);
                bos.write(FloatArrayToByteArray(matData));
                return bos.toByteArray();
            }
            else if (mat.type()==CV_8UC4||mat.type()==CV_8UC3){
                byte [] matData = new byte[mat.rows()*mat.cols()*mat.channels()];
                mat.get(0,0,matData);
                bos.write(matData);
                return bos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Integer byte2Int(byte [] bytes){
        Integer res = 0;
        res = res|((bytes[3] & 0xff)<<24);
        res = res|((bytes[2] & 0xff)<<16);
        res = res|((bytes[1] & 0xff)<<8);
        res = res|(bytes[0] & 0xff);
        return res;
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
    public static byte [] Int2byte(int integer){
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


}
