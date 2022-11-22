package com.example.facetect.implement;

import static com.example.facetect.bean.faces_location.getFaceslocation;
import static com.example.facetect.bean.faces_location.setFaceslocation;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.calcHist;
import static org.opencv.imgproc.Imgproc.cvtColor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
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
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.objdetect.FaceRecognizerSF;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Local implements IDetectFace {
    private static final String TAG = "mFaceDetector";
    private CascadeClassifier mJavaDetector;
    private FaceRecognizerSF mJavaRecognizeSF;
    private FaceDetectorYN mJavaDetectorYN;
    Size YNsize =   new Size(320,320);
    private List<Pair<String, Mat>> featureList = new ArrayList<>();
    private Class<? extends CameraActivity> CurrentActivity;
    private CameraActivity CurrentObject;
    private Method addonScreenMessage;
    private File fileDir;
    private static Context Context = null;
    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;


    //<editor-fold desc="加载数据">
    public void setCurrentObject(CameraActivity currentObject) {
        CurrentObject = currentObject;
        setCurrentActivity(currentObject.getClass());
    }


    public static void setContext(android.content.Context context) {
        Context = context;
    }

    public void setCurrentActivity(Class<? extends CameraActivity> currentActivity) {
        CurrentActivity = currentActivity;
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            // commons-io
            //IOUtils.copy(inputStream, outputStream);

        }

    }
    String [] Trump = new String[]{"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fworld.chinadaily.com.cn%2Fimg%2Fattachement%2Fjpg%2Fsite1%2F20170302%2Feca86bd9e2fa1a21c7630b.jpg&refer=http%3A%2F%2Fworld.chinadaily.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=a21e0e39237f6fe2e10f62ac69cdd990",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp2.cri.cn%2FM00%2F85%2FC2%2FCqgNOlgIIWiACtVIAAAAAAAAAAA216.980x626.jpg&refer=http%3A%2F%2Fp2.cri.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=695bc2686b779fd90ad19f439e4983f6",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp2.cri.cn%2FM00%2F56%2FA1%2FCqgNOlpRfvmAVEJhAAAAAAAAAAA480.4000x2667.jpg&refer=http%3A%2F%2Fp2.cri.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=67d1b97ccb08d09c015af121e092dc4e",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp2.cri.cn%2FM00%2F63%2F91%2FCqgNOleRhdqAD0AwAAAAAAAAAAA839.600x398.jpg&refer=http%3A%2F%2Fp2.cri.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=79a4f76acec7517d6e574eece6c92805",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.cyol.com%2Fimg%2Fnews%2Fattachement%2Fjpg%2Fsite2%2F20180325%2FIMG509a4c1fcb9c4719647522.jpg&refer=http%3A%2F%2Fimg.cyol.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=e5489854c67e3045251585fd7bff1313",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp5.img.cctvpic.com%2Fphotoworkspace%2Fcontentimg%2F2019%2F07%2F20%2F2019072013491443782.jpg&refer=http%3A%2F%2Fp5.img.cctvpic.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=5d0f0b7e37ea92a9656fc80dbc52dbd5",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpicture.youth.cn%2Fqtdb%2F201712%2FW020171223268085450849.jpg&refer=http%3A%2F%2Fpicture.youth.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671091996&t=3555a4d88aabd3aa06ea6b3e230a19e7"};
    String []Biden = new String[]{"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg3.utuku.imgcdc.com%2F650x0%2Fmilitary%2F20221109%2F5432eac7-7624-455a-a26f-957378523c30.jpg&refer=http%3A%2F%2Fimg3.utuku.imgcdc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671092201&t=abc755102c70dbede683c98a2749608e",
            "https://pics0.baidu.com/feed/86d6277f9e2f07085e862dc7c464fb92ab01f2c5.jpeg@f_auto?token=7f5f17f6cf6ac198108b92a1d85d31e0",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Frespub.xrdz.dzng.com%2Fpic_file%2Fpic_file%2F20220225%2Fsmall_7fa96777d56deca922f67e6aef526c51.jpg&refer=http%3A%2F%2Frespub.xrdz.dzng.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671092201&t=0bc464531a1df72323d57e20876db0a6",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fn.sinaimg.cn%2Fsinakd20220519s%2F214%2Fw2048h1366%2F20220519%2F095a-b2d9f96ae083162dd4825971aafe037a.jpg&refer=http%3A%2F%2Fn.sinaimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671092201&t=5be23d07b3a12be1f90f4994a4f8122f",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp9.itc.cn%2Fq_70%2Fimages03%2F20210527%2F8d2f95282fdf4985b7c69e426dfc0dbb.png&refer=http%3A%2F%2Fp9.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671092201&t=5e5ed8ae636f6a0135fb79c9129122a4",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fmz.eastday.com%2F58052059.jpeg%3Fimageslim&refer=http%3A%2F%2Fmz.eastday.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671092201&t=de3753c8d40d1a7d296f497f73cc4fa9",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fx0.ifengimg.com%2Fucms%2F2022_06%2FCDCAA22ED75762D6B228D531A41E1727FEB14BAB_size35_w690_h388.jpg&refer=http%3A%2F%2Fx0.ifengimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671092201&t=ec9c1f1fc89de46e249e68957092bc13"};
    private void loadfeaturedata() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    for (String url:
                            Trump) {

                        addonScreenMessage.invoke(CurrentObject,"loading");
                        URL myurl= new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                        conn.connect();
                        InputStream is = null;//获得图片的数据流
                        is = conn.getInputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
                        Bitmap bmp = BitmapFactory.decodeStream( is );//读取图像数据
                        SaveFeature(bmp,"Trump");


                    }
                    for (String url:
                            Biden) {
                        addonScreenMessage.invoke(CurrentObject,"loading");
                        URL myurl= new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                        conn.connect();
                        InputStream is = null;//获得图片的数据流
                        is = conn.getInputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
                        Bitmap bmp = BitmapFactory.decodeStream( is );//读取图像数据
                        SaveFeature(bmp,"Biden");
                    }
                    addonScreenMessage.invoke(CurrentObject,"load complete");
                } catch (IOException | IllegalAccessException | InvocationTargetException ioException) {
                    ioException.printStackTrace();
                }

            }
        }).start();

        return ;
    }

    protected void SaveFeature(Pair<String, Mat> pair) {
        featureList.add(pair);
    }

    public boolean SaveFeature(Bitmap bitmap,String name){
        //裁剪
        Mat image = new Mat();
        Mat imageT = new Mat();
        Utils.bitmapToMat(bitmap,image);
        //Imgproc.resize(image,image,YNsize);
//转换为rgb三通道
        Core.transpose(image,imageT);
        Pair<Mat,Mat> result =  FaceDetect(preprocess(imageT));
        if(result==null){
            return false ;
        }

        Mat feature = FeatureExtract(result.first);
        SaveFeature(new Pair<>(name,feature.clone()));

        return true;
    }

    //</editor-fold>




    //8UC4 8UC4
    public Mat preprocess(Mat inputMat) {
        cvtColor(inputMat,inputMat,COLOR_BGRA2BGR);
        Mat mRgba = new Mat();
        Core.transpose(inputMat,mRgba);
        //计算颜色分布直方图
        List<Mat> imgs = new ArrayList<Mat>();
        imgs.add(mRgba);
        MatOfFloat mfloat=new MatOfFloat(0,256);
        Mat hist = new Mat();
        calcHist(imgs,new MatOfInt(1),new Mat(),hist,new MatOfInt(100),mfloat);
        int maxIndex=-1;
        double maxcount = 0;
        for(int i = 0;i<100;i++){
            if(maxcount<hist.get(i,0)[0]){
                maxcount = hist.get(i,0)[0];
                maxIndex = i;
            }
        }
        //设置对比度
        maxIndex = (int) (-0.5*maxIndex+40);
        int brightess = maxIndex;
        double alpha = 2;
        double beta = 127*(1-alpha)+brightess;
        mRgba.convertTo(mRgba,mRgba.type(),alpha,beta);
        return mRgba.clone();
    }
    //8UC4 32FC1
    public Pair<Mat,Mat>  FaceDetect(Mat inputMat) {
        Mat YNfaces = new Mat();
        Mat Align_faces = new Mat();
        mJavaDetectorYN.setInputSize(inputMat.size());
        mJavaDetectorYN.detect(inputMat,YNfaces);
        if(YNfaces.empty()){
            return null;
        }
        mJavaRecognizeSF.alignCrop(inputMat,inputMat.row(0),Align_faces);
        return new Pair<>(Align_faces,YNfaces) ;
    }

    //32FC1 8UC4  ->32FC1
    public Mat FeatureExtract(Mat Align_faces) {
        Mat feature = new Mat();
        mJavaRecognizeSF.feature(Align_faces,feature);
        return  feature;

    }

    public String FeatureRecognize(Mat feature) {
        double score = -9990;
        String Name = "";
        for (int i = 0; i < featureList.size(); i++) {
            double score_tmp = mJavaRecognizeSF.match(feature,featureList.get(i).second);
            if (score_tmp>score){
                score = score_tmp;
                Name  = featureList.get(i).first;
            }
        }
        return Name;
    }

    /**
     * 输入与输出内容定义
     * Object[0] 临时变量
     * Object[1] 人脸位置
     * Object[2] 后处理图片
     * @param start
     * @param end
     * @param objects
     * @return
     */
    private Mat _faces ;
    @Override
    public Object[] callFunction(int start, int end, Object[] objects) {
        if(start==0){
            if(end==1){
                Object[] result = new Object[3];
                result[0]=preprocess((Mat) objects[0]);
                //Object[2] 后处理图片
                return result;
            }
            else if(end==2){
                Object[] result = new Object[3];
                //返回人脸位置
                Pair<Mat,Mat> Faces = FaceDetect(preprocess((Mat) objects[0]));
                if(Faces==null){
                    return result;
                }
                setFaceslocation(Faces.second);
                result[0] =  Faces.first;

                return result;
            }
            else if (end==3){
                Object[] result = new Object[2];
                Mat afterProcess =  preprocess((Mat) objects[0]);
                //不用返回后处理图像
                //返回人脸位置
                Pair<Mat,Mat> Faces  =  FaceDetect(afterProcess);
                if(Faces==null){
                    return result;
                }
                setFaceslocation(Faces.second);
                result[0]  = FeatureExtract(Faces.first);
                return result;
            }
            else{
                Object[] result = new Object[2];
                Mat afterProcess =  preprocess((Mat) objects[0]);
                //返回人脸位置
                Pair<Mat,Mat> Faces  =  FaceDetect(afterProcess);
                if(Faces==null){
                    return result;
                }
                result[1] = Faces.second;
                result[0]  = FeatureRecognize(FeatureExtract(Faces.first));
                return result;
            }
        }
        else if(start==1){

            if(end ==2){
                Object[] result = new Object[3];
                //返回人脸位置
                Pair<Mat,Mat> Faces  =  FaceDetect((Mat) objects[0]);
                if(Faces==null){
                    return result;
                }
                setFaceslocation(Faces.second);
                result[0] = Faces.first;
                return result;
            }
            else if(end == 3){
                Object[] result = new Object[2];
                //返回人脸位置
                Pair<Mat,Mat> Faces  =  FaceDetect((Mat) objects[0]);
                if(Faces==null){
                    return result;
                }
                setFaceslocation(Faces.second);
                //特征向量
                result[0]  = FeatureExtract((Mat) Faces.first );
                return result;
            }
            else {
                Object[] result = new Object[2];
                //返回人脸位置
                Pair<Mat,Mat> Faces  =  FaceDetect((Mat) objects[0]);
                if(Faces==null){
                    return result;
                }
                result[1] = Faces.second;

                result[0] = FeatureRecognize(FeatureExtract((Mat) Faces.first ));
                return result;
            }
        }
        else if (start==2){

            if(end==3){
                Object[] result = new Object[2];
                result[0] =  FeatureExtract((Mat) objects[0]);
                return result;
            }
            else{
                Object[] result = new Object[2];
                Mat face_loc = getFaceslocation();
                if(face_loc!=null&&!face_loc.empty()){
                    result[1] = face_loc;
                }
                result[0] =  FeatureRecognize(FeatureExtract((Mat) objects[0]));
                return result;
            }
        }
        else {
            Object [] result = new Object[2];
            Mat face_loc = getFaceslocation();
            if(face_loc!=null&&!face_loc.empty()){
                result[1] = face_loc;
            }
            result[0] = FeatureRecognize((Mat) objects[0]);
            return result;
        }
    }

    @Override
    public void init(CameraActivity RunningObjet, Context context) {
        _faces = new Mat();
        CurrentObject = RunningObjet;
        setCurrentActivity(RunningObjet.getClass());
        Context = context;
        try {
            if(addonScreenMessage==null){
                addonScreenMessage =  CurrentActivity.getMethod("addMessage",String.class);
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            fileDir = Context.getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(fileDir, "lbpcascade_frontalface.xml");
            File mYNFile = new File(fileDir, "yunet.onnx");
            File mSFFile = new File(fileDir, "face_recognizer_fast.onnx");
            if(!mCascadeFile.exists()){
                copyInputStreamToFile(Context.getResources().openRawResource(R.raw.lbpcascade_frontalface), mCascadeFile);

            }

            if(!mYNFile.exists()){
                copyInputStreamToFile(Context.getResources().openRawResource(R.raw.yunet), mYNFile);

            }

            if(!mSFFile.exists()){
                copyInputStreamToFile(Context.getResources().openRawResource(R.raw.face_recognizer_fast), mSFFile);

            }
            if(mJavaDetector==null||mJavaDetectorYN==null||mJavaRecognizeSF==null){
                mJavaDetectorYN = FaceDetectorYN.create(mYNFile.getAbsolutePath(),"",YNsize);
                mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                mJavaRecognizeSF = FaceRecognizerSF.create(mSFFile.getAbsolutePath(),"");
            }
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(featureList.isEmpty()){
            loadfeaturedata();
        }

    }
}
