package com.example.facetect.bean;

import org.opencv.core.Mat;

 public class faces_location {
    static private Mat faceslocation;

     public static void setFaceslocation(Mat faceslocation) {
         faces_location.faceslocation = faceslocation;
     }

     public static Mat getFaceslocation() {
         return faceslocation;
     }
 }
