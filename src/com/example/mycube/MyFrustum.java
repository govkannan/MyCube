package com.example.mycube;

import android.opengl.Matrix;

public class MyFrustum {

   
    float left ;
     float right;
    final float bottom = -1.0f;
    final float top = 1.0f;
    final float near = 3.0f;
    final float far = 9.0f;
    
    public float[] projectMatrix = new float[16];
    
    public MyFrustum(int width, int height) {
    	float ratio = (float) width / height;
    	left = -ratio;
    	right = ratio;
//    	Matrix.frustumM(projectMatrix, 0, left, right, bottom, top, near, far);
    	Matrix.orthoM(projectMatrix, 0, left, right, bottom, top, near, far);
    	
    }
    
    public void init() {
    	Matrix.frustumM(projectMatrix, 0, left, right, bottom, top, near, far);
    }
    
    public float[] getPrjectionMatrix() {
    	return projectMatrix;
    }
 
    

}
