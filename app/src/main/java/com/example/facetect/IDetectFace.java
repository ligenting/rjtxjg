package com.example.facetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import org.opencv.android.CameraActivity;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public interface IDetectFace {


    /**
     * 过程调用
     * @param start 开始入口模块
     * @param end 结束出口模块
     * @param objects 调用所需数据
     * @return 返回临时数据包
     */
    Object[] callFunction(int start ,int end,Object[] objects);

    /**
     * 初始化
     * @param RunningObject 当前类
     * @param context 上下文
     */
    void init(CameraActivity RunningObject, Context context);

    /**
     * 保存特征值
     * @param bitmap 从相册得到的图片
     * @param name 名字
     * @return 是否有人脸
     */
    boolean SaveFeature(Bitmap bitmap, String name);
}
