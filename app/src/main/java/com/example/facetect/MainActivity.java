package com.example.facetect;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_PLAIN;
import static org.opencv.imgproc.Imgproc.LINE_4;
import static org.opencv.imgproc.Imgproc.calcHist;
import static org.opencv.imgproc.Imgproc.cvtColor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.FaceRecognizerSF;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

import com.example.facetect.bean.Features;
import com.example.facetect.bean.Image;
public class MainActivity extends CameraActivity implements CvCameraViewListener2, View.OnTouchListener {

    private static final String  TAG = "FaceDetect::Activity";
    private static final int OPEN_ALBUM_REQUESTCODE = 88;
    //调用摄像头
    private CameraBridgeViewBase mOpenCvCameraView;
    private List<Image> imageList = new ArrayList<>();
    private String SelectName;
    boolean IsDetect = false;
    boolean IstoDetectFrameChange = false;
    boolean IsDetectedFrameChange = false;
    Socket socket;     ;
    private mFaceDetector faceDetector;
    private Bitmap SelectImg;
    private Mat DetectFrame;

    //<editor-fold desc="工具方法">
    private String getPicPath(Uri uri){
        String[] picPathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, picPathColumns, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(picPathColumns[0]));
    }
    public void ToastInfo(String Info){
        Toast.makeText(MainActivity.this,Info,Toast.LENGTH_SHORT).show();

    }
    //根据路径获取图片
    private Bitmap getImgFromDesc(String path) {
        Bitmap bm = null;
        File file = new File(path);
        // 动态申请权限
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        final int REQUEST_CODE = 10001;

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取

            for (String permission : permissions) {
                //  GRANTED---授权  DINIED---拒绝
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
                }
            }
        }

        boolean permission_readStorage = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean permission_camera = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        Log.d("ImgActivity:", "getImageFromDesc: \n");
        Log.d("ImgActivity: ", "readPermission: " + permission_readStorage + "\n");
        Log.d("ImgActivity： ", "cameraPermission: " + permission_camera + "\n");
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //只请求图片宽高，不解析图片像素(请求图片属性但不申请内存，解析bitmap对象，该对象不占内存)
        options.inJustDecodeBounds = false;
        if(file.exists()) {
            bm = BitmapFactory.decodeFile(path,options);
        } else {
            Log.d("ImgActivity ", "getImgFromDesc: 该图片不存在！");
        }



        return bm;
    }
    //</editor-fold>

    //<editor-fold desc="生命周期">
    private TextView textView_log;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        initScreenMsg(7);

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d(TAG, "Creating and setting view");
        setContentView(R.layout.face_detect);

        mOpenCvCameraView = findViewById(R.id.javaCamera2View);
        Button selectFace = findViewById(R.id.button_select);
        Button DetectFace = findViewById(R.id.button_detect);
        textView_log = findViewById(R.id.log_content);
        faceDetector = new mFaceDetector();

        DetectFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsDetect =!IsDetect;
                if(IsDetect){
                    IstoDetectFrameChange = false;
                }

            }
        });
        selectFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick select photo " );
                Intent openAlbumIntent = new Intent(Intent.ACTION_PICK); //打开相册
                openAlbumIntent.setType("image/*");     //选择全部照片
                startActivityForResult(openAlbumIntent, OPEN_ALBUM_REQUESTCODE); //发送请求
                //initImages();
                heilick(view);

            }
        });
        //mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


    }



    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {

            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Socket socket = null;
//                        try {
//                            socket = new Socket("222.201.190.153",9001);
//
//                            OutputStream os = socket.getOutputStream();
//                            byte [] send = new  byte[1024];
//                            for (int i = 0; i < send.length; i++) {
//                                send[i]= ((byte) i);
//                            }
//
//                            Features features  = new Features();
//                            float [] floatsList = new float[]{(float) 0.145234534,(float) 0.22323};
//
//                            List<float []> floats = new ArrayList<>();
//                            floats.add(floatsList);
//                            os.write(send);
//                            //oos.writeObject(floatsList);
//                            byte [] rec = new  byte[1024];
//                            //os.write(send);
//
//                            InputStream is = socket.getInputStream();
//                            is.read(rec);
//                            //os.close();
//                            is.close();
//                            socket.close();
//
//                            for (int i = 0; i < rec.length; i++) {
//                                System.out.println((char) rec[i]);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        URL myurl = null;
//                        myurl = new URL("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fworld.chinadaily.com.cn%2Fimg%2Fattachement%2Fjpg%2Fsite1%2F20161022%2Ff8bc126d91a519756b700e.jpg&refer=http%3A%2F%2Fworld.chinadaily.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671071355&t=7b64cc3ef7c13ce10cf58fec9122284f");
//                        HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
//                        conn.connect();
//                        InputStream is = conn.getInputStream();//获得图片的数据流
//                        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
//                        Bitmap bmp = BitmapFactory.decodeStream( is );//读取图像数据
//                        int n = bmp.getByteCount();
//                        System.out.println(n);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();


            //加载器回调
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void onCameraViewStarted(int width, int height) {

    }

    public void onCameraViewStopped() {
    }


    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="屏幕打印">
    private void initScreenMsg(int _maxrow) {

        maxrow = _maxrow;
        msgs = new String[maxrow];
    }
    static String [] msgs ;
    int maxrow ;
    int tail = 1;
    public void addMessage(String msg){
        tail =  (tail+1)%maxrow;
        msgs[tail] = msg;
    }
    public String addonScreenMessage(String msg){
        tail =  (tail+1)%maxrow;
        msgs[tail] = msg;
        String all_msg ="";
        int it = (tail+1)%maxrow;
        while(it!=tail){
            all_msg+=(msgs[it]+"\n");
            it=(it+1)%maxrow;
        }
        return all_msg;
    }
    //</editor-fold>

    //返回图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_ALBUM_REQUESTCODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                String picPath = getPicPath(uri);
                SelectImg =getImgFromDesc(picPath);

                Toast.makeText(MainActivity.this,"图片路径："+picPath,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Opencv初始化回调
    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    /* Now enable camera view to start receiving frames */
                    //设置触摸监听器
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                    mOpenCvCameraView.enableView();

                    faceDetector.init(MainActivity.this,getApplicationContext());

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }

    };
    //弹出框
    public void heilick(View view  ) {
        //step2 获取view
        //popup_view 是自己写的弹出的页面
        View pupupView = getLayoutInflater().inflate(R.layout.set_name_view, null);

        //step3 创建一个PopupWindow,把view 放进去
        //new PopupWindow 这里用到的是4个参数的构造方法: (view,宽,高,是否获取焦点)
        //是否获取焦点:true  → 点击空白处,popupwindow 就消失了
        //ViewGroup.LayoutParams.WRAP_CONTENT 表示 popWindow 刚好包裹住popView
//        final PopupWindow popupWindow = new PopupWindow(pupupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final PopupWindow popupWindow = new PopupWindow(pupupView, 1000,  1000, true);
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bear));

        //step4 显示PopupWindow,设置PopupWindow的位置
        popupWindow.showAsDropDown(pupupView, 20,  20);
        //popupWindow.showAsDropDown(pupupView,view.getWidth(),-view.getHeight());

        // 对view 里的按钮进行操作
        EditText text = pupupView.findViewById(R.id.editTextTextPersonName);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                SelectName = text.getText().toString();
                if(SelectName!=""&&SelectName!="Name"&&SelectImg!=null){
                    if(faceDetector.SaveFeature(SelectImg,SelectName)){
                        ToastInfo("Save Person "+SelectName);
                    }
                    else{
                        ToastInfo("no detect person"+SelectName);
                    }
                    SelectImg = null;
                }

                Log.d(TAG, "onDismiss: ");
            }
        });

    }


    protected Mat detectImage(CvCameraViewFrame inputFrame){
        if(inputFrame==null){
            return new Mat();
        }

        long first = System.currentTimeMillis();
        //图像预处理
        Mat mRgba = new Mat();
        mRgba = faceDetector.preprocess(inputFrame.rgba());

        long second = System.currentTimeMillis();
        //人脸检测
        MatOfRect faces = faceDetector.FaceDetect(mRgba);

        long third = System.currentTimeMillis();
        //特征提取
        String name = "";
        Mat feature =faceDetector.FeatureExtract(mRgba,faces);
        long forth = System.currentTimeMillis();
        if(feature!=null){
            name = faceDetector.FeatureRecognize(feature);
        }

        Rect[] facesArray = faces.toArray();
        Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

        //Core.multiply(mRgba,new Scalar(2,2,2),mRgba);
        Core.transpose(mRgba,mRgba);
        Imgproc.putText(mRgba,name,new Point(50,450),FONT_HERSHEY_PLAIN,5,new Scalar(244,123,153),3,LINE_4,false);
        addonScreenMessage("detect " + name);

        long fifth = System.currentTimeMillis();

        long period1 = second - first;
        long period2 = third - second;
        long period3 = forth - third;
        long period4 =  fifth- forth;
        addonScreenMessage("预处理："+period1+"  人脸检测 "+period2+"  特征提取"+period3+"  特征匹配"+period4);
        Log.e(TAG, "预处理："+period1+"  人脸检测 "+period2+"  特征提取"+period3+"  特征匹配"+period4);

        return mRgba;
    }


    long deltaTime= 0;
    long lastFrameTime = 0;
    long CurrentTime = 0;
    long ScreenMessageGap;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


        CurrentTime =  System.currentTimeMillis();
        deltaTime = CurrentTime-lastFrameTime;
        lastFrameTime = CurrentTime;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textView_log!=null){

                    if(ScreenMessageGap>1000){
                        ScreenMessageGap=0;
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
                        textView_log.setText(addonScreenMessage(formatter.format(date)));
                    }
                    else{
                        ScreenMessageGap+=deltaTime;
                    }
                }
            }
        });
        if (IsDetect){
            if(IstoDetectFrameChange){
                return DetectFrame;
            }
            else{
                DetectFrame = detectImage(inputFrame);
                IstoDetectFrameChange = true;
                return DetectFrame;
            }

        }
        else{
            return inputFrame.rgba();
        }

    }
}
