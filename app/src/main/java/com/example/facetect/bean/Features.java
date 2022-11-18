package com.example.facetect.bean;

import android.util.Pair;

import org.opencv.core.Mat;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Features implements Serializable {
    public Integer Size;
    private List<String> nameList;
    private List<float []> featuresList;
    public Features(){
        nameList = new ArrayList<>();
        featuresList = new ArrayList<>();
        Size =0;
    }
    public List<Pair<String, Mat>> GetFeatureList(){
        List<Pair<String, Mat>> res = new ArrayList<>();
        for (int i = 0; i < Size; i++) {
            Mat mat = new Mat();
            mat.put(0,0,featuresList.get(i));
            res.add(new Pair<>(nameList.get(i),mat));
        }
        return res;
    }
    public void add(String name,Mat feature){
        float [] floatList = new float[128];
        feature.get(0,0,floatList);
        nameList.add(name);
        featuresList.add(floatList);
        Size++;
    }
    public void add(Pair<String ,Mat> pair){
        float [] floatList = new float[128];
        pair.second.get(0,0,floatList);
        nameList.add(pair.first);
        featuresList.add(floatList);
        Size++;
    }

}
